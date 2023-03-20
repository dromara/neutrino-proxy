package fun.asgc.neutrino.proxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 端口池分组枚举类型
 * @author Yohanes
 * @date 2023/03/17
 */
@Getter
@AllArgsConstructor
public enum PoolGroupEnum {
    GLOBAL(0, "全局"),
    USER(1, "用户"),
    LICENSE(2, "License");

    private Integer status;
    private String desc;

}
