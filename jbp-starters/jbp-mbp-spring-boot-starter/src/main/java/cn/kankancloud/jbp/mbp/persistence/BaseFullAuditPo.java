package cn.kankancloud.jbp.mbp.persistence;

import cn.kankancloud.jbp.core.abstraction.IUserAuditable;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 数据库持久化对象基类
 */
@Getter
@Setter
public abstract class BaseFullAuditPo<K extends Serializable> extends BasePo<K> implements IUserAuditable {
    public abstract K getId();

    @TableField(fill = FieldFill.INSERT)
    private String createUserAccount;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateUserAccount;

    @TableField(fill = FieldFill.INSERT)
    private String createUserName;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateUserName;
}
