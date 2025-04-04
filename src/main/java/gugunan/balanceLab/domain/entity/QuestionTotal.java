package gugunan.balanceLab.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "QUESTION_TOTAL", catalog = "balance_experiment")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionTotal {

    @Id
    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(name = "count_a", nullable = false)
    private Integer countA;

    @Column(name = "count_b", nullable = false)
    private Integer countB;

}
