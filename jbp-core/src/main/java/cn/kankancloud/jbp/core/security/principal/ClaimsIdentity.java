package cn.kankancloud.jbp.core.security.principal;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClaimsIdentity implements IIdentity {

    public static final String DEFAULT_NAME_CLAIM_TYPE = ClaimTypes.NAME;
    public static final String DEFAULT_ROLE_CLAIM_TYPE = ClaimTypes.ROLE;

    private final String authenticationType;

    @Getter
    private String nameClaimType = DEFAULT_NAME_CLAIM_TYPE;

    @Getter
    private String roleClaimType = DEFAULT_ROLE_CLAIM_TYPE;
    private final List<Claim> instanceClaims = new ArrayList<>();

    public ClaimsIdentity() {
        this(null, null, null, null, null);
    }

    public ClaimsIdentity(IIdentity identity) {
        this(identity, null, null, null, null);
    }

    public ClaimsIdentity(String authenticationType) {
        this(null, null, authenticationType, null, null);
    }

    public ClaimsIdentity(List<Claim> claims, String authenticationType) {
        this(null, claims, authenticationType, null, null);
    }

    public ClaimsIdentity(IIdentity identity, List<Claim> claims) {
        this(identity, claims, null, null, null);
    }

    public ClaimsIdentity(String authenticationType, String nameType, String roleType) {
        this(null, null, authenticationType, nameType, roleType);
    }

    public ClaimsIdentity(List<Claim> claims, String authenticationType, String nameType, String roleType) {
        this(null, claims, authenticationType, nameType, roleType);
    }

    public ClaimsIdentity(IIdentity identity, List<Claim> claims, String authenticationType, String nameType, String roleType) {
        ClaimsIdentity claimsIdentity = null;
        if (identity instanceof ClaimsIdentity) {
            claimsIdentity = (ClaimsIdentity) identity;
        }

        if (identity != null && StringUtils.isEmpty(authenticationType)) {
            this.authenticationType = identity.authenticationType();
        } else {
            this.authenticationType = authenticationType;
        }

        if (!StringUtils.isEmpty(nameType)) {
            this.nameClaimType = nameType;
        } else if (claimsIdentity != null) {
            this.nameClaimType = claimsIdentity.nameClaimType;
        }

        if (!StringUtils.isEmpty(roleType)) {
            this.roleClaimType = roleType;
        } else if (claimsIdentity != null) {
            this.roleClaimType = claimsIdentity.roleClaimType;
        }

        if (claimsIdentity != null) {
            instanceClaims.addAll(claimsIdentity.instanceClaims);
        } else if (identity != null && !StringUtils.isEmpty(identity.name())) {
            instanceClaims.add(new Claim(nameType, identity.name(), ClaimValueTypes.STRING));
        }

        if (claims != null) {
            instanceClaims.addAll(claims);
        }
    }

    public void addClaim(Claim claim) {
        if (claim == null) {
            throw new IllegalArgumentException("claim");
        }

        if (instanceClaims.stream().noneMatch(f -> f == claim)) {
            instanceClaims.add(claim);
        }
    }

    public void removeClaim(Claim claim) {
        if (claim == null) {
            throw new IllegalArgumentException("claim");
        }

        instanceClaims.remove(claim);
    }

    public List<Claim> claims(Predicate<Claim> predicate) {
        if (predicate == null) {
            return instanceClaims;
        }

        return instanceClaims.stream().filter(predicate).collect(Collectors.toList());
    }

    public List<Claim> claims(String type) {
        if (StringUtils.isEmpty(type)) {
            throw new IllegalArgumentException("type");
        }

        return claims(f -> f.getType().equalsIgnoreCase(type));
    }

    public Claim claimFirst(String type) {
        if (StringUtils.isEmpty(type)) {
            throw new IllegalArgumentException("type");
        }

        List<Claim> claims = claims(f -> f.getType().equalsIgnoreCase(type));
        if (claims != null && !claims.isEmpty()) {
            return claims.get(0);
        }

        return null;
    }

    @Override
    public String name() {
        Claim claim = claimFirst(nameClaimType);
        if (claim != null) {
            return claim.getValue();
        }

        return null;
    }

    @Override
    public String authenticationType() {
        return authenticationType;
    }

    @Override
    public boolean isAuthenticated() {
        return !StringUtils.isEmpty(authenticationType);
    }

    public boolean hasClaim(String type, String value) {
        if (type == null) {
            throw new IllegalArgumentException("type");
        }

        if (value == null) {
            throw new IllegalArgumentException("value");
        }

        for (Claim claim : instanceClaims) {
            if (claim != null
                    && StringUtils.equalsIgnoreCase(claim.getType(), type)
                    && StringUtils.equals(claim.getValue(), value)) {
                return true;
            }
        }

        return false;
    }
}
