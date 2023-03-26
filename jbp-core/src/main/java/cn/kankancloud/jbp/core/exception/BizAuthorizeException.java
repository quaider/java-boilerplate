package cn.kankancloud.jbp.core.exception;

public class BizAuthorizeException extends BizException {

    public BizAuthorizeException() {
        super(ErrorCodeI.FORBIDDEN, null);
    }

    public BizAuthorizeException(String message, Object... args) {
        super(ErrorCodeI.FORBIDDEN, message, args);
    }

    public BizAuthorizeException(String message, Throwable throwable, Object... args) {
        super(ErrorCodeI.FORBIDDEN, message, throwable, args);
    }
}