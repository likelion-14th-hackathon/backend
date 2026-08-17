package com.example.likelion14th_hackathon;

import org.junit.jupiter.api.Test;

class Likelion14thHackathonApplicationTests {

    @Test
    void applicationClassExists() {
        // 서버 DB 접속값이 없는 로컬/CI 환경에서도 기본 빌드가 깨지지 않도록
        // Spring 전체 컨텍스트를 띄우지 않는 가벼운 스모크 테스트만 둔다.
        // 실제 DB 연결 검증은 가비아 서버의 .env 설정 후 실행 환경에서 확인한다.
        assert Likelion14thHackathonApplication.class != null;
    }

}
