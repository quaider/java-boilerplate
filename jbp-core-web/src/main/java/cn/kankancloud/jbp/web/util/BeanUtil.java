package cn.kankancloud.jbp.web.util;

import org.springframework.beans.BeanUtils;

import java.util.function.Function;

public class BeanUtil {

    private BeanUtil() {
    }

    public static <S, T> T copyProperties(S source, Class<T> tClass) {
        T target = BeanUtils.instantiateClass(tClass);
        BeanUtils.copyProperties(source, target);

        return target;
    }

    public static <S, T> Function<S, T> copyPropertiesSupplier(Class<T> tClass) {
        return s -> copyProperties(s, tClass);
    }

}
