package gugunan.balanceLab.api.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import gugunan.balanceLab.api.model.QuestionDto;
import gugunan.balanceLab.api.model.search.BalanceSearchParam;
import gugunan.balanceLab.domain.entity.QQuestion;
import gugunan.balanceLab.domain.entity.QQuestionTotal;
import gugunan.balanceLab.domain.entity.QSelection;
import gugunan.balanceLab.domain.entity.Question;
import gugunan.balanceLab.support.Constants.QUESTION_STATUS;
import gugunan.balanceLab.support.UserContext;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class QuestionService {

        @Autowired
        EntityManager entityManager;

        @Autowired
        JPAQueryFactory queryFactory;

        @Autowired
        PointService pointService;

        private static final QQuestion qQuestion = QQuestion.question;
        private static final QQuestionTotal qQusetionTotal = QQuestionTotal.questionTotal;

        private static final QSelection qSelection = QSelection.selection;

        /**
         * @apiNote 질문 수동 등록
         */
        public Object createQuestion(QuestionDto questionDto) {
                Question question = questionDto.toEntity();

                pointService.usePoint(question.getUserId(), questionDto.getUsedPoint(), "게임 생성 포인트 사용");
                entityManager.persist(question);

                return question;
        }

        /**
         * @apiNote 질문 수정
         *          시작 전에만 수정 가능
         */
        public Long modifyQuestion(QuestionDto questionDto) {

                String userId = UserContext.getAccount().getUserId();
                Integer used = questionDto.getUsedPoint();
                if (used != null) {

                        if (used > 0) {
                                pointService.usePoint(userId, used, "게임 수정 추가포인트 사용");
                        }
                        if (used < 0) {
                                pointService.addPoint(userId, Math.abs(used), "게임 수정 포인트 환급");

                        }
                }

                BooleanExpression isTarget = qQuestion.questionId.eq(questionDto.getQuestionId());
                BooleanExpression isMine = qQuestion.userId.eq(UserContext.getAccount().getUserId());
                BooleanExpression isWating = qQuestion.questionStatusCd.eq(QUESTION_STATUS.WAITING);
                BooleanExpression isAfter = qQuestion.strDate.after(LocalDate.now());

                return queryFactory.update(qQuestion).set(qQuestion.choiceA, questionDto.getChoiceA())
                                .set(qQuestion.title, questionDto.getTitle())
                                .set(qQuestion.choiceB, questionDto.getChoiceB())
                                .set(qQuestion.strDate, questionDto.getStrDate())
                                .set(qQuestion.endDate, questionDto.getEndDate())
                                .set(qQuestion.categoryCd, questionDto.getCategoryCd())
                                .set(qQuestion.questionStatusCd, questionDto.getQuestionStatusCd())
                                .set(qQuestion.updateUserId, userId)
                                .where(isTarget, isMine, isWating, isAfter)

                                .execute();

        }

        /**
         * @param questionId 질문 ID
         * @apiNote 질문 삭제 api
         * 
         */
        public Long removeQuestion(String questionId) {

                BooleanExpression isTarget = qQuestion.questionId.eq(questionId);
                BooleanExpression isMine = qQuestion.userId.eq(UserContext.getAccount().getUserId());
                BooleanExpression isWatingOrEnd = qQuestion.questionStatusCd.in(QUESTION_STATUS.WAITING,
                                QUESTION_STATUS.END);
                BooleanExpression isAfterOrIsBefore = qQuestion.strDate.after(LocalDate.now())
                                .or(qQuestion.endDate.before(LocalDate.now()));

                return queryFactory.update(qQuestion).set(qQuestion.delYn, true)
                                .where(isTarget, isMine, isWatingOrEnd, isAfterOrIsBefore)
                                .execute();

        }

        public Page<QuestionDto> getMyQuestionList(BalanceSearchParam BalanceSearchParam) {
                String userId = UserContext.getAccount().getUserId();
                Pageable pageable = PageRequest.of(BalanceSearchParam.getPage(), BalanceSearchParam.getPageSize());

                long totalCount = queryFactory
                                .select(qQuestion.count())
                                .from(qQuestion)
                                .where(
                                                qQuestion.userId.eq(userId))
                                .fetchOne();
                List<QuestionDto> results = queryFactory.select(Projections.constructor(QuestionDto.class, qQuestion,

                                qQusetionTotal))
                                .from(qQuestion)
                                .leftJoin(qQusetionTotal).on(qQuestion.questionId.eq(qQusetionTotal.questionId))
                                .where(
                                                qQuestion.userId.eq(userId))
                                .offset(pageable.getOffset())

                                .limit(BalanceSearchParam.getPageSize())
                                .orderBy(qQuestion.createdDtm.desc())
                                .fetch();

                return new PageImpl<>(results, pageable,
                                totalCount);

        }

        public Page<QuestionDto> getParticipationList(BalanceSearchParam BalanceSearchParam) {
                String userId = UserContext.getAccount().getUserId();
                Pageable pageable = PageRequest.of(BalanceSearchParam.getPage(), BalanceSearchParam.getPageSize());

                long totalCount = queryFactory
                                .select(qSelection.count())
                                .from(qSelection)
                                .where(
                                                qSelection.userId.eq(userId))
                                .fetchOne();
                List<QuestionDto> results = queryFactory
                                .select(Projections.constructor(
                                                QuestionDto.class,
                                                qQuestion,
                                                qSelection))
                                .from(qSelection)
                                .join(qQuestion)
                                .on(qSelection.questionId.eq(qQuestion.questionId))
                                .where(
                                                qSelection.userId.eq(userId).and(qSelection.rewardPoint.gt(0)))
                                .offset(pageable.getOffset())

                                .limit(BalanceSearchParam.getPageSize())
                                .orderBy(qSelection.createdDtm.desc())

                                .fetch();

                return new PageImpl<>(results, pageable,
                                totalCount);

        }

}
