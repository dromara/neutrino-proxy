package fun.asgc.neutrino.proxy.server.controller.req.system;

import lombok.Data;

import java.util.List;

/**
 * 批量修改端口分组请求
 */
@Data
public class PortPoolUpdateGroupReq {

    private String groupId;

    private List<Integer> portIdList;

}
