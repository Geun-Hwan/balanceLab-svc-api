package gugunan.balanceLab.api.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.BeanUtils;

import gugunan.balanceLab.domain.entity.QQuestionTotal;
import gugunan.balanceLab.domain.entity.Question;
import gugunan.balanceLab.domain.entity.QuestionTotal;
import gugunan.balanceLab.domain.entity.Selection;
import gugunan.balanceLab.support.Constants.QUESTION_STATUS;
import gugunan.balanceLab.support.UserContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    private String questionId;
    private String userId;
    private String title;
    private String choiceA;
    private String choiceB;
    private String enTitle;
    private String enChoiceA;
    private String enChoiceB;
    private String questionStatusCd;
    private String categoryCd;

    private String imgUrlA;
    private String imgUrlB;

    private Integer point;
    private LocalDate strDate;
    private LocalDate endDate;

    private Boolean delYn;

    // 참여여부
    private Boolean participation;

    // -----------------
    private String choiceType; // A or B or null

    private Integer selectA;
    private Integer selectB;

    private LocalDateTime participationDtm;

    private Integer usedPoint; // 등록할때 사용되는 포인트

    public QuestionDto(Question question) {

        BeanUtils.copyProperties(question, this);

    }

    /* 최초 참여 시간 및 포인트 */
    public QuestionDto(Question question, Selection selection) {
        this(question);
        this.choiceType = selection.getChoiceType();
        this.participationDtm = selection.getCreatedDtm();

    }

    /* 등록한 게임 현황 체크 */
    public QuestionDto(Question question, QuestionTotal total) {
        this(question);
        this.selectA = total.getCountA();
        this.selectB = total.getCountB();

    }

    /* 목록에서 참여여부 체크 */
    public QuestionDto(Question question, Boolean participation) {
        this(question);
        this.participation = participation;

    }

    /* 상세 */
    public QuestionDto(Question question, Boolean participation, QuestionTotal total) {
        this(question);
        this.participation = participation;
        this.selectA = total.getCountA();
        this.selectB = total.getCountB();

    }

    public QuestionDto(String questionId, String userId, String title,
            String choiceA, String choiceB, String enTitle,
            String enChoiceA, String enChoiceB, String questionStatusCd,
            String categoryCd, String imgUrlA, String imgUrlB,
            Integer point, java.sql.Date strDate, java.sql.Date endDate,
            Boolean delYn, Boolean participation) {
        this.questionId = questionId;
        this.userId = userId;
        this.title = title;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.enTitle = enTitle;
        this.enChoiceA = enChoiceA;
        this.enChoiceB = enChoiceB;
        this.questionStatusCd = questionStatusCd;
        this.categoryCd = categoryCd;
        this.imgUrlA = imgUrlA;
        this.imgUrlB = imgUrlB;
        this.point = point;
        this.strDate = (strDate != null) ? strDate.toLocalDate() : null;
        this.endDate = (endDate != null) ? endDate.toLocalDate() : null;
        this.delYn = delYn;
        this.participation = participation;
    }

    public Question toEntity() {

        String userId = UserContext.getAccount().getUserId();

        return Question.builder().title(title).choiceA(choiceA).choiceB(choiceB).strDate(strDate).endDate(endDate)
                .userId(userId)
                .categoryCd(categoryCd)
                .createUserId(userId)
                .point(point)

                .autoCreate(false)
                .updateUserId(userId)
                .questionStatusCd(Optional.ofNullable(questionStatusCd).orElse(QUESTION_STATUS.PROGRESS))
                .build();
    }
}
