package cn.kankancloud.jbp.core.security.principal;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ClaimsPrincipal implements IPrincipal {

    private static final String FIELD_IDENTITIES = "identities";

    @Getter
    private final List<ClaimsIdentity> identities = new ArrayList<>();

    public ClaimsPrincipal(List<ClaimsIdentity> identities) {
        if (identities == null) {
            throw new IllegalArgumentException(FIELD_IDENTITIES);
        }

        this.identities.addAll(identities);
    }

    public ClaimsPrincipal(IIdentity identity) {
        if (identity == null) {
            throw new IllegalArgumentException("identity");
        }

        if (identity instanceof ClaimsIdentity ci) {
            identities.add(ci);
        } else {
            identities.add(new ClaimsIdentity(identity));
        }
    }

    public ClaimsPrincipal(IPrincipal principal) {
        if (null == principal) {
            throw new IllegalArgumentException("principal");
        }

        if (principal instanceof ClaimsPrincipal cp) {
            if (!cp.identities.isEmpty()) {
                identities.addAll(cp.identities);
            }
        } else {
            identities.add(new ClaimsIdentity(principal.identity()));
        }
    }

    public void addIdentity(ClaimsIdentity identity) {
        if (identity == null) {
            throw new IllegalArgumentException("identity");
        }

        identities.add(identity);
    }

    public void addIdentities(List<ClaimsIdentity> identities) {
        if (identities == null) {
            throw new IllegalArgumentException(FIELD_IDENTITIES);
        }

        this.identities.addAll(identities);
    }

    @Override
    public IIdentity identity() {
        return selectPrimaryIdentity(this.identities);
    }

    @Override
    public boolean isInRole(String role) {
        for (ClaimsIdentity identity : identities) {
            if (identity.hasClaim(identity.getRoleClaimType(), role)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasClaim(String type, String value) {
        for (ClaimsIdentity identity : identities) {
            if (identity.hasClaim(type, value)) {
                return true;
            }
        }

        return false;
    }

    private static ClaimsIdentity selectPrimaryIdentity(List<ClaimsIdentity> identities) {
        if (identities == null) {
            throw new IllegalArgumentException(FIELD_IDENTITIES);
        }

        for (ClaimsIdentity identity : identities) {
            if (identity != null) {
                return identity;
            }
        }

        return null;
    }
}
