package gugunan.balanceLab.api.service;

import java.util.List;

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

import gugunan.balanceLab.api.model.PredictDto;
import gugunan.balanceLab.api.model.QuestionDto;
import gugunan.balanceLab.api.model.search.BalanceSearchParam;
import gugunan.balanceLab.api.model.search.PageParam;
import gugunan.balanceLab.domain.entity.Predict;
import gugunan.balanceLab.domain.entity.QPredict;
import gugunan.balanceLab.domain.entity.QPredictParticipation;
import gugunan.balanceLab.domain.entity.QPredictTotal;
import gugunan.balanceLab.support.Constants.QUESTION_STATUS;
import gugunan.balanceLab.support.UserContext;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class PredictService {

    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    EntityManager entityManager;

    @Autowired
    PointService pointService;
    private static final QPredict qPredict = QPredict.predict;
    private static final QPredictTotal qPredictTotal = QPredictTotal.predictTotal;

    private static final QPredictParticipation qPredictParticipation = QPredictParticipation.predictParticipation;

    public Page<PredictDto> getPredictList(PageParam pageParam) {

        String userId = UserContext.getAccount().getUserId();

        BooleanExpression delYExpression = qPredict.delYn.isFalse()
                .and(qPredict.questionStatusCd.ne(QUESTION_STATUS.WAITING));

        BooleanBuilder builder = new BooleanBuilder(qPredictParticipation.predictId.eq(qPredict.predictId));

        if (userId != null) {
            builder.and(qPredictParticipation.userId.eq(userId));
        } else {
            builder.and(Expressions.FALSE);
        }

        Pageable pageable = PageRequest.of(pageParam.getPage(), pageParam.getPageSize());

        long totalCount = queryFactory
                .select(qPredict.count())
                .from(qPredict)
                .where(
                        delYExpression)
                .fetchOne();
        List<PredictDto> results = queryFactory
                .select(Projections.constructor(PredictDto.class, qPredict, qPredictTotal, qPredictParticipation))
                .from(qPredict)
                .leftJoin(qPredictTotal)
                .on(qPredictTotal.predictId.eq(qPredict.predictId))
                .leftJoin(qPredictParticipation)
                .on(builder)
                .where(
                        delYExpression)
                .offset(pageable.getOffset())

                .limit(pageParam.getPageSize())
                .orderBy(new CaseBuilder()
                        .when(qPredict.questionStatusCd
                                .eq(QUESTION_STATUS.END))
                        .then(0).otherwise(1).desc(),
                        qPredict.strDtm.desc(),
                        qPredict.endDtm.asc(),
                        qPredictParticipation.predictId.desc().nullsFirst(),
                        qPredict.predictId.desc()

                )

                .fetch();

        return new PageImpl<>(results, pageable,
                totalCount);

    }

    public Page<PredictDto> getMyPredictList(PageParam pageParam) {
        String userId = UserContext.getAccount().getUserId();
        Pageable pageable = PageRequest.of(pageParam.getPage(), pageParam.getPageSize());

        long totalCount = queryFactory
                .select(qPredict.count())
                .from(qPredict)
                .where(
                        qPredict.userId.eq(userId))
                .fetchOne();
        List<PredictDto> results = queryFactory.select(Projections.constructor(PredictDto.class, qPredict,

                qPredictTotal))
                .from(qPredict)
                .leftJoin(qPredictTotal).on(qPredict.predictId.eq(qPredictTotal.predictId))
                .where(
                        qPredict.userId.eq(userId))
                .offset(pageable.getOffset())

                .limit(pageParam.getPageSize())
                .orderBy(qPredict.createdDtm.desc())
                .fetch();

        return new PageImpl<>(results, pageable,
                totalCount);

    }

    /**
     * @apiNote 예측 수동 등록
     */
    public Predict createPredict(PredictDto dto) {

        Predict predict = dto.toEntity();

        // pointService.usePoint(predict.getUserId(), dto.getUsedPoint(), "게임 생성 포인트
        // 사용");
        entityManager.persist(predict);

        return predict;

    }

    public void modifyPredict() {

    }

    public void deletePredict() {

    }
}
