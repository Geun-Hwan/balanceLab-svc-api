package gugunan.balanceLab.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import gugunan.balanceLab.api.model.PredictDto;
import gugunan.balanceLab.api.model.QuestionDto;
import gugunan.balanceLab.domain.entity.Predict;
import gugunan.balanceLab.domain.entity.PredictParticipation;
import gugunan.balanceLab.domain.entity.QPredict;
import gugunan.balanceLab.domain.entity.QPredictParticipation;
import gugunan.balanceLab.domain.entity.QQuestion;
import gugunan.balanceLab.domain.entity.QQuestionTotal;
import gugunan.balanceLab.domain.entity.QSelection;
import gugunan.balanceLab.domain.entity.Question;
import gugunan.balanceLab.domain.entity.Selection;
import gugunan.balanceLab.result.CustomException;
import gugunan.balanceLab.result.ErrorResult;
import gugunan.balanceLab.support.Constants.QUESTION_STATUS;
import gugunan.balanceLab.support.UserContext;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class SelectionService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    PointService pointService;

    private static final QSelection qSelection = QSelection.selection;
    private static final QQuestion qQuestion = QQuestion.question;
    private static final QQuestionTotal qQuestionTotal = QQuestionTotal.questionTotal;

    private static final QPredict qPredict = QPredict.predict;
    private static final QPredictParticipation qPredictParticipation = QPredictParticipation.predictParticipation;

    public Integer createSelection(QuestionDto selectionDto) {

        Question question = queryFactory.selectFrom(qQuestion)
                .where(qQuestion.questionId.eq(selectionDto.getQuestionId()))
                .fetchOne();

        if (question != null && question.getEndDate() != null) {
            if ((question.getEndDate().isBefore(LocalDate.now()) || Arrays.asList(
                    QUESTION_STATUS.END,
                    QUESTION_STATUS.COMPLETE).contains(question.getQuestionStatusCd()))) {
                throw new CustomException("해당 질문은 종료되었습니다.");

            }

            if (question.getDelYn()) {
                throw new CustomException(ErrorResult.NO_DATA);

            }
        } else {
            throw new CustomException(ErrorResult.NO_DATA);

        }

        // 정상적으로

        BooleanExpression selectionExistsCondition = qSelection.questionId.eq(selectionDto.getQuestionId())
                .and(qSelection.userId.eq(UserContext.getAccount().getUserId()));

        Optional.ofNullable(queryFactory.selectFrom(qSelection).where(selectionExistsCondition).fetchOne())
                .ifPresent(existing -> {
                    throw new RuntimeException();
                });

        Selection selection = selectionDto.toSelectionEntity();

        Integer point = Optional.ofNullable(selection.getRewardPoint()).orElse(0);
        if (point > 0) {
            point = pointService.addPoint(selection.getUserId(), selection.getRewardPoint(), "설문 참여 보상");
        }
        entityManager.persist(selection);

        return point;

    }

    public Integer createPredictBet(PredictDto predictDto) {

        Predict predict = queryFactory.selectFrom(qPredict)
                .where(qPredict.predictId.eq(predictDto.getPredictId()))
                .fetchOne();

        if (predict != null) {
            if ((predict.getEndDtm().isBefore(LocalDateTime.now())
                    || Arrays.asList(
                            QUESTION_STATUS.END,
                            QUESTION_STATUS.COMPLETE).contains(predict.getQuestionStatusCd()))) {
                throw new CustomException("해당 질문은 종료되었습니다.");

            }

            if (predict.getDelYn()) {
                throw new CustomException(ErrorResult.NO_DATA);

            }
        } else {
            throw new CustomException(ErrorResult.NO_DATA);

        }
        // 정상적으로

        BooleanExpression selectionExistsCondition = qPredictParticipation.predictId.eq(predictDto.getPredictId())
                .and(qPredictParticipation.userId.eq(UserContext.getAccount().getUserId()));

        Optional.ofNullable(queryFactory.selectFrom(qPredictParticipation).where(selectionExistsCondition).fetchOne())
                .ifPresent(existing -> {
                    throw new RuntimeException();
                });

        PredictParticipation participation = predictDto.toParticipationEntity();

        Integer point = pointService.usePoint(participation.getUserId(), participation.getBetPoint(), "예측 참여");

        entityManager.persist(participation);

        return point;

    }

    public Long addQuestionTotal(QuestionDto selectionDto) {

        NumberPath<Integer> targetColumn = selectionDto.getChoiceType().equals("A")
                ? qQuestionTotal.countA
                : qQuestionTotal.countB;

        return queryFactory.update(qQuestionTotal).set(targetColumn, targetColumn.add(1))
                .where(qQuestionTotal.questionId.eq(selectionDto.getQuestionId())).execute();
    }

    public List<Selection> getSelectionList() {
        String userId = UserContext.getAccount().getUserId();

        return queryFactory.selectFrom(qSelection).where(qSelection.userId.eq(userId))
                .orderBy(qSelection.createdDtm.desc()).fetch();
    }

}
