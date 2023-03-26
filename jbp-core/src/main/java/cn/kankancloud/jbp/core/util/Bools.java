package cn.kankancloud.jbp.core.util;

public class Bools {
    private Bools() {

    }

    public static boolean isTrue(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    public static boolean isFalse(Boolean value) {
        return Boolean.FALSE.equals(value);
    }
}
