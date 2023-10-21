package cn.kankancloud.jbp.core.secure.permission;

import cn.kankancloud.jbp.core.secure.UserRolePrincipal;
import cn.kankancloud.jbp.core.security.context.PrincipalContext;
import cn.kankancloud.jbp.core.security.principal.ClaimTypes;
import cn.kankancloud.jbp.core.security.principal.ClaimsPrincipal;
import cn.kankancloud.jbp.core.security.principal.IPrincipal;

import java.util.Arrays;

public interface PermissionChecker {

    default boolean hasAnyRole(String... matchRoles) {
        if (matchRoles == null || matchRoles.length == 0) {
            return false;
        }

        IPrincipal principal = PrincipalContext.getPrincipal();
        if (principal == null) {
            return false;
        }

        if (principal instanceof UserRolePrincipal userRolePrincipal) {
            return Arrays.stream(matchRoles).anyMatch(userRolePrincipal::isInRole);
        }

        if (principal instanceof ClaimsPrincipal claimsPrincipal) {
            return Arrays.stream(matchRoles).anyMatch(role -> claimsPrincipal.hasClaim(ClaimTypes.ROLE, role));
        }

        return false;
    }

    default boolean hasUrl(String url) {
        return true;
    }

    default boolean hasPermission(String permissionName) {
        return true;
    }
}
