package cn.kankancloud.jbp.mbp.audit;

import cn.kankancloud.jbp.core.abstraction.BaseAuditable;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class AutoFillAudit extends BaseAuditable {

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
