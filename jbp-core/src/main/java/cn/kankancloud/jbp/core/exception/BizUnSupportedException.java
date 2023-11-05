package cn.kankancloud.jbp.core.exception;

public class BizUnSupportedException extends BizException {

    public BizUnSupportedException(String message, Object... args) {
        super(ErrorCodeI.NOT_SUPPORTED, message, args);
    }

    public BizUnSupportedException(String message, Throwable throwable, Object... args) {
        super(ErrorCodeI.NOT_SUPPORTED, message, throwable, args);
    }
}
