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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerService {
	
	private final FreeBoardAnswerRepository freeBoardAnswerRepository;
	private final Environment env;
	private final WebClient.Builder webClient;
	private final KafkaProducer kafkaProducer;
	private final AnswerProducer answerProducer;
	private final CircuitBreakerFactory circuitBreakerFactory;


	public AnswerResponseDto register(AnswerRequestDto request, String token) {

		log.info("Before call boards microservice");
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
		boolean existedFreeBoard = Boolean.TRUE.equals(
				circuitBreaker.run(() ->
				webClient.build()
				.mutate().baseUrl("http://localhost:8080/boards")
//				.mutate().baseUrl(env.getProperty("board_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/{id}/exist")
						.build(request.boardId()))
				.header("Authorization", token)
				.retrieve()
				.bodyToMono(Boolean.class)
				.block(),
						throwable -> false)
		);
		log.info("After call boards microservice");

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
				.createDate(LocalDateTime.now())
				.updateDate(LocalDateTime.now())
				.build();
//		freeBoardAnswerRepository.save(freeBoardAnswer);

		kafkaProducer.send("board-topic", freeBoardAnswer);
		answerProducer.send("answers", freeBoardAnswer);

		log.info("Before call users microservice");
		UserResponseDto user = circuitBreaker.run(
				() -> getUserResponseDto(token, freeBoardAnswer.getWriterId()),
				throwable -> UserResponseDto.builder()
						.email("")
						.name("")
						.createDate("")
						.profileImage("")
						.userId("")
						.build());
		log.info("After called users microservice");

		if(user == null){
			throw new NotFoundException();
		}

		return AnswerResponseDto.builder()
				.answerId(answerId)
				.writer(user)
				.contents(freeBoardAnswer.getContents())
				.updatedDate(freeBoardAnswer.getFormattedUpdateDate())
				.build();
	}

	@Nullable
	private UserResponseDto getUserResponseDto(String token, String userId) {
		return webClient.build()
				.mutate().baseUrl("http://localhost:8080/users")
//				.mutate().baseUrl(env.getProperty("user_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/{id}")
						.build(userId))
				.header("Authorization", token)
				.retrieve()
				.bodyToMono(UserResponseDto.class)
				.block();
	}

	public AnswerResponseDto getFreeBoardAnswer(String answerId, String token) {
		FreeBoardAnswer freeBoardAnswer = freeBoardAnswerRepository.findByAnswerId(answerId)
				.orElseThrow(NotFoundException::new);

		log.info("Before call users microservice");
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
		UserResponseDto user = circuitBreaker.run(
				() -> getUserResponseDto(token, freeBoardAnswer.getWriterId()),
				throwable -> UserResponseDto.builder()
						.email("")
						.name("")
						.createDate("")
						.profileImage("")
						.userId("")
						.build());
		log.info("After called users microservice");

		return AnswerResponseDto.builder()
				.answerId(freeBoardAnswer.getAnswerId())
				.writer(user)
				.contents(freeBoardAnswer.getContents())
				.updatedDate(freeBoardAnswer.getFormattedUpdateDate())
				.build();
	}

	@Transactional
	public AnswerResponseDto updateFreeBoardAnswer(AnswerRequestDto request, String token) {
		FreeBoardAnswer answer = freeBoardAnswerRepository.findByAnswerId(request.answerId())
				.orElseThrow(NotFoundException::new);
		String userId = SecurityUtil.getCurrentMemberId();

		if(!answer.isSameWriter(userId)){
			throw new NotMatchUserException();
		}

		answer.update(request.contents());

		log.info("Before call users microservice");
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
		UserResponseDto user = circuitBreaker.run(
				() -> getUserResponseDto(token, answer.getWriterId()),
				throwable -> UserResponseDto.builder()
						.email("")
						.name("")
						.createDate("")
						.profileImage("")
						.userId("")
						.build());
		log.info("After called users microservice");

		return  AnswerResponseDto.builder()
				.answerId(answer.getAnswerId())
				.writer(user)
				.contents(answer.getContents())
				.updatedDate(answer.getFormattedUpdateDate())
				.build();
	}

	@Transactional
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

	public List<AnswerResponseDto> getFreeBoardAnswerList(String boardId, String token) {
		List<FreeBoardAnswer> freeBoardAnswers = freeBoardAnswerRepository.findByFreeBoardId(boardId);

		List<String> userIds = freeBoardAnswers.stream()
				.map(FreeBoardAnswer::getWriterId)
				.distinct()
				.toList();

		log.info("Before call users microservice");
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
		Map<String, UserResponseDto> users = circuitBreaker.run(
				() -> getUserResponseDtoMap(token, userIds),
				throwable -> new HashMap<>());
		log.info("After called users microservice");

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

	@Nullable
	private Map<String, UserResponseDto> getUserResponseDtoMap(String token, List<String> userIds) {
		return webClient.build()
				.mutate().baseUrl("http://localhost:8080/users")
//				.mutate().baseUrl(env.getProperty("user_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("")
						.queryParam("userIds", userIds)
						.build())
				.header(HttpHeaders.AUTHORIZATION, token)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<Map<String, UserResponseDto>>() {})
				.block();
	}

}