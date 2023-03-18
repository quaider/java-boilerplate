package cn.kankancloud.jbp.core.domain;

import com.alibaba.fastjson.JSON;
import cn.kankancloud.jbp.mbp.persistence.EventPo;
import cn.kankancloud.jbp.mbp.persistence.EventPoService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class MbpEventStore implements IEventStore {

    private final EventPoService eventPoService;

    @Override
    public void append(IDomainEvent... events) {
        append(Lists.newArrayList(events));
    }

    @Override
    public void append(List<IDomainEvent> events) {

        if (events == null) {
            return;
        }

        events.forEach(f -> {
            EventPo eventPo = new EventPo();
            eventPo.setEventType(f.getClass().getName());
            eventPo.setEventBody(JSON.toJSONString(f));

            eventPoService.save(eventPo);

            if (f instanceof DomainEvent) {
                ((DomainEvent) f).eventId(eventPo.getId());
            }
        });
    }

    @Override
    public void markAsPublished(long eventId) {
        EventPo eventPo = new EventPo();
        eventPo.setId(eventId);
        eventPo.markAsPublished();

        // 局部更新
        eventPoService.updateById(eventPo);
    }

    @Override
    public void markAsPublishFailed(long eventId) {
        EventPo eventPo = new EventPo();
        eventPo.setId(eventId);
        eventPo.markAsPublishFailed();

        // 局部更新
        eventPoService.updateById(eventPo);
    }

    @Override
    public List<IDomainEvent> latestBatchEvents(int batchSize) {
        return null;
    }

    @Override
    public DomainEvent get(long eventId) {
        return null;
    }
}
