package com.phcworld.boardanswerservice.web;

import com.phcworld.boardanswerservice.dto.AnswerRequestDto;
import com.phcworld.boardanswerservice.dto.AnswerResponseDto;
import com.phcworld.boardanswerservice.dto.SuccessResponseDto;
import com.phcworld.boardanswerservice.service.AnswerService;
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
	
	@PostMapping("")
	@ResponseStatus(value = HttpStatus.CREATED)
	public AnswerResponseDto register(@RequestBody AnswerRequestDto requestDto,
									  HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		return answerService.register(requestDto, token);
	}
	
	@GetMapping("/{answerId}")
	public AnswerResponseDto getFreeBoardAnswer(@PathVariable(name = "answerId") String answerId,
												HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		return answerService.getFreeBoardAnswer(answerId, token);
	}
	
	@PatchMapping("")
	public AnswerResponseDto updateFreeBoardAnswer(@RequestBody AnswerRequestDto requestDto,
												   HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		return answerService.updateFreeBoardAnswer(requestDto, token);
	}
	
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
