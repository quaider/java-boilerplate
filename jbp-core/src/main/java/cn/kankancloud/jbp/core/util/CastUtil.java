package cn.kankancloud.jbp.core.util;

import java.util.Arrays;
import java.util.List;

public class CastUtil {

    private CastUtil() {
    }

    public static int toInt(final Object value) {
        return NumberUtil.toInt(String.valueOf(value));
    }

    public static int toInt(final Object value, final int defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        return NumberUtil.toInt(String.valueOf(value), defaultValue);
    }

    public static long toLong(final Object value) {
        return NumberUtil.toLong(String.valueOf(value));
    }

    public static long toLong(final Object value, final long defaultValue) {
        return NumberUtil.toLong(value == null ? null : String.valueOf(value), defaultValue);
    }

    public static Double toDouble(Object value) {
        return toDouble(String.valueOf(value), -1.00);
    }

    public static Double toDouble(Object value, Double defaultValue) {
        return NumberUtil.toDouble(String.valueOf(value), defaultValue);
    }

    public static Float toFloat(Object value) {
        return toFloat(String.valueOf(value), -1.0f);
    }

    public static Float toFloat(Object value, Float defaultValue) {
        return NumberUtil.toFloat(String.valueOf(value), defaultValue);
    }

    public static Boolean toBoolean(Object value) {
        return toBoolean(value, null);
    }

    public static Boolean toBoolean(Object value, Boolean defaultValue) {
        if (value != null) {
            String val = String.valueOf(value);
            val = val.toLowerCase().trim();
            return Boolean.parseBoolean(val);
        }
        return defaultValue;
    }

    public static boolean isTrue(Boolean value) {
        return value != null && value;
    }

    public static boolean isFalse(Boolean value) {
        return !isTrue(value);
    }

    public static Integer[] toIntArray(String str) {
        return toIntArray(",", str);
    }

    public static Integer[] toIntArray(String split, String str) {
        if (StrUtil.isEmpty(str)) {
            return new Integer[]{};
        }
        String[] arr = str.split(split);
        final Integer[] ints = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            final int v = toInt(arr[i], 0);
            ints[i] = v;
        }
        return ints;
    }

    public static List<Integer> toIntList(String str) {
        return Arrays.asList(toIntArray(str));
    }

    public static List<Integer> toIntList(String split, String str) {
        return Arrays.asList(toIntArray(split, str));
    }

    public static Long[] toLongArray(String str) {
        return toLongArray(",", str);
    }

    public static Long[] toLongArray(String split, String str) {
        if (StrUtil.isEmpty(str)) {
            return new Long[]{};
        }
        String[] arr = str.split(split);
        final Long[] longs = new Long[arr.length];
        for (int i = 0; i < arr.length; i++) {
            final long v = toLong(arr[i], 0);
            longs[i] = v;
        }
        return longs;
    }

    public static List<Long> toLongList(String str) {
        return Arrays.asList(toLongArray(str));
    }

    public static List<Long> toLongList(String split, String str) {
        return Arrays.asList(toLongArray(split, str));
    }

    public static String[] toStrArray(String str) {
        return toStrArray(",", str);
    }

    public static String[] toStrArray(String split, String str) {
        if (StrUtil.isBlank(str)) {
            return new String[]{};
        }
        return str.split(split);
    }

    public static List<String> toStrList(String str) {
        return Arrays.asList(toStrArray(str));
    }

    public static List<String> toStrList(String split, String str) {
        return Arrays.asList(toStrArray(split, str));
    }

    /**
     * 强转string,并去掉多余空格
     *
     * @param str 字符串
     * @return String
     */
    public static String toStr(Object str) {
        return toStr(str, "");
    }

    /**
     * 强转string,并去掉多余空格
     *
     * @param str          字符串
     * @param defaultValue 默认值
     * @return String
     */
    public static String toStr(Object str, String defaultValue) {
        if (null == str) {
            return defaultValue;
        }

        return String.valueOf(str);
    }
}
