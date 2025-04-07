package gugunan.balanceLab.api.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import gugunan.balanceLab.BalanceLabApplication;
import gugunan.balanceLab.api.model.PredictDto;
import gugunan.balanceLab.api.model.QuestionDto;
import gugunan.balanceLab.api.model.SelectionDto;
import gugunan.balanceLab.api.model.search.BalanceSearchParam;
import gugunan.balanceLab.api.model.search.PageParam;
import gugunan.balanceLab.domain.entity.QDailyCounts;
import gugunan.balanceLab.domain.entity.QMonthlyCounts;
import gugunan.balanceLab.domain.entity.QQuestion;
import gugunan.balanceLab.domain.entity.QQuestionTotal;
import gugunan.balanceLab.domain.entity.QSelection;
import gugunan.balanceLab.domain.entity.QWeekliyCounts;
import gugunan.balanceLab.support.Constants.QUESTION_STATUS;
import gugunan.balanceLab.support.UserContext;
import gugunan.balanceLab.utils.SqlUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class PublicService {

        @Autowired
        EntityManager entityManager;

        @Autowired
        JPAQueryFactory queryFactory;

        @Autowired
        PointService pointService;

        @Autowired
        SelectionService selectionService;

        @Autowired
        QuestionService questionService;

        @Autowired
        PredictService predictService;

        private static final QQuestion qQuestion = QQuestion.question;

        private static final QSelection qSelection = QSelection.selection;

        private static final QDailyCounts qDailyCount = QDailyCounts.dailyCounts;
        private static final QWeekliyCounts qWeekliyCounts = QWeekliyCounts.weekliyCounts;
        private static final QMonthlyCounts qQMonthlyCounts = QMonthlyCounts.monthlyCounts;

        /**
         * @param
         * @apiNote 질문 목록 조회
         * 
         */
        public Page<QuestionDto> getQuestionList(BalanceSearchParam BalanceSearchParam) {

                return questionService.getQuestionList(BalanceSearchParam);

        }

        /**
         * @param questionId 질문 ID
         * @apiNote 질문 상세 조회
         */
        public QuestionDto getQuestion(String questionId) {
                return questionService.getQuestion(questionId);

        }

        public Page<PredictDto> getPredictList(PageParam pageParam) {

                return predictService.getPredictList(pageParam);
        }

        public List<String> getPublicIdList() {
                LocalDate now = LocalDate.now();

                return queryFactory.select(qQuestion.questionId).from(qQuestion)
                                .where(qQuestion.strDate.after(now.minusMonths(1)))
                                .fetch();
        }

        public List<QuestionDto> getDailyRank() {

                String userId = UserContext.getAccount().getUserId();

                BooleanBuilder builder = new BooleanBuilder(qSelection.questionId.eq(qQuestion.questionId));

                if (userId != null) {
                        builder.and(qSelection.userId.eq(userId));
                } else {
                        builder.and(Expressions.FALSE);
                }

                List<QuestionDto> daliyRank = queryFactory.select(Projections.constructor(
                                QuestionDto.class,
                                qQuestion,
                                new CaseBuilder().when(qSelection.isNotNull()).then(true)
                                                .otherwise(false)))
                                .from(qDailyCount)
                                .join(qQuestion)

                                .on(qQuestion.questionId.eq(qDailyCount.questionId)).orderBy(qDailyCount.count.desc())
                                .leftJoin(qSelection)
                                .on(builder)

                                .limit(3).fetch();

                if (daliyRank.size() > 0 && daliyRank.size() < 3) {
                        return getRemainList(daliyRank);
                }
                return daliyRank;

        }

        public List<QuestionDto> getWeeklyRank() {
                String userId = UserContext.getAccount().getUserId();

                BooleanBuilder builder = new BooleanBuilder(qSelection.questionId.eq(qQuestion.questionId));

                if (userId != null) {
                        builder.and(qSelection.userId.eq(userId));
                } else {
                        builder.and(Expressions.FALSE);
                }

                List<QuestionDto> weeklyRank = queryFactory.select(Projections.constructor(
                                QuestionDto.class,
                                qQuestion,
                                new CaseBuilder().when(qSelection.isNotNull()).then(true)
                                                .otherwise(false)))
                                .from(qWeekliyCounts)
                                .join(qQuestion)
                                .on(qQuestion.questionId.eq(qWeekliyCounts.questionId))
                                .leftJoin(qSelection)
                                .on(builder)

                                .orderBy(qWeekliyCounts.count.desc())
                                .limit(3).fetch();

                if (weeklyRank.size() > 0 && weeklyRank.size() < 3) {
                        return getRemainList(weeklyRank);
                }
                return weeklyRank;

        }

        public List<QuestionDto> getMonthlyRank() {
                String userId = UserContext.getAccount().getUserId();
                BooleanBuilder builder = new BooleanBuilder(qSelection.questionId.eq(qQuestion.questionId));

                if (userId != null) {
                        builder.and(qSelection.userId.eq(userId));
                } else {
                        builder.and(Expressions.FALSE);
                }
                List<QuestionDto> monthlyRank = queryFactory.select(Projections.constructor(
                                QuestionDto.class,
                                qQuestion,
                                new CaseBuilder().when(qSelection.isNotNull()).then(true)
                                                .otherwise(false)))
                                .from(qQMonthlyCounts)
                                .join(qQuestion)
                                .on(qQuestion.questionId.eq(qQMonthlyCounts.questionId))
                                .leftJoin(qSelection)
                                .on(builder)

                                .orderBy(qQMonthlyCounts.count.desc())
                                .limit(3).fetch();

                if (monthlyRank.size() > 0 && monthlyRank.size() < 3) {
                        return getRemainList(monthlyRank);
                }
                return monthlyRank;

        }

        public List<QuestionDto> getTodayQuestion() {
                LocalDate today = LocalDate.now();
                String userId = UserContext.getAccount().getUserId();

                BooleanBuilder builder = new BooleanBuilder(qSelection.questionId.eq(qQuestion.questionId));

                if (userId != null) {
                        builder.and(qSelection.userId.eq(userId));
                } else {
                        builder.and(Expressions.FALSE);
                }

                return queryFactory.select(Projections.constructor(
                                QuestionDto.class,
                                qQuestion,
                                new CaseBuilder().when(qSelection.isNotNull()).then(true)
                                                .otherwise(false)))
                                .from(qQuestion)
                                .leftJoin(qSelection)
                                .on(builder)

                                .where(qQuestion.strDate.isNotNull().and(qQuestion.strDate.eq(today))
                                                .and(qQuestion.delYn.isFalse())
                                                .and(qQuestion.questionStatusCd.eq(QUESTION_STATUS.PROGRESS))
                                                .and(qQuestion.autoCreate.isTrue()))
                                .orderBy(qQuestion.questionId.desc())
                                .limit(3).fetch();

        }

        /* 비로그인 유저한테 보이는 항목 */
        public List<QuestionDto> getRemainList(List<QuestionDto> rankList) {
                String userId = UserContext.getAccount().getUserId();

                String remainingQuery = """
                                    SELECT q.question_id AS questionId, q.user_id AS userId, q.title,
                                           q.choice_a AS choiceA, q.choice_b AS choiceB,
                                           q.en_title AS enTitle, q.en_choice_a AS enChoiceA,
                                           q.en_choice_b AS enChoiceB, q.question_status_cd AS questionStatusCd,
                                           q.category_cd AS categoryCd, q.img_url_a AS imgUrlA, q.img_url_b AS imgUrlB,
                                           q.point, q.str_date AS strDate, q.end_date AS endDate,
                                           q.del_yn AS delYn,
                                           CASE WHEN s.question_id IS NOT NULL THEN TRUE ELSE FALSE END AS participation
                                    FROM QUESTION q
                                    LEFT JOIN SELECTION s ON q.question_id = s.question_id AND (:userId IS NOT NULL AND s.user_id = :userId)
                                    WHERE q.question_id NOT IN :selectedQuestionIds
                                    ORDER BY RAND()
                                    LIMIT :limit
                                """;

                // 네이티브 쿼리를 실행하고, 결과를 QuestionDto 매핑하여 반환
                List<Tuple> tuples = entityManager
                                .createNativeQuery(remainingQuery, Tuple.class) // ✅ Tuple.class 지정
                                .setParameter("userId", userId)
                                .setParameter("selectedQuestionIds", rankList.stream()
                                                .map(QuestionDto::getQuestionId)
                                                .collect(Collectors.toList()))
                                .setParameter("limit", 3 - rankList.size())
                                .getResultList(); // ✅ 여기서 Tuple 리스트로 받아야 함

                List<QuestionDto> remainingQuestions = tuples.stream()
                                .map(tuple -> SqlUtil.mapTupleToDto(tuple, QuestionDto.class)) // ✅ 이제 tuple은 Tuple 타입
                                .collect(Collectors.toList());

                List<QuestionDto> mergeList = new ArrayList<>(rankList);
                mergeList.addAll(remainingQuestions);

                return mergeList;
        }

        public Long addQuestionTotalCount(SelectionDto selectionDto) {
                return selectionService.addQuestionTotal(selectionDto);
        }

}
