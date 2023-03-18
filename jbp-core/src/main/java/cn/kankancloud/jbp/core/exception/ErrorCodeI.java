package cn.kankancloud.jbp.core.exception;

public interface ErrorCodeI {

    int getErrorCode();

    String getErrorMessage();

    int SUCCESS = 20000;

    /**
     * 服务端异常
     */
    int INTERNAL = 50000;

    /**
     * 服务端异常(未知的出乎预料的异常)
     */
    int UNKNOWN = 50001;

    /**
     * 客户端非法请求
     */
    int BAD_REQUEST = 40000;

    /**
     * 验证失败
     */
    int VALIDATION_FAILED = 40001;

    /**
     * 记录重复
     */
    int DUPLICATED = 40002;

    /**
     * 认证失败
     */
    int UNAUTHENTICATED = 40100;

    /**
     * 无权限
     */
    int FORBIDDEN = 40300;

    /**
     * 记录不存在
     */
    int NOT_FOUND = 40400;

    /**
     * 超时
     */
    int TIMEOUT = 40800;
}
