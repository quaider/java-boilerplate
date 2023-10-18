package cn.kankancloud.jbp.core;

import cn.kankancloud.jbp.core.exception.ErrorCodeI;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@Schema(description = "响应结果")
public class Result<T> {

    @Schema(description = "响应码")
    private final int code;

    @Schema(description = "错误消息")
    private final String message;

    @Schema(description = "响应数据")
    private final T data;

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private List<? extends Serializable> errors;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorCodeI.SUCCESS, null, data);
    }

    public static <T> Result<T> failed(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> failed(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    public static <T> Result<T> notFound(String message) {
        return failed(ErrorCodeI.NOT_FOUND, message);
    }

    public static <T> Result<T> notFound() {
        return failed(ErrorCodeI.NOT_FOUND, "记录不存在");
    }

    /**
     * 数据为null时返回记录不存在，否则正常返回
     */
    public static <T> Result<T> successOrNotFound(T data) {
        if (data == null) {
            return notFound();
        }

        return success(data);
    }

    public boolean isSuccess() {
        return code == ErrorCodeI.SUCCESS;
    }

    public Result<T> withErrors(List<? extends Serializable> errors) {
        if (isSuccess()) {
            return this;
        }

        this.errors = errors;
        return this;
    }

}
