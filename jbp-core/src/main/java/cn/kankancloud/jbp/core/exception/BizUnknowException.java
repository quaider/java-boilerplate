package cn.kankancloud.jbp.core.exception;

public class BizUnknowException extends BizException {

    public BizUnknowException(String message, Object... args) {
        super(ErrorCodeI.UNKNOWN, message, args);
    }

    public BizUnknowException(String message, Throwable throwable, Object... args) {
        super(ErrorCodeI.UNKNOWN, message, throwable, args);
    }
}
