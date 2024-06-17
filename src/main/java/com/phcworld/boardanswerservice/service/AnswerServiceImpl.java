package com.phcworld.boardanswerservice.service;

import com.phcworld.boardanswerservice.domain.port.AnswerRequest;
import com.phcworld.boardanswerservice.controller.port.AnswerService;
import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.domain.Authority;
import com.phcworld.boardanswerservice.exception.model.*;
import com.phcworld.boardanswerservice.security.utils.SecurityUtil;
import com.phcworld.boardanswerservice.service.port.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Builder
public class AnswerServiceImpl implements AnswerService {
	
	private final AnswerRepository answerRepository;
	private final BoardProducer boardProducer;
	private final AnswerProducer answerProducer;
	private final LocalDateTimeHolder timeHolder;
	private final UuidHolder uuidHolder;

	@Override
	public Answer register(AnswerRequest request) {

//		String answerId = UUID.randomUUID().toString();
//		while(answerRepository.findByAnswerId(answerId).isPresent()){
//			answerId = UUID.randomUUID().toString();
//		}

		String userId = SecurityUtil.getCurrentMemberId();
		Answer answer = Answer.from(request, userId, uuidHolder, timeHolder);

//		boardProducer.send("board-topic", answer);

		return answerProducer.send("answers", answer);
	}

	@Override
	public Answer getAnswer(String answerId) {
		return answerRepository.findByAnswerId(answerId)
				.orElseThrow(NotFoundException::new);
	}

	@Override
	public Answer update(AnswerRequest request) {
		Answer answer = answerRepository.findByAnswerId(request.answerId())
				.orElseThrow(NotFoundException::new);
		if(answer.isDeleted()){
			throw new DeletedEntityException();
		}
		String userId = SecurityUtil.getCurrentMemberId();

		if(!answer.matchWriter(userId)){
			throw new ForbiddenException();
		}

		answer = answer.update(request.contents());

		return answerProducer.send("answers", answer);
	}

	@Override
	public Answer delete(String answerId) {
		Answer answer = answerRepository.findByAnswerId(answerId)
				.orElseThrow(NotFoundException::new);
		if(answer.isDeleted()){
			throw new DeletedEntityException();
		}
		String userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();
		if(!answer.matchWriter(userId) && authorities != Authority.ROLE_ADMIN) {
			throw new ForbiddenException();
		}
		answer = answer.delete();
		return answerProducer.send("answers", answer);
	}

	@Override
	public List<Answer> getAnswerList(Long boardId) {
		return answerRepository.findByFreeBoardIdAndIsDeleted(boardId, false);
	}

}
