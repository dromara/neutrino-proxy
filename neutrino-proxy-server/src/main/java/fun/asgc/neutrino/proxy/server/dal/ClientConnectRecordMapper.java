package fun.asgc.neutrino.proxy.server.dal;

import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.aop.Intercept;
import fun.asgc.neutrino.core.db.mapper.SqlMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.ClientConnectRecordDO;

/**
 * @author: aoshiguchen
 * @date: 2022/11/23
 */
@Intercept(ignoreGlobal = true)
@Component
public interface ClientConnectRecordMapper extends SqlMapper {

    void add(ClientConnectRecordDO clientConnectRecordDO);
}
