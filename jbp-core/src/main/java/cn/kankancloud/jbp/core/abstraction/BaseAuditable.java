package cn.kankancloud.jbp.core.abstraction;

import lombok.Data;

import java.util.Date;

/**
 * 可审计对象
 */
@Data
public abstract class BaseAuditable implements IAuditable {
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
}
