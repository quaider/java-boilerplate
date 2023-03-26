package cn.kankancloud.jbp.core.security.principal;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * describe properties of the principal.
 * these normally consist of a type and a value, but can be a name only.
 */
@Getter
public class Claim {
    private final String type;
    private final String value;
    private final String valueType;
    private final Map<String, String> properties = new HashMap<>();

    public Claim(String type, String value) {
        this(type, value, ClaimValueTypes.STRING);
    }

    public Claim(String type, String value, String valueType) {
        this(type, value, valueType, null, null);
    }

    public Claim(String type, String value, String valueType, String propertyKey, String propertyValue) {

        if (type == null) {
            throw new IllegalArgumentException("type");
        }

        if (value == null) {
            throw new IllegalArgumentException("value");
        }

        this.type = type;
        this.value = value;
        this.valueType = StringUtils.isEmpty(value) ? ClaimValueTypes.STRING : valueType;

        if (!StringUtils.isEmpty(propertyKey)) {
            properties.put(propertyKey, propertyValue);
        }
    }

    public void addProperty(String propertyKey, String propertyValue) {
        if (!StringUtils.isEmpty(propertyKey)) {
            properties.put(propertyKey, propertyValue);
        }
    }

    public String getProperty(String propertyKey) {
        return properties.get(propertyKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Claim claim = (Claim) o;

        return new EqualsBuilder().append(type, claim.type).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(type).toHashCode();
    }
}
