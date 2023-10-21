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
    private static final String CREATE_USER_FIELD = "createUserAccount";
    private static final String CREATE_USER_NAME_FIELD = "createUserName";
    private static final String UPDATE_USER_FIELD = "updateUserAccount";
    private static final String UPDATE_USER_NAME_FIELD = "updateUserName";

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

        if (metaObject.getOriginalObject() instanceof IUserAuditable originalObject) {
            if (insert && (StringUtils.isEmpty(originalObject.getCreateUserAccount()) || StringUtils.isEmpty(originalObject.getCreateUserName()))) {
                return true;
            } else {
                return StringUtils.isEmpty(originalObject.getUpdateUserAccount());
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
        String account = null;

        if (current instanceof UserRolePrincipal userRolePrincipal) {
            name = userRolePrincipal.getUseRoleIdentity().getUserDetail().getFullname();
            account = userRolePrincipal.getUseRoleIdentity().getUserDetail().getAccount();
        } else if (current instanceof ClaimsPrincipal claimsPrincipal) {
            for (ClaimsIdentity identity : claimsPrincipal.getIdentities()) {
                if (StringUtils.isEmpty(name)) {
                    name = getFirstClaimValue(identity, ClaimTypes.FULLNAME);
                }

                if (StringUtils.isEmpty(account)) {
                    account = getFirstClaimValue(identity, ClaimTypes.ACCOUNT);
                }
            }
        }

        final String name0 = name;
        final String account0 = account;

        if (insert) {
            this.strictInsertFill(metaObject, CREATE_USER_FIELD, defaultAuditUser(account0), String.class);
            this.strictInsertFill(metaObject, CREATE_USER_NAME_FIELD, defaultAuditUser(name0), String.class);
            return;
        }

        this.strictUpdateFill(metaObject, UPDATE_USER_FIELD, defaultAuditUser(account0), String.class);
        this.strictUpdateFill(metaObject, UPDATE_USER_NAME_FIELD, defaultAuditUser(name0), String.class);
    }

    private String getFirstClaimValue(ClaimsIdentity identity, String claimType) {
        Claim claim = identity.claimFirst(claimType);
        return claim != null ? claim.getValue() : null;
    }

    private Supplier<String> defaultAuditUser(String value) {
        return () -> StringUtils.isEmpty(value) ? "system" : value;
    }
}
