package com.phcworld.boardanswerservice.web;

import com.phcworld.boardanswerservice.dto.FreeBoardAnswerRequestDto;
import com.phcworld.boardanswerservice.dto.FreeBoardAnswerResponseDto;
import com.phcworld.boardanswerservice.dto.SuccessResponseDto;
import com.phcworld.boardanswerservice.service.FreeBoardAnswerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
public class FreeBoardAnswerApiController {
	
	private final FreeBoardAnswerService freeBoardAnswerService;
	
	@PostMapping("")
	@ResponseStatus(value = HttpStatus.CREATED)
	public FreeBoardAnswerResponseDto register(@RequestBody FreeBoardAnswerRequestDto requestDto,
											   HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		return freeBoardAnswerService.register(requestDto, token);
	}
	
	@GetMapping("/{id}")
	public FreeBoardAnswerResponseDto getFreeBoardAnswer(@PathVariable(name = "id") Long id,
														 HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		return freeBoardAnswerService.getFreeBoardAnswer(id, token);
	}
	
	@PatchMapping("")
	public FreeBoardAnswerResponseDto updateFreeBoardAnswer(@RequestBody FreeBoardAnswerRequestDto requestDto,
															HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		return freeBoardAnswerService.updateFreeBoardAnswer(requestDto, token);
	}
	
	@DeleteMapping("/{id}")
	public SuccessResponseDto deleteFreeBoardAnswer(@PathVariable(name = "id") Long id) {
		return freeBoardAnswerService.deleteFreeBoardAnswer(id);
	}

	@GetMapping("/freeboards/{freeboardId}")
	public List<FreeBoardAnswerResponseDto> getFreeBoardAnswers(@PathVariable(name = "freeboardId") Long freeboardId,
																HttpServletRequest request){
		String token = request.getHeader("Authorization");
		return freeBoardAnswerService.getFreeBoardAnswerList(freeboardId, token);
	}

}
