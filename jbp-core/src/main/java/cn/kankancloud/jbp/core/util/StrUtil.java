package cn.kankancloud.jbp.core.util;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.helpers.MessageFormatter;

import java.util.concurrent.ThreadLocalRandom;

public class StrUtil {

    public static final char U_A = 'A';
    public static final char L_A = 'a';
    public static final char U_Z = 'Z';
    public static final char L_Z = 'z';

    public enum RandomType {
        INT, STRING, ALL;

        public static final String S_INT = "0123456789";
        public static final String S_STR = "ABCDEFGHJLMNPQRSTUVWXYabcdefghjlmnpqrstuvwxy";
        public static final String S_ALL = S_INT + S_STR;
    }

    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    public static boolean isBlank(String cs) {
        int strLen = cs == null ? 0 : cs.length();
        if (strLen == 0) {
            return true;
        } else {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * 去掉指定后缀
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
     */
    public static String removeSuffix(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return "";
        }

        final String str2 = str.toString();
        if (str2.endsWith(suffix.toString())) {
            return subPre(str2, str2.length() - suffix.length());
        }
        return str2;
    }

    /**
     * 日志字符串格式化
     *
     * @param pattern 格式化模式
     * @param args    格式化参数
     * @return 格式化后的字符串
     */
    public static String formatMessage(String pattern, Object... args) {
        if (ObjectUtils.isEmpty(args)) {
            return pattern;
        }

        return MessageFormatter.arrayFormat(pattern, args).getMessage();
    }

    /**
     * 切割指定位置之前部分的字符串
     *
     * @param string  字符串
     * @param toIndex 切割到的位置（不包括）
     * @return 切割后的剩余的前半部分字符串
     */
    public static String subPre(CharSequence string, int toIndex) {
        return sub(string, 0, toIndex);
    }

    public static String sub(CharSequence str, int fromIndex, int toIndex) {
        if (isEmpty(str)) {
            return "";
        }
        int len = str.length();

        if (fromIndex < 0) {
            fromIndex = len + fromIndex;
            if (fromIndex < 0) {
                fromIndex = 0;
            }
        } else if (fromIndex > len) {
            fromIndex = len;
        }

        if (toIndex < 0) {
            toIndex = len + toIndex;
            if (toIndex < 0) {
                toIndex = len;
            }
        } else if (toIndex > len) {
            toIndex = len;
        }

        if (toIndex < fromIndex) {
            int tmp = fromIndex;
            fromIndex = toIndex;
            toIndex = tmp;
        }

        if (fromIndex == toIndex) {
            return "";
        }

        return str.toString().substring(fromIndex, toIndex);
    }

    /**
     * 判断一个字符串是否是数字
     *
     * @param cs the CharSequence to check, may be null
     * @return {boolean}
     */
    public static boolean isNumeric(final String cs) {
        if (isBlank(cs)) {
            return false;
        }

        for (int i = cs.length(); --i >= 0; ) {
            int chr = cs.charAt(i);
            if (chr < 48 || chr > 57) {
                return false;
            }
        }

        return true;
    }

    /**
     * 首字母变小写
     *
     * @param str 字符串
     * @return {String}
     */
    public static String lowerFirst(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= U_A && firstChar <= U_Z) {
            char[] arr = str.toCharArray();
            arr[0] += (L_A - U_A);
            return new String(arr);
        }
        return str;
    }

    /**
     * 首字母变大写
     *
     * @param str 字符串
     * @return {String}
     */
    public static String upperFirst(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= L_A && firstChar <= L_Z) {
            char[] arr = str.toCharArray();
            arr[0] -= (L_A - U_A);
            return new String(arr);
        }
        return str;
    }

    /**
     * 驼峰转下划线
     */
    public static String camelCaseToUnderline(String param) {
        param = lowerFirst(param);
        StringBuilder sb = new StringBuilder(param);
        int temp = 0;
        for (int i = 0; i < param.length(); i++) {
            if (Character.isUpperCase(param.charAt(i))) {
                sb.insert(i + temp, "_");
                temp += 1;
            }
        }

        return sb.toString().toLowerCase();
    }

    /**
     * 获取标识符，用于参数清理
     *
     * @param param 参数
     * @return 清理后的标识符
     */
    public static String cleanIdentifier(String param) {
        if (param == null) {
            return null;
        }
        StringBuilder paramBuilder = new StringBuilder();
        for (int i = 0; i < param.length(); i++) {
            char c = param.charAt(i);
            if (Character.isJavaIdentifierPart(c)) {
                paramBuilder.append(c);
            }
        }
        return paramBuilder.toString();
    }

    /**
     * 随机数生成
     *
     * @param count 字符长度
     * @return 随机数
     */
    public static String random(int count) {
        return random(count, RandomType.ALL);
    }

    /**
     * 随机数生成
     *
     * @param count      字符长度
     * @param randomType 随机数类别
     * @return 随机数
     */
    public static String random(int count, RandomType randomType) {
        if (count == 0) {
            return "";
        }

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        char[] buffer = new char[count];
        for (int i = 0; i < count; i++) {
            if (RandomType.INT == randomType) {
                buffer[i] = RandomType.S_INT.charAt(random.nextInt(RandomType.S_INT.length()));
            } else if (RandomType.STRING == randomType) {
                buffer[i] = RandomType.S_STR.charAt(random.nextInt(RandomType.S_STR.length()));
            } else {
                buffer[i] = RandomType.S_ALL.charAt(random.nextInt(RandomType.S_ALL.length()));
            }
        }

        return new String(buffer);
    }
}
