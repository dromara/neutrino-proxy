package fun.asgc.neutrino.proxy.server.dal;

import fun.asgc.neutrino.core.db.annotation.ResultType;
import fun.asgc.neutrino.core.db.annotation.Select;
import fun.asgc.neutrino.core.db.page.Page;
import fun.asgc.neutrino.proxy.server.controller.req.ClientConnectRecordListReq;
import fun.asgc.neutrino.proxy.server.controller.res.ClientConnectRecordListRes;
import fun.asgc.neutrino.proxy.server.dal.entity.ClientConnectRecordDO;

/**
 * @author: aoshiguchen
 * @date: 2022/11/23
 */
public interface ClientConnectRecordMapper {

    void add(ClientConnectRecordDO clientConnectRecordDO);

    @ResultType(ClientConnectRecordListRes.class)
    @Select("select * from client_connect_record order by id desc")
    void page(Page page, ClientConnectRecordListReq req);
}
