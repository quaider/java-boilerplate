package cn.kankancloud.jbp.core.exception;

public class BizUnAuthorizeException extends BizException {

    public BizUnAuthorizeException() {
        super(ErrorCodeI.FORBIDDEN, null);
    }

    public BizUnAuthorizeException(String message, Object... args) {
        super(ErrorCodeI.FORBIDDEN, message, args);
    }

    public BizUnAuthorizeException(String message, Throwable throwable, Object... args) {
        super(ErrorCodeI.FORBIDDEN, message, throwable, args);
    }
}