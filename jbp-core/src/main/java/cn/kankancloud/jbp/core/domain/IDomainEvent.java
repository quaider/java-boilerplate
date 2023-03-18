package cn.kankancloud.jbp.core.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 领域事件
 */
public interface IDomainEvent extends Serializable {
    Date occurredOn();

    Long eventId();
}
