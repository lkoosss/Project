package com.example.common.util;

public class StringUtil {

    /**
     * <pre>
     *  isEmpty
     * </pre>
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * <pre>
     *  isNotEmpty
     * </pre>
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * <pre>
     *  isBlank
     * </pre>
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * <pre>
     *  isNotBlank
     * </pre>
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
