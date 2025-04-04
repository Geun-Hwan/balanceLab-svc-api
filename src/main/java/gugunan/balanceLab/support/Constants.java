package gugunan.balanceLab.support;

public final class Constants {

    public static class POINT_AMOUNT {
        public static final int ATTENDANCE_REWARD = 50;
        public static final int JOIN_REWARD = 300;
    }

    public static class USER_STUS {
        public static final String ADMIN = "10000000"; //

        public static final String ACTIVE = "10000001"; // 정상 사용자
        public static final String TERMINATED = "10000002"; // 탈퇴 대기
        public static final String BANNED = "10000003"; // 차단된 사용자
        public static final String DORMANT = "10000004"; // 휴면 상태 사용자
        public static final String WITHDRAW = "10000005"; // 탈퇴한 사용자

    }

    public static class QUESTION_STATUS {

        public static final String PROGRESS = "20000001"; // 투표 진행 중
        public static final String END = "20000002"; // 투표 종료
        public static final String WAITING = "20000003"; // 투표 대기 중

    }

    public static class POINT_TYPE_CD {
        public static final String EARN = "21000001"; // 적립
        public static final String USE = "21000002"; // 사용
        public static final String EXPIRED = "21000003"; // 만료
    }

    public static class CATEGORY_CD {
        public static final String DAILY = "30000001"; // 일상
        public static final String FOOD = "30000002"; // 음식
        public static final String SELF_IMPROVEMENT = "30000003"; // 자기계발
        public static final String RELATIONSHIPS = "30000004"; // 인간관계
        public static final String MISC = "30000005"; // 기타
        public static final String ADULT = "30000006"; // 19금

    }

    public static class PREFIX {

        public static final String REFRESH_TOKEN = "_refresh_token_";
        public static final String EMAIL_CODE = "_email_code_";

    }

    public static class VALID {

        public static final long ACCESS_TOKEN = 1000 * 60 * 30; // 액세스 토큰 유효기간 (30분)
        public static final long REFRESH_TOKEN = 1000 * 60 * 60 * 24 * 7; // 리프레시 토큰 유효기간 (7일)

    }
}
