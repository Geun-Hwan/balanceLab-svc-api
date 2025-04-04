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

@Table(name = "MONTHLY_COUNTS", catalog = "balance_experiment")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MonthlyCounts {

    @Id
    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(name = "count", nullable = true)
    private Integer count;

}
