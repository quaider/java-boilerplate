package cn.kankancloud.jbp.core.exception;

import lombok.Getter;

import static cn.kankancloud.jbp.core.util.StrUtil.formatMessage;

/**
 * 业务异常基类
 */
@Getter
public class BizException extends RuntimeException {
    private final int code;

    public BizException(ErrorCodeI errorCode) {
        super(errorCode.getErrorMessage());
        this.code = errorCode.getErrorCode();
    }

    public BizException(ErrorCodeI errorCode, Throwable throwable) {
        super(errorCode.getErrorMessage(), throwable);
        this.code = errorCode.getErrorCode();
    }

    public BizException(int code, String message, Object... args) {
        super(formatMessage(message, args));
        this.code = code;
    }

    public BizException(int code, String message, Throwable throwable, Object... args) {
        super(formatMessage(message, args), throwable);
        this.code = code;
    }

    public BizException(String message, Object... args) {
        super(formatMessage(message, args));
        this.code = ErrorCodeI.INTERNAL;
    }

    public BizException(String message, Throwable throwable, Object... args) {
        super(formatMessage(message, args), throwable);
        this.code = ErrorCodeI.INTERNAL;
    }
}
