package cn.kankancloud.jbp.core.domain;

import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * 基础实体标识
 *
 * @param <K> 唯一标识类型参数
 * @param <T> 实体类型参数
 */
public abstract class BaseEntity<K extends Serializable, T extends BaseEntity<K, T>> implements IEntity<K, T> {

    protected EntityStatus entityStatus = EntityStatus.NEW;

    protected abstract void setId(K id);

    public EntityStatus entityStatus() {
        return entityStatus;
    }

    public T toUpdate() {
        this.entityStatus = EntityStatus.UPDATED;
        return (T) this;
    }

    public T toDelete() {
        this.entityStatus = EntityStatus.DELETED;
        return (T) this;
    }

    public T toUnChange() {
        this.entityStatus = EntityStatus.UNCHANGED;
        return (T) this;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;

        @SuppressWarnings("unchecked")
        T other = (T) obj;

        return getId() != null && sameIdAs(other);
    }

    /**
     * 用于自增情况下，唯一标识的回填
     */
    @Override
    public T onPersisted(K id) {
        setId(id);
        return (T) this;
    }

    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
