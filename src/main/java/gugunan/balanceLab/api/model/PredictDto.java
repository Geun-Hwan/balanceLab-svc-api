package gugunan.balanceLab.api.model;

import java.time.LocalDateTime;
import java.util.Optional;

import gugunan.balanceLab.domain.entity.Predict;
import gugunan.balanceLab.domain.entity.PredictParticipation;
import gugunan.balanceLab.domain.entity.PredictTotal;
import gugunan.balanceLab.support.Constants.QUESTION_STATUS;
import gugunan.balanceLab.support.UserContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictDto {

    private String predictId;
    private String title;
    private String userId;

    private String optionA;
    private String optionB;
    private String optionC;

    private String questionStatusCd;

    private LocalDateTime strDtm;
    private LocalDateTime endDtm;

    private Boolean delYn;

    private Integer usedPoint;

    private Integer countA;
    private Integer countB;
    private Integer countC;
    private Integer sumPointA;
    private Integer sumPointB;
    private Integer sumPointC;
    private Integer totalPoints;

    private String payoutA;
    private String payoutB;
    private String payoutC;

    private String winner;

    private Boolean participation;
    private String choiceType; // A or B or C
    private Integer rewardPoint;
    private Integer betPoint;

    public PredictDto(Predict predict, PredictTotal predictTotal) {
        this.predictId = predict.getPredictId();
        this.title = predict.getTitle();
        this.userId = predict.getUserId();
        this.optionA = predict.getOptionA();
        this.optionB = predict.getOptionB();
        this.optionC = predict.getOptionC();
        this.strDtm = predict.getStrDtm();
        this.endDtm = predict.getEndDtm();
        this.questionStatusCd = predict.getQuestionStatusCd();
        this.delYn = predict.getDelYn();

        this.countA = predictTotal.getCountA();
        this.countB = predictTotal.getCountB();
        this.countC = predictTotal.getCountC();
        this.sumPointA = predictTotal.getSumPointA();
        this.sumPointB = predictTotal.getSumPointB();
        this.sumPointC = predictTotal.getSumPointC();
        this.winner = predictTotal.getWinner();

        // 배당률을 PredictTotal에서 가져옵니다.
        this.payoutA = predictTotal.getPayoutA().toPlainString();
        this.payoutB = predictTotal.getPayoutB().toPlainString();
        this.payoutC = predictTotal.getPayoutC().toPlainString();
    }

    // 생성자
    public PredictDto(Predict predict, PredictTotal predictTotal, PredictParticipation predictParticipation) {

        this(predict, predictTotal);
        if (predictParticipation != null) {
            this.participation = true;
            this.choiceType = predictParticipation.getChoiceType();
            this.betPoint = predictParticipation.getBetPoint();
            this.rewardPoint = predictParticipation.getRewardPoint();
        }
    }

    public Predict toEntity() {

        String userId = UserContext.getAccount().getUserId();

        return Predict.builder().title(title).optionA(optionA).optionB(optionB).optionC(optionC).strDtm(strDtm)
                .endDtm(endDtm)
                .userId(userId)
                .delYn(false)
                .createUserId(userId)
                .updateUserId(userId)
                .questionStatusCd(Optional.ofNullable(questionStatusCd).orElse(QUESTION_STATUS.WAITING))
                .build();
    }

    public PredictParticipation toParticipationEntity() {
        String userId = UserContext.getAccount().getUserId();

        return PredictParticipation.builder().predictId(predictId).userId(userId).choiceType(choiceType)
                .betPoint(betPoint)
                .build();

    }
}
