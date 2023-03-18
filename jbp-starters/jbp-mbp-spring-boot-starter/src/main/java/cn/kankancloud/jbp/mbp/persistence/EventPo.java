package cn.kankancloud.jbp.mbp.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_event")
public class EventPo extends BasePo<Long> {

    public static final int INIT = 0;
    public static final int PUBLISHED = 1;
    public static final int PUBLISH_FAILED = 2;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String eventBody;

    private String eventType;
    private Integer status = INIT;

    public void markAsPublished() {
        this.status = PUBLISHED;
    }

    public void markAsPublishFailed() {
        this.status = PUBLISH_FAILED;
    }
}
