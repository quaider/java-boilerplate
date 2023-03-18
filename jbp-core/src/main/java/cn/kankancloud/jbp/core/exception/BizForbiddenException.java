package cn.kankancloud.jbp.core.exception;

public class BizForbiddenException extends BizException {

    public BizForbiddenException(String message, Object... args) {
        super(ErrorCodeI.FORBIDDEN, message, args);
    }

    public BizForbiddenException(String message, Throwable throwable, Object... args) {
        super(ErrorCodeI.FORBIDDEN, message, throwable, args);
    }
}