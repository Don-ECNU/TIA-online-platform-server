package com.scsse.workflow.entity.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@Data
@ToString
@Entity
@NoArgsConstructor
@Table(name = "question")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer"})
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Column
    private String description;

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }

    @OneToMany(mappedBy = "question")
  	@JsonBackReference(value = "question.question")
      private Set<Answer> answers = new HashSet<>();
}