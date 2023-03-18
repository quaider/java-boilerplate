package cn.kankancloud.jbp.core.exception;

public class BizValidateException extends BizException {

    public BizValidateException(String message) {
        super(message);
    }

    public BizValidateException(String message, Object... args) {
        super(ErrorCodeI.VALIDATION_FAILED, message, args);
    }

    public BizValidateException(String message, Throwable throwable, Object... args) {
        super(ErrorCodeI.VALIDATION_FAILED, message, throwable, args);
    }
}