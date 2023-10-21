package cn.kankancloud.jbp.mbp.audit;

import cn.kankancloud.jbp.core.abstraction.ITimeAuditable;
import cn.kankancloud.jbp.core.abstraction.IUserAuditable;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AutoFullFillAudit implements IUserAuditable, ITimeAuditable {

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String createUserAccount;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateUserAccount;

    @TableField(fill = FieldFill.INSERT)
    private String createUserName;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateUserName;
}
