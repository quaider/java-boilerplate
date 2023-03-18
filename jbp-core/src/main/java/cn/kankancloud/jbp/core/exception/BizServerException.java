package cn.kankancloud.jbp.core.exception;

public class BizServerException extends BizException {

    public BizServerException(String message, Object... args) {
        super(ErrorCodeI.INTERNAL, message, args);
    }

    public BizServerException(String message, Throwable throwable, Object... args) {
        super(ErrorCodeI.INTERNAL, message, throwable, args);
    }
}
