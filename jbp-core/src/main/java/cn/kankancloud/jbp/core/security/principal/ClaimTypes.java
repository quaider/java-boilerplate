package cn.kankancloud.jbp.core.security.principal;

public final class ClaimTypes {

    private ClaimTypes() {
    }

    static final String CLAIM_TYPE_NAMESPACE = "http://kankancloud.cn/identity/claims";

    public static final String NAME = CLAIM_TYPE_NAMESPACE + "/name";
    public static final String ROLE = CLAIM_TYPE_NAMESPACE + "/role";

    public static final String FULLNAME = CLAIM_TYPE_NAMESPACE + "/fullname";
    public static final String ACCOUNT = CLAIM_TYPE_NAMESPACE + "/account";

}
