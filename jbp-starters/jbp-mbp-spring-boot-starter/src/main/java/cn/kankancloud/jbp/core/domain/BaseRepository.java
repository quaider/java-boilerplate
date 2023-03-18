package cn.kankancloud.jbp.core.domain;

import cn.kankancloud.jbp.mbp.utils.ServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;

@Slf4j
public abstract class BaseRepository<K extends Serializable, T extends AggregateRoot<K, T>> implements IRepository<K, T> {

    @Override
    public void save(T aggregate) {

        if (aggregate.entityStatus().isUnchanged()) {
            log.warn("persisted request is made, but the aggregate is unchanged!!!");
            return;
        }

        eventStore().append(aggregate.getEvents());

        K id = saveInternal(aggregate);

        if (aggregate.entityStatus().isNew() && aggregate.getId() == null) {
            aggregate.onPersisted(id);
        }

        // publish event to memory event bus
        // 事件发布的时机 理想情况下，应该是 在事务提交成功后，且数据库连接被释放之后
        // 为了简化处理，这里在事务还没提交时，就发布了事件，但是事件处理必须满足以下要求：
        // 1. 事件处理器是异步的
        // 2. 需要加上注解 @TransactionalEventListener
        // 这样可以保证，事件处理器的执行 是在「事务提交之后」，异步的目的是 不阻塞事务和隔离数据库连接
        aggregate.getEvents().forEach(evt -> {
            log.info("publishing domain event, eventId={}, occurredOn={}", evt.eventId(), evt.occurredOn());
            applicationEventPublisher().publishEvent(evt);
        });

        aggregate.clearEvents();
    }

    /**
     * 保存聚合
     *
     * @param aggregate 待保存的聚合对象
     * @return 新建状态返回聚合id，修改和删除状态返回受影响的行数，未修改状态返回-1
     */
    protected abstract K saveInternal(T aggregate);

    protected IEventStore eventStore() {
        return ServiceProvider.getService(IEventStore.class);
    }

    protected ApplicationEventPublisher applicationEventPublisher() {
        return ServiceProvider.getEventBus();
    }
}
