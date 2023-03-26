package cn.kankancloud.jbp.mbp.audit;

import cn.kankancloud.jbp.core.secure.UserRolePrincipal;
import cn.kankancloud.jbp.core.security.context.PrincipalContext;
import cn.kankancloud.jbp.core.security.principal.*;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;
import java.util.function.Supplier;

public class AuditMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        this.fillUser(metaObject, true);
        this.strictInsertFill(metaObject, "createTime", () -> now, Date.class);
        this.strictUpdateFill(metaObject, "updateTime", () -> now, Date.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.fillUser(metaObject, false);
        this.strictUpdateFill(metaObject, "updateTime", Date::new, Date.class);
    }

    private void fillUser(MetaObject metaObject, boolean insert) {
        IPrincipal current = PrincipalContext.getPrincipal();
        if (current == null) {
            return;
        }

        String name = null;
        if (current instanceof UserRolePrincipal) {
            UserRolePrincipal userRolePrincipal = (UserRolePrincipal) current;
            name = userRolePrincipal.getUseRoleIdentity().getFullname();
        } else if (current instanceof ClaimsPrincipal) {
            ClaimsPrincipal claimsPrincipal = (ClaimsPrincipal) current;
            for (ClaimsIdentity identity : claimsPrincipal.getIdentities()) {
                Claim claim = identity.claimFirst(ClaimTypes.FULLNAME);
                if (claim != null) {
                    name = claim.getValue();
                }
            }
        }

        if (StringUtils.isEmpty(name)) {
            return;
        }

        final String name0 = name;
        Supplier<String> nameSupplier = () -> name0;

        if (insert) {
            this.strictInsertFill(metaObject, "updateUser", nameSupplier, String.class);
            this.strictInsertFill(metaObject, "createUser", nameSupplier, String.class);
        } else {
            this.strictUpdateFill(metaObject, "updateUser", nameSupplier, String.class);
        }
    }
}
