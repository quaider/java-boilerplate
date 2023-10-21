package cn.kankancloud.jbp.core.secure;

import cn.kankancloud.jbp.core.secure.permission.PermissionChecker;
import cn.kankancloud.jbp.web.util.RequestUtil;

/**
 * 权限表达式
 */
public class AuthorizeExpression {

    private final PermissionChecker permissionChecker;

    public AuthorizeExpression(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }

    /**
     * 允许匿名访问
     */
    public boolean anonymous() {
        return true;
    }

    public boolean permission(String permissionName) {
        return permissionChecker.hasPermission(permissionName);
    }

    /**
     * 是否有当前地址的权限即可访问
     */
    public boolean url() {
        String requestURI = RequestUtil.getRequest().getRequestURI();
        return permissionChecker.hasUrl(requestURI);
    }

    /**
     * 指定角色才能访问
     */
    public boolean role(String role) {
        return anyRole(role);
    }

    /**
     * 指定的任意角色均可访问
     */
    public boolean anyRole(String... matchRoles) {
        return permissionChecker.hasAnyRole(matchRoles);
    }
}
