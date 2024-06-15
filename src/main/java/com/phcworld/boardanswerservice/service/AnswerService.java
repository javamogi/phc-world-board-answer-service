package com.phcworld.boardanswerservice.service;

import com.phcworld.boardanswerservice.domain.Authority;
import com.phcworld.boardanswerservice.infrastructure.FreeBoardAnswerEntity;
import com.phcworld.boardanswerservice.controller.port.AnswerRequest;
import com.phcworld.boardanswerservice.controller.port.AnswerResponse;
import com.phcworld.boardanswerservice.controller.port.SuccessResponseDto;
import com.phcworld.boardanswerservice.service.port.UserResponse;
import com.phcworld.boardanswerservice.exception.model.NotFoundException;
import com.phcworld.boardanswerservice.exception.model.NotMatchUserException;
import com.phcworld.boardanswerservice.exception.model.UnauthorizedException;
import com.phcworld.boardanswerservice.messagequeue.AnswerProducerImpl;
import com.phcworld.boardanswerservice.messagequeue.BoardProducerImpl;
import com.phcworld.boardanswerservice.infrastructure.FreeBoardAnswerJpaRepository;
import com.phcworld.boardanswerservice.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerService {
	
	private final FreeBoardAnswerJpaRepository freeBoardAnswerJpaRepository;
	private final BoardProducerImpl kafkaProducer;
	private final AnswerProducerImpl answerProducer;
	private final WebclientService webclientService;


	public AnswerResponse register(AnswerRequest request, String token) {

		boolean existedFreeBoard = webclientService.existFreeBoard(request, token);

		if (!existedFreeBoard) {
			throw new NotFoundException();
		}
		String answerId = UUID.randomUUID().toString();
		while(freeBoardAnswerJpaRepository.findByAnswerId(answerId).isPresent()){
			answerId = UUID.randomUUID().toString();
		}

		String userId = SecurityUtil.getCurrentMemberId();
		FreeBoardAnswerEntity freeBoardAnswerEntity = FreeBoardAnswerEntity.builder()
				.answerId(answerId)
				.writerId(userId)
				.freeBoardId(request.boardId())
				.contents(request.contents())
				.updateDate(LocalDateTime.now())
				.build();
//		freeBoardAnswerRepository.save(freeBoardAnswer);

		kafkaProducer.send("board-topic", freeBoardAnswerEntity);
		answerProducer.send("answers", freeBoardAnswerEntity);

		UserResponse user = webclientService.getUserResponseDto(token, freeBoardAnswerEntity);

		return AnswerResponse.builder()
				.answerId(answerId)
				.writer(user)
				.contents(freeBoardAnswerEntity.getContents())
				.updatedDate(freeBoardAnswerEntity.getFormattedUpdateDate())
				.build();
	}

	public AnswerResponse getFreeBoardAnswer(String answerId, String token) {
		FreeBoardAnswerEntity freeBoardAnswerEntity = freeBoardAnswerJpaRepository.findByAnswerId(answerId)
				.orElseThrow(NotFoundException::new);

		UserResponse user = webclientService.getUserResponseDto(token, freeBoardAnswerEntity);

		return AnswerResponse.builder()
				.answerId(freeBoardAnswerEntity.getAnswerId())
				.writer(user)
				.contents(freeBoardAnswerEntity.getContents())
				.updatedDate(freeBoardAnswerEntity.getFormattedUpdateDate())
				.build();
	}

	public AnswerResponse updateFreeBoardAnswer(AnswerRequest request, String token) {
		FreeBoardAnswerEntity answer = freeBoardAnswerJpaRepository.findByAnswerId(request.answerId())
				.orElseThrow(NotFoundException::new);
		String userId = SecurityUtil.getCurrentMemberId();

		if(!answer.isSameWriter(userId)){
			throw new NotMatchUserException();
		}

		answer.update(request.contents());
		answerProducer.send("answers", answer);

		UserResponse user = webclientService.getUserResponseDto(token, answer);

		return  AnswerResponse.builder()
				.answerId(answer.getAnswerId())
				.writer(user)
				.contents(answer.getContents())
				.updatedDate(answer.getFormattedUpdateDate())
				.build();
	}

	public SuccessResponseDto deleteFreeBoardAnswer(String answerId) {
		FreeBoardAnswerEntity freeBoardAnswerEntity = freeBoardAnswerJpaRepository.findByAnswerId(answerId)
				.orElseThrow(NotFoundException::new);
		String userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();
		if(!freeBoardAnswerEntity.isSameWriter(userId) && authorities != Authority.ROLE_ADMIN) {
			throw new UnauthorizedException();
		}

//		freeBoardAnswerRepository.delete(freeBoardAnswer);

		answerProducer.send("answers", freeBoardAnswerEntity);
		
		return SuccessResponseDto.builder()
				.message("삭제성공")
				.statusCode(200)
				.build();
	}

	public List<AnswerResponse> getFreeBoardAnswerList(String boardId, String token) {
		List<FreeBoardAnswerEntity> freeBoardAnswerEntities = freeBoardAnswerJpaRepository.findByFreeBoardId(boardId);

		List<String> userIds = freeBoardAnswerEntities.stream()
				.map(FreeBoardAnswerEntity::getWriterId)
				.distinct()
				.toList();

		Map<String, UserResponse> users = webclientService.getUserResponseDtoMap(token, userIds);

		UserResponse user = UserResponse.builder()
				.email("")
				.name("")
				.createDate("")
				.profileImage("")
				.userId("")
				.build();

		return freeBoardAnswerEntities.stream()
				.map(a -> {
					return AnswerResponse.builder()
							.answerId(a.getAnswerId())
							.writer(users.isEmpty() ? user : users.get(a.getWriterId()))
							.contents(a.getContents())
							.updatedDate(a.getFormattedUpdateDate())
							.build();
				})
				.toList();
	}

}
