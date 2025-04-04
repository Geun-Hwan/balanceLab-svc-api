package gugunan.balanceLab.support;

import java.lang.reflect.Member;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.EnumSet;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;

import gugunan.balanceLab.domain.CustomGeneratedId;

public class IdGenerator implements BeforeExecutionGenerator {

    private final String tableNm;

    public IdGenerator(CustomGeneratedId customGeneratedId, Member idMember,
            CustomIdGeneratorCreationContext creationContext) {
        this.tableNm = customGeneratedId.method();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner,
            Object currentValue, EventType eventType) {
        String sql = "{? = CALL FN_NEXTVAL(?)}";
        String newId = null;

        // JDBC Connection
        try (Connection con = session.getJdbcConnectionAccess().obtainConnection()) {
            try (CallableStatement callStatement = con.prepareCall(sql)) {
                callStatement.registerOutParameter(1, Types.VARCHAR);
                callStatement.setString(2, tableNm);

                callStatement.execute();
                newId = callStatement.getString(1);
            }
        } catch (SQLException sqlException) {
            throw new HibernateException(sqlException);
        }
        return newId;
    }
}