package cn.kankancloud.jbp.core.domain;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 基础值对象标识
 *
 * @param <E> 值对象类型参数
 */
public abstract class ValueObject<E extends IValueObject<E>> implements IValueObject<E> {

    private transient int cachedHashCode = 0;

    @Override
    public boolean sameValueAs(E other) {
        return other != null && reflectionEquals((E) this, other);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return sameValueAs((E) o);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int hashCode() {
        int h = cachedHashCode;
        if (h == 0) {
            h = reflectionHash((E) this);
            cachedHashCode = h;
        }

        return h;
    }

    private int reflectionHash(E a) {
        Class<?> clazz = a.getClass();
        List<Object> list = new ArrayList<>();

        try {
            PropertyDescriptor[] pds = Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                Method readMethod = pd.getReadMethod();
                Object aValue = readMethod.invoke(a);
                list.add(aValue);
            }
        } catch (Exception ignored) {
            // empty
        }

        return Objects.hash(list);
    }

    private boolean reflectionEquals(E a, E b) {
        if (a.getClass() != b.getClass()) return false;
        boolean diff = false;
        Class<?> clazz = a.getClass();

        try {
            // 获取object的所有属性
            PropertyDescriptor[] pds = Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors();

            for (PropertyDescriptor pd : pds) {
                Method readMethod = pd.getReadMethod();

                // a.getXxx()
                Object aValue = readMethod.invoke(a);
                // b.getXxx()
                Object bValue = readMethod.invoke(b);

                if (aValue == null && bValue == null) {
                    continue;
                }

                if (aValue == null || !aValue.equals(bValue)) {
                    return true;
                }
            }
        } catch (Exception e) {
            // empty
        }

        return diff;
    }
}
