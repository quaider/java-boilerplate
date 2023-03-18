package cn.kankancloud.jbp.web.principal;

import cn.kankancloud.jbp.core.principal.PrincipalFactory;
import cn.kankancloud.jbp.core.principal.UserPrincipal;
import cn.kankancloud.jbp.web.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class HttpHeaderPrincipalFactory implements PrincipalFactory {

    @Override
    public String name() {
        return HttpHeaderPrincipalFactory.class.getSimpleName();
    }

    @Override
    public boolean supported() {
        return RequestUtil.isWebEnvironment();
    }

    @Override
    public UserPrincipal getCurrent() {
        HttpServletRequest request = RequestUtil.getRequest();

        String account = request.getHeader(PrincipalFactory.DEFAULT_ACCOUNT_CLAIM);
        String userId = request.getHeader(PrincipalFactory.DEFAULT_IDENTITY_CLAIM);
        String fullName = RequestUtil.getHeaderWithUrlDecode(request, PrincipalFactory.DEFAULT_FULLNAME_CLAIM);

        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(fullName)) {
            return null;
        }

        return new UserPrincipal(userId, account, fullName);
    }

    @Override
    public void setCurrent(UserPrincipal userPrincipal) {
        // no need to set
    }
}
