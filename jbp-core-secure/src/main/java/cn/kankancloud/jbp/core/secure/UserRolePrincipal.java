package cn.kankancloud.jbp.core.secure;

import cn.kankancloud.jbp.core.security.principal.IIdentity;
import cn.kankancloud.jbp.core.security.principal.IPrincipal;
import lombok.Getter;

public class UserRolePrincipal implements IPrincipal {

    @Getter
    private final UseRoleIdentity useRoleIdentity;

    public UserRolePrincipal(UseRoleIdentity useRoleIdentity) {
        if (useRoleIdentity == null) {
            throw new IllegalArgumentException("useRoleIdentity");
        }

        this.useRoleIdentity = useRoleIdentity;
    }

    @Override
    public IIdentity identity() {
        return useRoleIdentity;
    }

    @Override
    public boolean isInRole(String role) {
        return useRoleIdentity.getRoles().stream().anyMatch(f -> f.equalsIgnoreCase(role));
    }
}
