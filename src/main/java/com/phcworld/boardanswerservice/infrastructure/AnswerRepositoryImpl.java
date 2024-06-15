package com.phcworld.boardanswerservice.infrastructure;

import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.service.port.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AnswerRepositoryImpl implements AnswerRepository {

    private final FreeBoardAnswerJpaRepository freeBoardAnswerJpaRepository;

    @Override
    public List<Answer> findByWriterId(String userId) {
        return freeBoardAnswerJpaRepository.findByWriterId(userId)
                .stream()
                .map(FreeBoardAnswerEntity::toModel)
                .toList();
    }

    @Override
    public List<Answer> findByFreeBoardIdAndIsDeleted(Long boardId, boolean isDelete) {
        return freeBoardAnswerJpaRepository.findByFreeBoardIdAndIsDeleted(boardId, isDelete)
                .stream()
                .map(FreeBoardAnswerEntity::toModel)
                .toList();
    }

    @Override
    public Optional<Answer> findByAnswerId(String answerId) {
        return freeBoardAnswerJpaRepository.findByAnswerId(answerId)
                .map(FreeBoardAnswerEntity::toModel);
    }

    @Override
    public Answer save(Answer answer) {
        return freeBoardAnswerJpaRepository.save(FreeBoardAnswerEntity.from(answer)).toModel();
    }
}
