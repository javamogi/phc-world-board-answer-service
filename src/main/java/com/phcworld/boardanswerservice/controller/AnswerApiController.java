package com.phcworld.boardanswerservice.controller;

import com.phcworld.boardanswerservice.controller.port.*;
import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.domain.port.AnswerRequest;
import com.phcworld.boardanswerservice.service.port.UserResponse;
import com.phcworld.boardanswerservice.utils.LocalDateTimeUtils;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
@Builder
public class AnswerApiController {
	
	private final AnswerService answerService;
	private final WebClientService webClientService;

	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "답변을 등록할 게시글 없음"),
			@ApiResponse(responseCode = "409", description = "DB에 저장할 때 UNIQUE 충돌"),
			@ApiResponse(responseCode = "404", description = "답변을 등록하는 회원 없음")
	})
	@PostMapping("")
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<AnswerResponse> register(@RequestBody AnswerRequest requestDto,
												  @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		webClientService.existBoard(token, requestDto);
		UserResponse user = webClientService.getUser(token, null);
		Answer answer = answerService.register(requestDto);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(AnswerResponse.of(answer, user));
	}

	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "요청한 답변 없음"),
	})
	@GetMapping("/{answerId}")
	public ResponseEntity<AnswerResponse> getAnswer(@PathVariable(name = "answerId") String answerId,
													@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		Answer answer = answerService.getAnswer(answerId);
		UserResponse user = webClientService.getUser(token, answer);
		return ResponseEntity
				.ok()
				.body(AnswerResponse.of(answer, user));
	}

	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "수정할 답변 없음"),
			@ApiResponse(responseCode = "403", description = "수정 권한 없음")
	})
	@PatchMapping("")
	public ResponseEntity<AnswerResponse> update(@RequestBody AnswerRequest requestDto,
												 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

		Answer answer = answerService.update(requestDto);
		UserResponse user = webClientService.getUser(token, answer);
		return ResponseEntity
				.ok()
				.body(AnswerResponse.of(answer, user));
	}

	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "삭제할 답변 없음"),
			@ApiResponse(responseCode = "403", description = "삭제 권한 없음")
	})
	@DeleteMapping("/{answerId}")
	public ResponseEntity<AnswerResponse> delete(@PathVariable(name = "answerId") String answerId,
												 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		Answer answer = answerService.delete(answerId);
		UserResponse user = webClientService.getUser(token, answer);
		return ResponseEntity
				.ok()
				.body(AnswerResponse.of(answer, user));
	}

	@GetMapping("/freeboards/{freeboardId}")
	public ResponseEntity<List<AnswerResponse>> getAnswersByBoardId(@PathVariable(name = "freeboardId") Long freeboardId,
																	@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
		List<Answer> answers = answerService.getAnswerList(freeboardId);

		Map<String, UserResponse> users = webClientService.getUsersMap(token, answers);
		List<AnswerResponse> result = answers.stream()
				.map(answer -> {
					return AnswerResponse.builder()
							.answerId(answer.getAnswerId())
							.writer(!users.isEmpty() ? users.get(answer.getWriterId()) : null)
							.contents(answer.getContents())
							.updatedDate(LocalDateTimeUtils.getTime(answer.getUpdateDate()))
							.boardId(answer.getFreeBoardId())
							.isDeleted(answer.isDeleted())
							.build();
				})
				.toList();
		return ResponseEntity
				.ok()
				.body(result);
	}

}
