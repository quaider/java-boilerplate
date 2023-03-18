package cn.kankancloud.jbp.core.domain;

public enum EntityStatus {
    NEW, // 新增
    UNCHANGED, // 不变
    UPDATED, // 更改
    DELETED // 删除
    ;

    public boolean isNew() {
        return this == NEW;
    }

    public boolean isUpdated() {
        return this == UPDATED;
    }

    public boolean isUnchanged() {
        return this == UNCHANGED;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }
}
