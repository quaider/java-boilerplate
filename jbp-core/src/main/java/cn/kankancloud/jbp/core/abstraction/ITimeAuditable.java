package cn.kankancloud.jbp.core.abstraction;

import java.util.Date;

public interface ITimeAuditable {
    Date getCreateTime();

    Date getUpdateTime();
}
