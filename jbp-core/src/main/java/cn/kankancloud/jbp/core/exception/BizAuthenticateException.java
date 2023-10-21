package cn.kankancloud.jbp.core.exception;

public class BizAuthenticateException extends BizException {

    public BizAuthenticateException(String message, Object... args) {
        super(ErrorCodeI.UNAUTHENTICATED, message, args);
    }

    public BizAuthenticateException(String message, Throwable throwable, Object... args) {
        super(ErrorCodeI.UNAUTHENTICATED, message, throwable, args);
    }

    public BizAuthenticateException() {
        super(ErrorCodeI.UNAUTHENTICATED, "未登录或登录已过期");
    }
}