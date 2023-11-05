package cn.kankancloud.jbp.core.exception;

import lombok.Getter;

import static cn.kankancloud.jbp.core.util.StrUtil.formatMessage;

/**
 * 业务异常基类
 */
@Getter
public class BizEncryptException extends RuntimeException {
    private final int code;

    public BizEncryptException(ErrorCodeI errorCode) {
        super(errorCode.getErrorMessage());
        this.code = errorCode.getErrorCode();
    }

    public BizEncryptException(Exception exception) {
        super(exception);
        this.code =  ErrorCodeI.INTERNAL;
    }

    public BizEncryptException(ErrorCodeI errorCode, Throwable throwable) {
        super(errorCode.getErrorMessage(), throwable);
        this.code = errorCode.getErrorCode();
    }

    public BizEncryptException(int code, String message, Object... args) {
        super(formatMessage(message, args));
        this.code = code;
    }

    public BizEncryptException(int code, String message, Throwable throwable, Object... args) {
        super(formatMessage(message, args), throwable);
        this.code = code;
    }

    public BizEncryptException(String message, Object... args) {
        super(formatMessage(message, args));
        this.code = ErrorCodeI.INTERNAL;
    }

    public BizEncryptException(String message, Throwable throwable, Object... args) {
        super(formatMessage(message, args), throwable);
        this.code = ErrorCodeI.INTERNAL;
    }
}
