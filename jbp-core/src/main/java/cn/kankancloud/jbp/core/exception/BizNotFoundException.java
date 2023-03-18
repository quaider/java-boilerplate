package cn.kankancloud.jbp.core.exception;

public class BizNotFoundException extends BizException {

    public BizNotFoundException(String message, Object... args) {
        super(ErrorCodeI.NOT_FOUND, message, args);
    }

    public BizNotFoundException(String message, Throwable throwable, Object... args) {
        super(ErrorCodeI.NOT_FOUND, message, throwable, args);
    }
}
