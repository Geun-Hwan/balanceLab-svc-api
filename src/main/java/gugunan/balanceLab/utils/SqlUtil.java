package gugunan.balanceLab.utils;

import jakarta.persistence.Tuple;
import java.lang.reflect.Field;

public final class SqlUtil {

    private SqlUtil() {
    }

    public static <T> T mapTupleToDto(Tuple tuple, Class<T> dtoClass) {
        try {
            T dto = dtoClass.getDeclaredConstructor().newInstance();
            Field[] fields = dtoClass.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                try {
                    Object value = tuple.get(fieldName);

                    // ✅ LocalDate 처리 (java.sql.Date → LocalDate)
                    if (value instanceof java.sql.Date sqlDate && field.getType().equals(java.time.LocalDate.class)) {
                        value = sqlDate.toLocalDate();
                    }

                    // ✅ LocalDateTime 처리 (java.sql.Timestamp → LocalDateTime)
                    if (value instanceof java.sql.Timestamp sqlTimestamp
                            && field.getType().equals(java.time.LocalDateTime.class)) {
                        value = sqlTimestamp.toLocalDateTime();
                    }

                    field.set(dto, value);
                } catch (IllegalArgumentException e) {
                    // 컬럼이 없는 경우 무시
                }
            }
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
