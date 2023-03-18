package cn.kankancloud.jbp.core.domain;

import java.io.Serializable;
import java.util.Optional;

/**
 * 表示仓储
 *
 * @param <K> 聚合根标识类型
 * @param <T> 聚合根类型
 */
public interface IRepository<K extends Serializable, T extends AggregateRoot<K, T>> {

    /**
     * 根据主键返回聚合根
     *
     * @param id 主键标识, 注：主键并非领域对象的标识，领域对象的标识更多的是描述了业务标识
     */
    Optional<T> findById(K id);

    /**
     * 保存聚合更
     *
     * @param aggregate 聚合根对象
     */
    void save(T aggregate);

}
