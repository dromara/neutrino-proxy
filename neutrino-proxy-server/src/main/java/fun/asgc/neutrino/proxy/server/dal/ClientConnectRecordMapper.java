package fun.asgc.neutrino.proxy.server.dal;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.asgc.neutrino.core.db.annotation.ResultType;
import fun.asgc.neutrino.core.db.annotation.Select;
import fun.asgc.neutrino.core.db.page.PageInfo;
import fun.asgc.neutrino.proxy.server.controller.req.ClientConnectRecordListReq;
import fun.asgc.neutrino.proxy.server.controller.res.ClientConnectRecordListRes;
import fun.asgc.neutrino.proxy.server.dal.entity.ClientConnectRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: aoshiguchen
 * @date: 2022/11/23
 */
@Mapper
public interface ClientConnectRecordMapper extends BaseMapper<ClientConnectRecordDO> {

    void add(ClientConnectRecordDO clientConnectRecordDO);

    @ResultType(ClientConnectRecordListRes.class)
    @Select("select * from client_connect_record order by id desc")
    void page(PageInfo pageInfo, ClientConnectRecordListReq req);
}
