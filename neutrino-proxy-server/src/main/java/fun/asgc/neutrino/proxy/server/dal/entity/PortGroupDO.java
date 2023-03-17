package fun.asgc.neutrino.proxy.server.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 端口组
 */
@ToString
@Accessors(chain = true)
@Data
@TableName("port_group")
public class PortGroupDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 所有者类型 (0、全局共享 1、用户所有 2License所有)
     */
    private Integer possessorType;

    /**
     * 所有者id(当type为0时 固定为-1、当type为1时为用户id 、当type为2时为licenseid)
     */
    private Integer possessorId;

    /**
     * 是否启用(1、启用 2、禁用)
     */
    private Integer enable;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
