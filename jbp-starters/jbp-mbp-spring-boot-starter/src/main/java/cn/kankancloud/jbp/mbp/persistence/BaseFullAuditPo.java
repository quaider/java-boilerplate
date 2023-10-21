package cn.kankancloud.jbp.mbp.persistence;

import cn.kankancloud.jbp.mbp.audit.AutoFullFillAudit;

import java.io.Serializable;

/**
 * 数据库持久化对象基类
 */
public abstract class BaseFullAuditPo<K extends Serializable> extends AutoFullFillAudit {
    public abstract K getId();
}
