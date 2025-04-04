package gugunan.balanceLab.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "POINT_HISTORY", catalog = "balance_experiment")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PointHistory {

    @Id
    @Column(name = "point_seq", nullable = false)
    private Long pointSeq;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "point_amount", nullable = false)
    private Integer pointAmount;

    @Column(name = "point_type_cd", nullable = false)
    private String pointTypeCd;

    @Column(name = "reason", nullable = false)
    private String reason;

    @CreationTimestamp
    @Column(name = "created_dtm", nullable = true)
    private LocalDateTime createdDtm;

}
