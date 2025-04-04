package gugunan.balanceLab.api.service;

import org.springframework.stereotype.Service;

import gugunan.balanceLab.result.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PointService {

    @PersistenceContext
    private EntityManager entityManager;

    public Integer addPoint(String userId, Integer amount, String reason) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("AddPoint");

        // 프로시저 인자 설정 (예시)
        query.registerStoredProcedureParameter("userId", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("pointAmount", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("reason", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("resultCode", String.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("newTotalPoint", Integer.class, ParameterMode.OUT);

        query.setParameter("userId", userId);
        query.setParameter("pointAmount", amount);

        query.setParameter("reason", reason);

        // 프로시저 실행
        query.execute();

        String resultCode = (String) query.getOutputParameterValue("resultCode");
        Integer newTotalPoint = (Integer) query.getOutputParameterValue("newTotalPoint");

        if ("E".equals(resultCode)) {
            log.error("AddPoint 프로시저 실행 오류: 사용자 ID={} 포인트 적립 실패", userId);
            throw new CustomException("포인트 적립 실패");
        }

        log.info("포인트 적립 성공: 사용자 ID={}, 적립된 포인트={}", userId, amount);
        return newTotalPoint; // S - 성공
    }

    public Integer usePoint(String userId, Integer amount, String reason) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("UsePoint");

        // 프로시저 인자 설정 (예시)
        query.registerStoredProcedureParameter("userId", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("pointAmount", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("reason", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("resultCode", String.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("newTotalPoint", Integer.class, ParameterMode.OUT);

        query.setParameter("userId", userId);
        query.setParameter("pointAmount", amount);

        query.setParameter("reason", reason);

        // 프로시저 실행
        query.execute();

        String resultCode = (String) query.getOutputParameterValue("resultCode");
        Integer newTotalPoint = (Integer) query.getOutputParameterValue("newTotalPoint");

        if ("E".equals(resultCode)) {
            log.error("UsePoint 프로시저 실행 오류: 사용자 ID={} 포인트 사용 실패", userId);
            throw new CustomException("포인트 사용 실패");
        }

        log.info("포인트 사용 성공: 사용자 ID={}, 사용된 포인트={}", userId, amount);
        return newTotalPoint;
    }

}
