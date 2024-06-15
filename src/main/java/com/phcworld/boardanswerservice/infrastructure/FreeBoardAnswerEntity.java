package com.phcworld.boardanswerservice.infrastructure;

import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.utils.LocalDateTimeUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "answers",
		indexes = {@Index(name = "idx__writer_id_create_date", columnList = "writer_id, createDate"),
		@Index(name = "idx__free_board_id_create_date", columnList = "free_board_id, createDate")})
@DynamicUpdate
public class FreeBoardAnswerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String answerId;

	@Column(nullable = false)
	private String writerId;

	@Column(nullable = false)
	private Long freeBoardId;
	
	@Lob
	@Column(nullable = false)
	private String contents;

	@Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP()")
	private LocalDateTime createDate;

	@Column(nullable = false)
	private LocalDateTime updateDate;

	@ColumnDefault("false")
	@Column(nullable = false)
	private Boolean isDeleted;

	public static FreeBoardAnswerEntity from(Answer answer) {
		return FreeBoardAnswerEntity.builder()
				.answerId(answer.getAnswerId())
				.writerId(answer.getWriterId())
				.freeBoardId(answer.getFreeBoardId())
				.contents(answer.getContents())
				.createDate(answer.getCreateDate())
				.updateDate(answer.getUpdateDate())
				.isDeleted(answer.isDeleted())
				.build();
	}

	public Answer toModel() {
		return Answer.builder()
				.id(id)
				.answerId(answerId)
				.writerId(writerId)
				.freeBoardId(freeBoardId)
				.contents(contents)
				.createDate(createDate)
				.updateDate(updateDate)
				.isDeleted(isDeleted)
				.build();
	}
}
