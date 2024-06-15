package com.phcworld.boardanswerservice.infrastructure;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FreeBoardAnswerJpaRepository extends JpaRepository<FreeBoardAnswerEntity, Long> {
	List<FreeBoardAnswerEntity> findByWriterId(String userId);
	List<FreeBoardAnswerEntity> findByFreeBoardIdAndIsDeleted(Long boardId, boolean isDelete);

	Optional<FreeBoardAnswerEntity> findByAnswerId(String answerId);
}
