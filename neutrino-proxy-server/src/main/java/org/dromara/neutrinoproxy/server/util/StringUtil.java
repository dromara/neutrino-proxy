package org.dromara.neutrinoproxy.server.util;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: aoshiguchen
 * @date: 2023/12/15
 */
public class StringUtil {
    private static final Integer BYTES_MUL_KB = 1024;
    private static final Integer BYTES_MUL_MB = BYTES_MUL_KB * 1024;
    private static final Integer BYTES_MUL_GB = BYTES_MUL_MB * 1024;
    private static final String[] BYTES_UNIT_STR = {"B", "K", "KB", "M", "MB", "G", "GB"};
    private static final Integer[] BYTES_UNIT_MUL = {1, BYTES_MUL_KB, BYTES_MUL_KB, BYTES_MUL_MB, BYTES_MUL_MB, BYTES_MUL_GB, BYTES_MUL_GB};
    private static final String BYTES_DESC_REGEX = "\\s*(\\d+\\.*\\d*)\\s*(B|K|KB|M|MB|G|GB)\\s*";
    private static final Pattern BYTES_DESC_PATTERN = Pattern.compile(BYTES_DESC_REGEX);

    /**
     * 校验是否符合字节描述
     * @param desc
     * @return
     */
    public static boolean isBytesDesc(String desc) {
        if (StrUtil.isBlank(desc)) {
            return false;
        }
        return desc.toUpperCase().matches(BYTES_DESC_REGEX);
    }

    /**
     * 解析字节数
     * 支持B、K、KB、M、MB、G、GB 忽略大小写、忽略首尾空格、忽略数字与单位之间的空格
     * @param desc
     * @return
     */
    public static Long parseBytes(String desc) {
        try {
            if (!isBytesDesc(desc)) {
                return null;
            }
            Matcher matcher = BYTES_DESC_PATTERN.matcher(desc.toUpperCase());
            boolean found = matcher.find();
            if (!found) {
                return null;
            }
            Double n = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(2);
            Integer unitIndex = getBytesUnitIndex(unit);
            if (null == unitIndex) {
                return null;
            }

            return (long)(n * BYTES_UNIT_MUL[unitIndex]);
        } catch (Exception e) {
            // ignore
        }

        return null;
    }

    private static Integer getBytesUnitIndex(String unit) {
        if (StrUtil.isBlank(unit)) {
            return null;
        }
        for (int i = 0; i < BYTES_UNIT_STR.length; i++) {
            if (unit.equals(BYTES_UNIT_STR[i])) {
                return i;
            }
        }
        return null;
    }
}
