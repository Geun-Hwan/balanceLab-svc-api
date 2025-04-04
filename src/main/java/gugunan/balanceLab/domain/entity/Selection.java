package gugunan.balanceLab.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "SELECTION", catalog = "balance_experiment")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Selection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "select_seq", nullable = false)
    private Integer selectSeq;

    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "choice_type", nullable = false)
    private String choiceType;

    @Column(name = "reward_point", nullable = true)
    private Integer rewardPoint;

    @CreationTimestamp
    @Column(name = "created_dtm", nullable = false)
    private LocalDateTime createdDtm;

    @UpdateTimestamp
    @Column(name = "updated_dtm", nullable = false)
    private LocalDateTime updatedDtm;

}
