package com.practicetec.book.model.entity;

import com.practicetec.book.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Feedback extends BaseEntity {

    private Double note;
    private String comment;

    @ManyToOne()
    @JoinColumn(name = "book_id")
    private Book book;
}
