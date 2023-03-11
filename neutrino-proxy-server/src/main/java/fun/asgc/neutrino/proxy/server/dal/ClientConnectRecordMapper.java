package fun.asgc.neutrino.proxy.server.dal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.ClientConnectRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

/**
 * @author: aoshiguchen
 * @date: 2022/11/23
 */
@Mapper
public interface ClientConnectRecordMapper extends BaseMapper<ClientConnectRecordDO> {
    default void clean(Date date) {
        this.delete(new LambdaQueryWrapper<ClientConnectRecordDO>()
                .lt(ClientConnectRecordDO::getCreateTime, date)
        );
    }
}
