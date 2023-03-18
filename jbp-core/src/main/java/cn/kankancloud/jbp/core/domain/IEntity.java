package cn.kankancloud.jbp.core.domain;

/**
 * 基础实体标识
 *
 * @param <E> 实体类型参数
 */
public interface IEntity<K, E extends IEntity<K, E>> extends IDomainObject {

    /**
     * 数据库主键(持久化需要)
     */
    K getId();

    E onPersisted(K id);

    default boolean sameIdAs(E other) {
        return other != null && getId().equals(other.getId());
    }
}

