package com.daebangg.sayroam.global.exception;

import org.springframework.http.ProblemDetail;

/** MVC와 Security 필터가 공유하는 오류 본문 생성 규칙. */
public final class ApiProblems {
    private ApiProblems() {
    }

    public static ProblemDetail of(ErrorCode code) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(code.getStatus(), code.getMessage());
        problem.setProperty("code", code.getCode());
        return problem;
    }
}
