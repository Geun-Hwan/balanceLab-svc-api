package gugunan.balanceLab.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PREDICT_TOTAL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PredictTotal {

    @Id
    @Column(name = "predict_id", nullable = false)
    private String predictId;

    @Column(name = "count_a")
    private Integer countA;

    @Column(name = "count_b")
    private Integer countB;

    @Column(name = "count_c")
    private Integer countC;

    @Column(name = "sum_point_a", nullable = false)
    private Integer sumPointA;

    @Column(name = "sum_point_b", nullable = false)
    private Integer sumPointB;

    @Column(name = "sum_point_c", nullable = false)
    private Integer sumPointC;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;

    @Column(name = "payout_a", precision = 4, scale = 2)
    private BigDecimal payoutA;

    @Column(name = "payout_b", precision = 4, scale = 2)
    private BigDecimal payoutB;

    @Column(name = "payout_c", precision = 4, scale = 2)
    private BigDecimal payoutC;

    @Column(name = "winner", length = 1)
    private String winner;

    @Column(name = "last_update_user_id", nullable = true)
    private String lastUpdateUserId;
}
