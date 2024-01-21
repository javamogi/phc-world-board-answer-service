package com.phcworld.boardanswerservice.service;

import com.phcworld.boardanswerservice.domain.Authority;
import com.phcworld.boardanswerservice.domain.FreeBoardAnswer;
import com.phcworld.boardanswerservice.dto.AnswerRequestDto;
import com.phcworld.boardanswerservice.dto.AnswerResponseDto;
import com.phcworld.boardanswerservice.dto.SuccessResponseDto;
import com.phcworld.boardanswerservice.dto.UserResponseDto;
import com.phcworld.boardanswerservice.exception.model.DuplicationException;
import com.phcworld.boardanswerservice.exception.model.NotFoundException;
import com.phcworld.boardanswerservice.exception.model.NotMatchUserException;
import com.phcworld.boardanswerservice.exception.model.UnauthorizedException;
import com.phcworld.boardanswerservice.messagequeue.AnswerProducer;
import com.phcworld.boardanswerservice.messagequeue.KafkaProducer;
import com.phcworld.boardanswerservice.repository.FreeBoardAnswerRepository;
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
	
	private final FreeBoardAnswerRepository freeBoardAnswerRepository;
	private final KafkaProducer kafkaProducer;
	private final AnswerProducer answerProducer;
	private final WebclientService webclientService;


	public AnswerResponseDto register(AnswerRequestDto request, String token) {

		boolean existedFreeBoard = webclientService.existFreeBoard(request, token);

		if (!existedFreeBoard) {
			throw new NotFoundException();
		}
		String answerId = UUID.randomUUID().toString();
		boolean exist = freeBoardAnswerRepository.findByAnswerId(answerId)
				.isPresent();
		if(exist){
			throw new DuplicationException();
		}

		String userId = SecurityUtil.getCurrentMemberId();
		FreeBoardAnswer freeBoardAnswer = FreeBoardAnswer.builder()
				.answerId(answerId)
				.writerId(userId)
				.freeBoardId(request.boardId())
				.contents(request.contents())
				.updateDate(LocalDateTime.now())
				.build();
//		freeBoardAnswerRepository.save(freeBoardAnswer);

		kafkaProducer.send("board-topic", freeBoardAnswer);
		answerProducer.send("answers", freeBoardAnswer);

		UserResponseDto user = webclientService.getUserResponseDto(token, freeBoardAnswer);

		return AnswerResponseDto.builder()
				.answerId(answerId)
				.writer(user)
				.contents(freeBoardAnswer.getContents())
				.updatedDate(freeBoardAnswer.getFormattedUpdateDate())
				.build();
	}

	public AnswerResponseDto getFreeBoardAnswer(String answerId, String token) {
		FreeBoardAnswer freeBoardAnswer = freeBoardAnswerRepository.findByAnswerId(answerId)
				.orElseThrow(NotFoundException::new);

		UserResponseDto user = webclientService.getUserResponseDto(token, freeBoardAnswer);

		return AnswerResponseDto.builder()
				.answerId(freeBoardAnswer.getAnswerId())
				.writer(user)
				.contents(freeBoardAnswer.getContents())
				.updatedDate(freeBoardAnswer.getFormattedUpdateDate())
				.build();
	}

	public AnswerResponseDto updateFreeBoardAnswer(AnswerRequestDto request, String token) {
		FreeBoardAnswer answer = freeBoardAnswerRepository.findByAnswerId(request.answerId())
				.orElseThrow(NotFoundException::new);
		String userId = SecurityUtil.getCurrentMemberId();

		if(!answer.isSameWriter(userId)){
			throw new NotMatchUserException();
		}

		answer.update(request.contents());
		answerProducer.send("answers", answer);

		UserResponseDto user = webclientService.getUserResponseDto(token, answer);

		return  AnswerResponseDto.builder()
				.answerId(answer.getAnswerId())
				.writer(user)
				.contents(answer.getContents())
				.updatedDate(answer.getFormattedUpdateDate())
				.build();
	}

	public SuccessResponseDto deleteFreeBoardAnswer(String answerId) {
		FreeBoardAnswer freeBoardAnswer = freeBoardAnswerRepository.findByAnswerId(answerId)
				.orElseThrow(NotFoundException::new);
		String userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();
		if(!freeBoardAnswer.isSameWriter(userId) && authorities != Authority.ROLE_ADMIN) {
			throw new UnauthorizedException();
		}

//		freeBoardAnswerRepository.delete(freeBoardAnswer);

		answerProducer.send("answers", freeBoardAnswer);
		
		return SuccessResponseDto.builder()
				.message("삭제성공")
				.statusCode(200)
				.build();
	}

	public List<AnswerResponseDto> getFreeBoardAnswerList(String boardId, String token) {
		List<FreeBoardAnswer> freeBoardAnswers = freeBoardAnswerRepository.findByFreeBoardId(boardId);

		List<String> userIds = freeBoardAnswers.stream()
				.map(FreeBoardAnswer::getWriterId)
				.distinct()
				.toList();

		Map<String, UserResponseDto> users = webclientService.getUserResponseDtoMap(token, userIds);

		UserResponseDto user = UserResponseDto.builder()
				.email("")
				.name("")
				.createDate("")
				.profileImage("")
				.userId("")
				.build();

		return freeBoardAnswers.stream()
				.map(a -> {
					return AnswerResponseDto.builder()
							.answerId(a.getAnswerId())
							.writer(users.isEmpty() ? user : users.get(a.getWriterId()))
							.contents(a.getContents())
							.updatedDate(a.getFormattedUpdateDate())
							.build();
				})
				.toList();
	}

}
