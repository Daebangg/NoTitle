package com.daebangg.sayroam.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String CODE = "code";

    // 우리가 던지는 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusiness(BusinessException e) {
        ErrorCode code = e.getErrorCode();
        ProblemDetail body = ProblemDetail.forStatusAndDetail(code.getStatus(), e.getMessage());
        body.setProperty(CODE, code.getCode());
        return ResponseEntity.status(code.getStatus()).body(body);
    }

    // @Valid 실패 -> 필드별 사유 포함
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(status, CommonErrorCode.INVALID_INPUT.getMessage());
        body.setProperty(CODE, CommonErrorCode.INVALID_INPUT.getCode());
        body.setProperty("errors", toFieldErrors(e.getBindingResult().getFieldErrors()));
        return handleExceptionInternal(e, body, headers, status, request);
    }

    // 404, 405, 415, 본문 파싱 실패 등 Spring 기본 예외에도 code 부여
    // 이 예외들은 body가 null로 넘어오고 super에서 ProblemDetail이 만들어지므로, super 호출 후 결과에 붙인다
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception e, Object body,
            HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        ResponseEntity<Object> response = super.handleExceptionInternal(e, body, headers, status, request);
        if (response != null && response.getBody() instanceof ProblemDetail problemDetail && !hasCode(problemDetail)) {
            problemDetail.setProperty(CODE, toCode(status));
        }
        return response;
    }

    // 메서드 보안(@PreAuthorize 등)에서 난 인증·인가 예외는 Security 필터가 401/403으로 처리하도록 그대로 던진다
    @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
    public void rethrowSecurityException(RuntimeException e) {
        throw e;
    }

    // 그 외 전부 -> 500, 내부 정보 숨기고 로그만
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception e) {
        log.error("Unhandled exception", e);
        CommonErrorCode code = CommonErrorCode.INTERNAL_SERVER_ERROR;
        ProblemDetail body = ProblemDetail.forStatusAndDetail(code.getStatus(), code.getMessage());
        body.setProperty(CODE, code.getCode());
        return ResponseEntity.status(code.getStatus()).body(body);
    }

    private List<Map<String, String>> toFieldErrors(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "reason", fe.getDefaultMessage() == null ? "" : fe.getDefaultMessage()))
                .toList();
    }

    private boolean hasCode(ProblemDetail problemDetail) {
        return problemDetail.getProperties() != null && problemDetail.getProperties().containsKey(CODE);
    }

    private String toCode(HttpStatusCode status) {
        HttpStatus resolved = HttpStatus.resolve(status.value());
        return resolved != null ? resolved.name() : "HTTP_" + status.value();
    }

}
