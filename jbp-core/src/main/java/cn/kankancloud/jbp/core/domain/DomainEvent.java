package cn.kankancloud.jbp.core.domain;

import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

@ToString
public abstract class DomainEvent implements IDomainEvent {

    private final Date occurredOn;
    private Long eventId;

    protected DomainEvent() {
        this.occurredOn = new Date();
    }

    public Date occurredOn() {
        return occurredOn;
    }

    public void eventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public Long eventId() {
        return eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DomainEvent that = (DomainEvent) o;

        return new EqualsBuilder().append(eventId, that.eventId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(eventId).toHashCode();
    }
}
