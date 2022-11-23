package fun.asgc.neutrino.proxy.server.dal.entity;

import fun.asgc.neutrino.core.db.annotation.Id;
import fun.asgc.neutrino.core.db.annotation.Table;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author: aoshiguchen
 * @date: 2022/11/23
 */
@ToString
@Accessors(chain = true)
@Data
@Table("client_connect_record")
public class ClientConnectRecordDO {
    @Id
    private Integer id;
    private Integer userId;
    private String ip;
    private Integer licenseId;
    private String licenseKey;
    private Integer type;
    private String msg;
    /**
     * 1、成功
     * 2、失败
     */
    private Integer code;
    private String err;
    /**
     * 创建时间
     */
    private Date createTime;
}
