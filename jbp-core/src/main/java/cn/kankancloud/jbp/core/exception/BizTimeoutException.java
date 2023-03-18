package cn.kankancloud.jbp.core.exception;

public class BizTimeoutException extends BizException {

    public BizTimeoutException(String message, Object... args) {
        super(ErrorCodeI.NOT_FOUND, message, args);
    }

    public BizTimeoutException(String message, Throwable throwable, Object... args) {
        super(ErrorCodeI.NOT_FOUND, message, throwable, args);
    }
}
