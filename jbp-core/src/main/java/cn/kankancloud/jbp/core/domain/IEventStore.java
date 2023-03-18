package cn.kankancloud.jbp.core.domain;

import java.util.List;

/**
 * 事件存储
 */
public interface IEventStore {

    /**
     * 追加事件到时间存储
     *
     * @param domainEvents 领域事件
     */
    void append(IDomainEvent... domainEvents);

    /**
     * 追加事件到时间存储
     *
     * @param domainEvents 领域事件
     */
    void append(List<IDomainEvent> domainEvents);

    /**
     * 标记事件为已发布
     *
     * @param eventId 事件id
     */
    void markAsPublished(long eventId);

    /**
     * 标记事件为发布失败
     *
     * @param eventId 事件id
     */
    void markAsPublishFailed(long eventId);

    List<IDomainEvent> latestBatchEvents(int batchSize);

    /**
     * 获取事件
     *
     * @param eventId 事件id
     * @return DomainEvent
     */
    DomainEvent get(long eventId);

}
