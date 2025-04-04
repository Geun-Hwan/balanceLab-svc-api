package gugunan.balanceLab.domain.entity;

import java.time.LocalDate;
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

@Table(name = "QUESTION", catalog = "balance_experiment")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Question {

    @CustomGeneratedId(method = "QUESTION")
    @Id
    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "choice_a", nullable = false)
    private String choiceA;

    @Column(name = "choice_b", nullable = false)
    private String choiceB;

    @Column(name = "en_title", nullable = true)
    private String enTitle;

    @Column(name = "en_choice_a", nullable = true)
    private String enChoiceA;

    @Column(name = "en_choice_b", nullable = true)
    private String enChoiceB;

    @Column(name = "img_url_a", nullable = true)
    private String imgUrlA;

    @Column(name = "img_url_b", nullable = true)
    private String imgUrlB;

    @Column(name = "question_status_cd", nullable = false)
    private String questionStatusCd;

    @ColumnDefault("0")
    @Column(name = "del_yn", nullable = false)
    private Boolean delYn;

    @ColumnDefault("1")
    @Column(name = "auto_create", nullable = false)
    private Boolean autoCreate;

    @Column(name = "category_cd", nullable = false)
    private String categoryCd;

    @Column(name = "point", nullable = true)
    private Integer point;

    @Column(name = "str_date", nullable = true)
    private LocalDate strDate;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

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
