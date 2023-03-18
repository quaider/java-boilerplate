package cn.kankancloud.jbp.core.domain;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * 聚合根标识
 *
 * @param <T> 聚合根实体类型
 */
public abstract class AggregateRoot<K extends Serializable, T extends BaseEntity<K, T>> extends BaseEntity<K, T> {

    private final transient List<IDomainEvent> events = newArrayList();

    protected final void raiseEvent(IDomainEvent event) {
        events.add(event);
    }

    final void clearEvents() {
        events.clear();
    }

    public final List<IDomainEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
