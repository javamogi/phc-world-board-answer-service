package com.phcworld.boardanswerservice.repository;


import com.phcworld.boardanswerservice.domain.FreeBoardAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FreeBoardAnswerRepository extends JpaRepository<FreeBoardAnswer, Long> {
	List<FreeBoardAnswer> findByWriterId(String userId);
	List<FreeBoardAnswer> findByFreeBoardId(String boardId);

	Optional<FreeBoardAnswer> findByAnswerId(String answerId);
}
