package com.phcworld.boardanswerservice.domain;

import com.phcworld.boardanswerservice.utils.LocalDateTimeUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
public class FreeBoardAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String answerId;

	@Column(nullable = false)
	private String writerId;

	@Column(nullable = false)
	private String freeBoardId;
	
	@Lob
	private String contents;

	@CreatedDate
	private LocalDateTime createDate;
	
	@LastModifiedDate
	private LocalDateTime updateDate;
	
	public String getFormattedCreateDate() {
		return LocalDateTimeUtils.getTime(createDate);
	}
	
	public String getFormattedUpdateDate() {
		return LocalDateTimeUtils.getTime(updateDate);
	}

	public boolean isSameWriter(String userId) {
		return this.writerId.equals(userId);
	}

	public void update(String contents) {
		this.contents = contents;
	}

}
