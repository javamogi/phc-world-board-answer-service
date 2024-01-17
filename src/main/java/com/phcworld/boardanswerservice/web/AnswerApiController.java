package com.phcworld.boardanswerservice.web;

import com.phcworld.boardanswerservice.dto.AnswerRequestDto;
import com.phcworld.boardanswerservice.dto.AnswerResponseDto;
import com.phcworld.boardanswerservice.dto.SuccessResponseDto;
import com.phcworld.boardanswerservice.service.AnswerService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
public class AnswerApiController {
	
	private final AnswerService answerService;

	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "답변을 등록할 게시글 없음"),
			@ApiResponse(responseCode = "409", description = "DB에 저장할 때 UNIQUE 충돌"),
			// TO DO 수정
			@ApiResponse(responseCode = "404", description = "답변을 등록하는 회원 없음")
	})
	@PostMapping("")
	@ResponseStatus(value = HttpStatus.CREATED)
	public AnswerResponseDto register(@RequestBody AnswerRequestDto requestDto,
									  HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		return answerService.register(requestDto, token);
	}

	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "요청한 답변 없음"),
	})
	@GetMapping("/{answerId}")
	public AnswerResponseDto getFreeBoardAnswer(@PathVariable(name = "answerId") String answerId,
												HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		return answerService.getFreeBoardAnswer(answerId, token);
	}

	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "수정할 답변 없음"),
			@ApiResponse(responseCode = "403", description = "수정 권한 없음")
	})
	@PatchMapping("")
	public AnswerResponseDto updateFreeBoardAnswer(@RequestBody AnswerRequestDto requestDto,
												   HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		return answerService.updateFreeBoardAnswer(requestDto, token);
	}

	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "삭제할 답변 없음"),
			@ApiResponse(responseCode = "403", description = "삭제 권한 없음")
	})
	@DeleteMapping("/{answerId}")
	public SuccessResponseDto deleteFreeBoardAnswer(@PathVariable(name = "answerId") String answerId) {
		return answerService.deleteFreeBoardAnswer(answerId);
	}

	@GetMapping("/freeboards/{freeboardId}")
	public List<AnswerResponseDto> getFreeBoardAnswers(@PathVariable(name = "freeboardId") String freeboardId,
													   HttpServletRequest request){
		String token = request.getHeader("Authorization");
		return answerService.getFreeBoardAnswerList(freeboardId, token);
	}

}
