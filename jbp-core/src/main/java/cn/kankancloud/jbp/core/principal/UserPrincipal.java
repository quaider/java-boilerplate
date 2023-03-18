package cn.kankancloud.jbp.core.principal;


import cn.kankancloud.jbp.core.exception.BizAuthenticateException;
import cn.kankancloud.jbp.core.util.StrUtil;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 代表用户认证主体
 */
@Getter
public class UserPrincipal {
    private final String identity;
    private final String account;
    private final String fullName;

    public UserPrincipal(String identity, String account, String fullName) {
        if (StrUtil.isBlank(identity) || StrUtil.isBlank(account) || StrUtil.isBlank(fullName)) {
            throw new BizAuthenticateException("用户身份信息有误");
        }

        this.identity = identity;
        this.account = account;
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserPrincipal that = (UserPrincipal) o;

        return new EqualsBuilder().append(identity, that.identity).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(identity).toHashCode();
    }
}
