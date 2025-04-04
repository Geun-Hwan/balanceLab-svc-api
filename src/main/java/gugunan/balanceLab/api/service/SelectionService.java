package gugunan.balanceLab.api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import gugunan.balanceLab.api.model.SelectionDto;
import gugunan.balanceLab.domain.entity.QQuestion;
import gugunan.balanceLab.domain.entity.QQuestionTotal;
import gugunan.balanceLab.domain.entity.QSelection;
import gugunan.balanceLab.domain.entity.Question;
import gugunan.balanceLab.domain.entity.QuestionTotal;
import gugunan.balanceLab.domain.entity.Selection;
import gugunan.balanceLab.result.CustomException;
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

    public Integer createSelection(SelectionDto selectionDto) {

        Selection selection = selectionDto.toEntity();

        Question question = queryFactory.selectFrom(qQuestion)
                .where(qQuestion.questionId.eq(selection.getQuestionId()))
                .fetchOne();

        if (question != null && question.getEndDate().isBefore(LocalDate.now())) {
            throw new CustomException("해당 질문은 종료되었습니다.");

        }

        // 정상적으로

        BooleanExpression selectionExistsCondition = qSelection.questionId.eq(selection.getQuestionId())
                .and(qSelection.userId.eq(UserContext.getAccount().getUserId()));

        Optional.ofNullable(queryFactory.selectFrom(qSelection).where(selectionExistsCondition).fetchOne())
                .ifPresent(existing -> {
                    throw new RuntimeException();
                });
        Integer point = Optional.ofNullable(selection.getRewardPoint()).orElse(0);
        if (point > 0) {
            point = pointService.addPoint(selection.getUserId(), selection.getRewardPoint(), "설문 참여 보상");
        }
        entityManager.persist(selection);

        return point;

    }

    public Long addQuestionTotal(SelectionDto selectionDto) {

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
