package cn.kankancloud.jbp.mbp.tenant;

import cn.kankancloud.jbp.mbp.persistence.EventPo;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import cn.kankancloud.jbp.mbp.HintType;
import cn.kankancloud.jbp.mbp.InterceptorIgnoreHint;
import lombok.AllArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.commons.lang3.ObjectUtils;

@AllArgsConstructor
public class TenantHandler implements TenantLineHandler {

    private final TenantProperties properties;

    /**
     * 获取当前用户所属租户ID 值表达式，只支持单个 ID 值
     *
     * @return 租户 ID 值表达式
     */
    @Override
    public Expression getTenantId() {
        return new StringValue("");
    }

    @Override
    public String getTenantIdColumn() {
        return properties.getColumn();
    }

    @Override
    public boolean ignoreTable(String tableName) {

        if (SqlHelper.table(EventPo.class).getTableName().equals(tableName)) {
            return true;
        }

        // 开启hint, 关闭多租户控制
        if (InterceptorIgnoreHint.useHint(HintType.TENANT, tableName)) {
            return true;
        }

        return ObjectUtils.isNotEmpty(properties.getIgnoreTables()) && properties.getIgnoreTables().contains(tableName);
    }
}
