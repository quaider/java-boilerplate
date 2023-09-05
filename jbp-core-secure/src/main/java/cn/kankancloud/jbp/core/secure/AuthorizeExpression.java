package cn.kankancloud.jbp.core.secure;

import cn.kankancloud.jbp.core.security.context.PrincipalContext;
import cn.kankancloud.jbp.core.security.principal.ClaimTypes;
import cn.kankancloud.jbp.core.security.principal.ClaimsPrincipal;
import cn.kankancloud.jbp.core.security.principal.IPrincipal;

import java.util.Arrays;

public class AuthorizeExpression {

    /**
     * 允许匿名访问
     */
    public boolean allowAnonymous() {
        return true;
    }

    public boolean hasPermission(String permissionName) {
        return true;
    }

    /**
     * 是否有当前地址的权限即可访问
     */
    public boolean hasCurrentUrl() {
        return true;
    }

    /**
     * 指定角色才能访问
     */
    public boolean hasRole(String role) {
        return hasAnyRole(role);
    }

    /**
     * 指定的任意角色均可访问
     */
    public boolean hasAnyRole(String... matchRoles) {
        if (matchRoles == null || matchRoles.length == 0) {
            return false;
        }

        IPrincipal principal = PrincipalContext.getPrincipal();
        if (principal == null) {
            return false;
        }

        if (principal instanceof UserRolePrincipal) {
            UserRolePrincipal userRolePrincipal = (UserRolePrincipal) principal;
            return Arrays.stream(matchRoles).anyMatch(userRolePrincipal::isInRole);
        }

        if (principal instanceof ClaimsPrincipal) {
            ClaimsPrincipal claimsPrincipal = (ClaimsPrincipal) principal;
            return Arrays.stream(matchRoles).anyMatch(role -> claimsPrincipal.hasClaim(ClaimTypes.ROLE, role));
        }

        return false;
    }
}
