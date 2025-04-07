package gugunan.balanceLab.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import gugunan.balanceLab.domain.CustomGeneratedId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "PREDICT", catalog = "balance_experiment")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Predict {

    @CustomGeneratedId(method = "PREDICT")
    @Id
    @Column(name = "predict_id", nullable = false)
    private String predictId;

    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "en_title")
    private String enTitle;

    @Column(name = "option_a", nullable = false)
    private String optionA;

    @Column(name = "en_option_a")
    private String enOptionA;

    @Column(name = "option_b", nullable = false)
    private String optionB;

    @Column(name = "en_option_b")
    private String enOptionB;

    @Column(name = "option_c")
    private String optionC;

    @Column(name = "en_option_c")
    private String enOptionC;

    @Column(name = "str_dtm", nullable = false)
    private LocalDateTime strDtm;

    @Column(name = "end_dtm", nullable = false)
    private LocalDateTime endDtm;

    @Column(name = "question_status_cd", nullable = false)
    private String questionStatusCd;

    @Column(name = "del_yn", nullable = false)
    @ColumnDefault("0")
    private Boolean delYn;

    @Column(name = "create_user_id", nullable = false)
    private String createUserId;

    @Column(name = "update_user_id", nullable = false)
    private String updateUserId;

    @CreationTimestamp
    @Column(name = "created_dtm", nullable = false)
    private LocalDateTime createdDtm;

    @UpdateTimestamp
    @Column(name = "updated_dtm", nullable = false)
    private LocalDateTime updatedDtm;

}
