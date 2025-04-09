package gugunan.balanceLab.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import gugunan.balanceLab.domain.idclass.PredictParticipationId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PREDICT_PARTICIPATION")
@IdClass(PredictParticipationId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PredictParticipation {

    @Id
    @Column(name = "predict_id", nullable = false)
    private String predictId;

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "choice_type", nullable = false, length = 1)
    private String choiceType; // "A", "B", "C"

    @Column(name = "bet_point", nullable = false)
    private Integer betPoint;

    @Column(name = "reward_point")
    private Integer rewardPoint;

    @CreationTimestamp
    @Column(name = "created_dtm", nullable = false)
    private LocalDateTime createdDtm;

    @UpdateTimestamp
    @Column(name = "updated_dtm", nullable = false)
    private LocalDateTime updatedDtm;

}
