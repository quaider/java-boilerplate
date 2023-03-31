package cn.kankancloud.jbp.mbp.audit;

import cn.kankancloud.jbp.core.abstraction.IUserAuditable;
import cn.kankancloud.jbp.core.secure.UserRolePrincipal;
import cn.kankancloud.jbp.core.security.context.PrincipalContext;
import cn.kankancloud.jbp.core.security.principal.*;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;
import java.util.function.Supplier;

public class AuditMetaObjectHandler implements MetaObjectHandler {

    private static final String CREATE_TIME_FIELD = "createTime";
    private static final String UPDATE_TIME_FIELD = "updateTime";
    private static final String CREATE_USER_FIELD = "createUser";
    private static final String UPDATE_USER_FIELD = "updateUser";

    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        if (shouldFillUser(metaObject, true)) {
            this.fillUser(metaObject, true);
        }

        this.strictInsertFill(metaObject, CREATE_TIME_FIELD, () -> now, Date.class);
        this.strictUpdateFill(metaObject, UPDATE_TIME_FIELD, () -> now, Date.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (shouldFillUser(metaObject, false)) {
            this.fillUser(metaObject, false);
        }

        this.strictUpdateFill(metaObject, UPDATE_TIME_FIELD, Date::new, Date.class);
    }

    private boolean shouldFillUser(MetaObject metaObject, boolean insert) {
        if (metaObject.getOriginalObject() == null) {
            return false;
        }

        if (metaObject.getOriginalObject() instanceof IUserAuditable) {
            IUserAuditable originalObject = (IUserAuditable) metaObject.getOriginalObject();
            if (originalObject == null) {
                return false;
            }

            if (insert && (StringUtils.isEmpty(originalObject.getCreateUser()) || StringUtils.isEmpty(originalObject.getUpdateUser()))) {
                return true;
            } else {
                return StringUtils.isEmpty(originalObject.getUpdateUser());
            }
        }

        return false;
    }

    private void fillUser(MetaObject metaObject, boolean insert) {

        IPrincipal current = PrincipalContext.getPrincipal();
        if (current == null) {
            return;
        }

        String name = null;
        if (current instanceof UserRolePrincipal) {
            UserRolePrincipal userRolePrincipal = (UserRolePrincipal) current;
            name = userRolePrincipal.getUseRoleIdentity().getUserDetail().fullname();
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
            this.strictInsertFill(metaObject, UPDATE_USER_FIELD, nameSupplier, String.class);
            this.strictInsertFill(metaObject, CREATE_USER_FIELD, nameSupplier, String.class);
        } else {
            this.strictUpdateFill(metaObject, UPDATE_USER_FIELD, nameSupplier, String.class);
        }
    }
}
