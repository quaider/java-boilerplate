package cn.kankancloud.jbp.core.domain;

/**
 * 基础值对象标识
 *
 * @param <E> 值对象类型参数
 */
public interface IValueObject<E> extends IDomainObject {
    boolean sameValueAs(E other);
}
