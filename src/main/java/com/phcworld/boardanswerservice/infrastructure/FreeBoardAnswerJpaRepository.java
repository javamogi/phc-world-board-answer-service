package com.phcworld.boardanswerservice.infrastructure;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FreeBoardAnswerJpaRepository extends JpaRepository<FreeBoardAnswerEntity, Long> {
	List<FreeBoardAnswerEntity> findByWriterId(String userId);
	List<FreeBoardAnswerEntity> findByFreeBoardId(String boardId);

	Optional<FreeBoardAnswerEntity> findByAnswerId(String answerId);
}
