package gugunan.balanceLab.api.model;

import java.time.LocalDateTime;

import gugunan.balanceLab.domain.entity.Selection;
import gugunan.balanceLab.support.UserContext;
import lombok.Data;

@Data
public class SelectionDto {

    private String questionId;

    private String choiceType; // A or B

    private Integer rewardPoint;

    private LocalDateTime createdDtm;

    private String userId;

    public Selection toEntity() {
        this.userId = UserContext.getAccount().getUserId();
        return Selection.builder().questionId(questionId).userId(userId).choiceType(choiceType).rewardPoint(rewardPoint)
                .build();
    }

}
