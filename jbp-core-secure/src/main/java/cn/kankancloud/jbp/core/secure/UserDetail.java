package cn.kankancloud.jbp.core.secure;

import com.google.common.collect.Lists;

import java.util.List;

public interface UserDetail {
    String identity();

    String account();

    String fullname();

    default List<String> departments() {
        return Lists.newArrayList();
    }
}
