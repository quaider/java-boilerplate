package cn.kankancloud.jbp.core.secure;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthorizeStrategy {
    ROLE("role"), PERMISSION("permission"), URL("url");

    private final String funcName;
}
