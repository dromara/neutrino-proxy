package fun.asgc.neutrino.proxy.server.util;

/**
 * @author: aoshiguchen
 * @date: 2023/3/19
 */
public class FormatUtil {
    private static final String[] SIZE_UNINTS = {"B", "KB", "MB", "GB", "TB"};
    private static final int SIZE_SYSTEM = 1024;
    /**
     * 根据字节数获取大小描述
     * 1、小于1024字节的以B为单位
     * 2、小于1024KB的以KB为单位
     * 3、小于1024M的以MB为单位
     * 4、小于1024G的以GB为单位
     * 5、其他以TB为单位
     * @param byteCount
     * @return
     */
    public static String getSizeDescByByteCount(long byteCount){
        if(byteCount <= 0){
            return "0B";
        }

        double res = byteCount;
        int index = 0;
        while (index < SIZE_UNINTS.length && res >= SIZE_SYSTEM){
            res /= SIZE_SYSTEM;
            index++;
        }

        if(index >= SIZE_UNINTS.length){
            index = SIZE_UNINTS.length - 1;
            res *= 1024;
        }

        return trimZero(String.format("%.2f", res)) + " " + SIZE_UNINTS[index];
    }

    private static String trimZero(String s) {
        if (s.indexOf(".") > 0) {
            // 去掉多余的0
            s = s.replaceAll("0+?$", "");
            // 如最后一位是.则去掉
            s = s.replaceAll("[.]$", "");
        }
        return s;
    }
}
