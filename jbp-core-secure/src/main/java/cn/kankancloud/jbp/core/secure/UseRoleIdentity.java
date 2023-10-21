package cn.kankancloud.jbp.core.secure;

import cn.kankancloud.jbp.core.security.principal.IIdentity;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
public class UseRoleIdentity implements IIdentity {

    private final UserDetail userDetail;
    private final List<String> roles;

    public UseRoleIdentity(UserDetail userDetail, List<String> roles) {
        if (userDetail == null) {
            throw new IllegalArgumentException("userDetail");
        }

        this.userDetail = userDetail;
        this.roles = roles;
    }

    @Override
    public String name() {
        return userDetail.getAccount();
    }

    @Override
    public String authenticationType() {
        return "default";
    }

    @Override
    public boolean isAuthenticated() {
        return StringUtils.isNotEmpty(userDetail.getAccount());
    }
}
