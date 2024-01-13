package com.phcworld.boardanswerservice.service;

import com.phcworld.boardanswerservice.domain.Authority;
import com.phcworld.boardanswerservice.domain.FreeBoardAnswer;
import com.phcworld.boardanswerservice.dto.FreeBoardAnswerRequestDto;
import com.phcworld.boardanswerservice.dto.FreeBoardAnswerResponseDto;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class FreeBoardAnswerService {
	
	private final FreeBoardAnswerRepository freeBoardAnswerRepository;
	private final Environment env;
	private final WebClient.Builder webClient;
	private final KafkaProducer kafkaProducer;
	private final AnswerProducer answerProducer;


	public FreeBoardAnswerResponseDto register(FreeBoardAnswerRequestDto request, String token) {

		boolean existedFreeBoard = Boolean.TRUE.equals(webClient.build()
				.mutate().baseUrl(env.getProperty("board_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/{id}/exist")
						.build(request.boardId()))
				.header("Authorization", token)
				.retrieve()
				.bodyToMono(Boolean.class)
				.block());
		if (!existedFreeBoard) {
			throw new NotFoundException();
		}
		String answerId = UUID.randomUUID().toString();
		freeBoardAnswerRepository.findByAnswerId(answerId)
				.orElseThrow(DuplicationException::new);

		String userId = SecurityUtil.getCurrentMemberId();
		FreeBoardAnswer freeBoardAnswer = FreeBoardAnswer.builder()
				.answerId(answerId)
				.writerId(userId)
				.freeBoardId(request.boardId())
				.contents(request.contents())
				.createDate(LocalDateTime.now())
				.updateDate(LocalDateTime.now())
				.build();
//		freeBoardAnswerRepository.save(freeBoardAnswer);

		kafkaProducer.send("board-topic", freeBoardAnswer);
		answerProducer.send("answers", freeBoardAnswer);

		UserResponseDto user = getUserResponseDto(token, freeBoardAnswer);

		if(user == null){
			throw new NotFoundException();
		}

		return FreeBoardAnswerResponseDto.builder()
				.answerId(answerId)
				.writer(user)
				.contents(freeBoardAnswer.getContents())
				.updatedDate(freeBoardAnswer.getFormattedUpdateDate())
				.build();
	}

	@Nullable
	private UserResponseDto getUserResponseDto(String token, FreeBoardAnswer freeBoardAnswer) {
		return webClient.build()
				.mutate().baseUrl(env.getProperty("user_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/{id}")
						.build(freeBoardAnswer.getWriterId()))
				.header("Authorization", token)
				.retrieve()
				.bodyToMono(UserResponseDto.class)
				.block();
	}

	public FreeBoardAnswerResponseDto getFreeBoardAnswer(String answerId, String token) {
		FreeBoardAnswer freeBoardAnswer = freeBoardAnswerRepository.findByAnswerId(answerId)
				.orElseThrow(NotFoundException::new);

		UserResponseDto user = getUserResponseDto(token, freeBoardAnswer);

		return FreeBoardAnswerResponseDto.builder()
				.answerId(freeBoardAnswer.getAnswerId())
				.writer(user)
				.contents(freeBoardAnswer.getContents())
				.updatedDate(freeBoardAnswer.getFormattedUpdateDate())
				.build();
	}
	
	public FreeBoardAnswerResponseDto updateFreeBoardAnswer(FreeBoardAnswerRequestDto request, String token) {
		FreeBoardAnswer answer = freeBoardAnswerRepository.findByAnswerId(request.answerId())
				.orElseThrow(NotFoundException::new);
		String userId = SecurityUtil.getCurrentMemberId();

		if(!answer.isSameWriter(userId)){
			throw new NotMatchUserException();
		}

		answer.update(request.contents());

		UserResponseDto user = getUserResponseDto(token, answer);

		return  FreeBoardAnswerResponseDto.builder()
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

		freeBoardAnswerRepository.delete(freeBoardAnswer);
		
		return SuccessResponseDto.builder()
				.message("삭제성공")
				.statusCode(200)
				.build();
	}

	public List<FreeBoardAnswerResponseDto> getFreeBoardAnswerList(String boardId, String token) {
		List<FreeBoardAnswer> freeBoardAnswers = freeBoardAnswerRepository.findByFreeBoardId(boardId);

		List<String> userIds = freeBoardAnswers.stream()
				.map(FreeBoardAnswer::getWriterId)
				.distinct()
				.toList();

		Mono<Map<String, UserResponseDto>> response = getUserResponseDtoList(token, userIds);

		Map<String, UserResponseDto> users = response.block();

		return freeBoardAnswers.stream()
				.map(a -> {
					return FreeBoardAnswerResponseDto.builder()
							.answerId(a.getAnswerId())
							.writer(users.get(a.getWriterId()))
							.contents(a.getContents())
							.updatedDate(a.getFormattedUpdateDate())
							.build();
				})
				.toList();
	}

	@NotNull
	private Mono<Map<String, UserResponseDto>> getUserResponseDtoList(String token, List<String> userIds) {
		return webClient.build()
				.mutate().baseUrl(env.getProperty("user_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("")
						.queryParam("userIds", userIds)
						.build())
				.header(HttpHeaders.AUTHORIZATION, token)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<Map<String, UserResponseDto>>() {});
	}

}
