package gugunan.balanceLab.api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import gugunan.balanceLab.api.model.QuestionDto;
import gugunan.balanceLab.api.model.search.BalanceSearchParam;
import gugunan.balanceLab.domain.entity.QQuestion;
import gugunan.balanceLab.domain.entity.QQuestionTotal;
import gugunan.balanceLab.domain.entity.QSelection;
import gugunan.balanceLab.domain.entity.Question;
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
public class QuestionService {

        @Autowired
        EntityManager entityManager;

        @Autowired
        JPAQueryFactory queryFactory;

        @Autowired
        PointService pointService;

        private static final QQuestion qQuestion = QQuestion.question;
        private static final QQuestionTotal qQuestionTotal = QQuestionTotal.questionTotal;

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

                BooleanExpression finalCondition;

                if ("SYSTEM".equals(UserContext.getAccount().getUserId())) {
                        finalCondition = isTarget;
                } else {
                        finalCondition = isTarget.and(isMine).and(isWating).and(isAfter);
                }

                return queryFactory.update(qQuestion).set(qQuestion.choiceA, questionDto.getChoiceA())
                                .set(qQuestion.title, questionDto.getTitle())
                                .set(qQuestion.choiceB, questionDto.getChoiceB())
                                .set(qQuestion.strDate, questionDto.getStrDate())
                                .set(qQuestion.endDate, questionDto.getEndDate())
                                .set(qQuestion.categoryCd, questionDto.getCategoryCd())
                                .set(qQuestion.questionStatusCd, questionDto.getQuestionStatusCd())
                                .set(qQuestion.updateUserId, userId)
                                .where(finalCondition)

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

        /**
         * @param
         * @apiNote 질문 목록 조회
         * 
         */
        public Page<QuestionDto> getQuestionList(BalanceSearchParam BalanceSearchParam) {

                String userId = UserContext.getAccount().getUserId();

                String keywod = "%" + BalanceSearchParam.getSearch() + "%";

                BooleanExpression dateExpression = qQuestion.strDate.goe(BalanceSearchParam.getStartDate())
                                .and(qQuestion.strDate.loe(BalanceSearchParam.getEndDate()));

                BooleanExpression endedExpression = BalanceSearchParam.getShowEnded()
                                ? qQuestion.questionStatusCd.in(QUESTION_STATUS.PROGRESS, QUESTION_STATUS.END)
                                : qQuestion.questionStatusCd.eq(QUESTION_STATUS.PROGRESS);

                BooleanExpression categoryExpression = BalanceSearchParam.getCategories().size() > 0
                                ? qQuestion.categoryCd.in(BalanceSearchParam.getCategories())
                                : null;

                BooleanExpression keywordExpression = BalanceSearchParam.getSearch().length() > 1
                                ? qQuestion.title.like(keywod).or(qQuestion.choiceA.like(keywod))
                                                .or(qQuestion.choiceB.like(keywod))
                                                .or(qQuestion.enTitle.like(keywod))
                                                .or(qQuestion.enChoiceA.like(keywod))
                                                .or(qQuestion.enChoiceB.like(keywod))
                                : null;

                BooleanExpression delYExpression = qQuestion.delYn.isFalse();

                BooleanBuilder builder = new BooleanBuilder(qSelection.questionId.eq(qQuestion.questionId));

                if (userId != null) {
                        builder.and(qSelection.userId.eq(userId));
                } else {
                        builder.and(Expressions.FALSE);
                }

                Pageable pageable = PageRequest.of(BalanceSearchParam.getPage(), BalanceSearchParam.getPageSize());

                long totalCount = queryFactory
                                .select(qQuestion.count())
                                .from(qQuestion)
                                .where(
                                                categoryExpression,
                                                dateExpression,
                                                endedExpression,
                                                keywordExpression, delYExpression)
                                .fetchOne();
                List<QuestionDto> results = queryFactory
                                .select(Projections.constructor(
                                                QuestionDto.class,
                                                qQuestion,
                                                new CaseBuilder().when(qSelection.isNotNull()).then(true)
                                                                .otherwise(false)))
                                .from(qQuestion)
                                .leftJoin(qSelection)
                                .on(builder)
                                .where(
                                                categoryExpression,
                                                dateExpression,
                                                endedExpression,
                                                keywordExpression, delYExpression)
                                .offset(pageable.getOffset())

                                .limit(BalanceSearchParam.getPageSize())
                                .orderBy(new CaseBuilder()
                                                .when(qQuestion.questionStatusCd
                                                                .eq(QUESTION_STATUS.END))
                                                .then(0).otherwise(1).desc(),
                                                qQuestion.strDate.desc(),
                                                qQuestion.endDate.asc(),
                                                qSelection.questionId.desc().nullsFirst(),
                                                qQuestion.questionId.desc()

                                )

                                .fetch();

                return new PageImpl<>(results, pageable,
                                totalCount);

        }

        /**
         * @param questionId 질문 ID
         * @apiNote 질문 상세 조회
         */
        public QuestionDto getQuestion(String questionId) {

                String userId = UserContext.getAccount().getUserId();

                BooleanBuilder builder = new BooleanBuilder(qSelection.questionId.eq(qQuestion.questionId));

                if (userId != null) {
                        builder.and(qSelection.userId.eq(userId));
                } else {
                        builder.and(Expressions.FALSE);
                }
                return Optional.ofNullable(

                                queryFactory.select(Projections.constructor(QuestionDto.class, qQuestion,
                                                qSelection,
                                                qQuestionTotal))
                                                .from(qQuestion).leftJoin(qQuestionTotal)
                                                .on(qQuestion.questionId.eq(qQuestionTotal.questionId))
                                                .leftJoin(qSelection).on(builder)
                                                .where(qQuestion.questionId.eq(questionId)
                                                                .and(qQuestion.delYn.isFalse()))
                                                .fetchOne())
                                .orElseThrow(() -> {
                                        throw new CustomException(ErrorResult.NO_DATA);
                                });

        }

        public Page<QuestionDto> getMyQuestionList(BalanceSearchParam BalanceSearchParam) {
                String userId = UserContext.getAccount().getUserId();
                Pageable pageable = PageRequest.of(BalanceSearchParam.getPage(), BalanceSearchParam.getPageSize());

                BooleanBuilder builder = new BooleanBuilder(Expressions.TRUE);
                if (!userId.equals("SYSTEM")) {
                        builder.and(qQuestion.userId.eq(userId));

                }

                long totalCount = queryFactory
                                .select(qQuestion.count())
                                .from(qQuestion)
                                .where(builder)
                                .fetchOne();
                List<QuestionDto> results = queryFactory.select(Projections.constructor(QuestionDto.class, qQuestion,

                                qQuestionTotal))
                                .from(qQuestion)
                                .leftJoin(qQuestionTotal).on(qQuestion.questionId.eq(qQuestionTotal.questionId))
                                .where(builder)
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
