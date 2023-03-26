package cn.kankancloud.jbp.core.secure;

import cn.kankancloud.jbp.core.security.principal.IIdentity;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
public class UseRoleIdentity implements IIdentity {

    private final String userId;
    private String fullname;
    private final List<String> roles;

    public UseRoleIdentity(String userId, List<String> roles) {
        this.userId = userId;
        this.roles = roles;
    }

    @Override
    public String name() {
        return UseRoleIdentity.class.getSimpleName();
    }

    @Override
    public String authenticationType() {
        return "RBAC";
    }

    @Override
    public boolean isAuthenticated() {
        return !StringUtils.isNoneEmpty(userId);
    }
}
