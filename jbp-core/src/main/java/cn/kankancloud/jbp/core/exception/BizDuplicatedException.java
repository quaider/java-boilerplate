package cn.kankancloud.jbp.core.exception;

public class BizDuplicatedException extends BizException {

    public BizDuplicatedException(String message, Object... args) {
        super(ErrorCodeI.DUPLICATED, message, args);
    }

    public BizDuplicatedException(String message, Throwable throwable, Object... args) {
        super(ErrorCodeI.DUPLICATED, message, throwable, args);
    }
}
