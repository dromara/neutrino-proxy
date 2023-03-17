package fun.asgc.neutrino.proxy.server.dal;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import fun.asgc.neutrino.proxy.server.controller.req.PortGroupListReq;
import fun.asgc.neutrino.proxy.server.controller.res.PortGroupListRes;
import fun.asgc.neutrino.proxy.server.dal.entity.PortGroupDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;


@Mapper
public interface PortGroupMapper extends BaseMapper<PortGroupDO> {

    List<PortGroupListRes> selectPortGroupListResList(PortGroupListReq res);


   default void updateEnableStatus(Integer id, Integer enable, Date now){
       this.update(null, Wrappers.lambdaUpdate(PortGroupDO.class)
               .eq(PortGroupDO::getId,id)
               .set(PortGroupDO::getEnable,enable)
               .set(PortGroupDO::getUpdateTime,now)
       );
   }
}
