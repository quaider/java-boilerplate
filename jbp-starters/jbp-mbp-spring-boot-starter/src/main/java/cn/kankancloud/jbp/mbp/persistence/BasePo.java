package cn.kankancloud.jbp.mbp.persistence;

import cn.kankancloud.jbp.mbp.audit.AutoFillAudit;

import java.io.Serializable;

/**
 * 数据库持久化对象基类
 */
public abstract class BasePo<K extends Serializable> extends AutoFillAudit {
    public abstract K getId();
}
