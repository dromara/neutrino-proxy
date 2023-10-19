package org.dromara.neutrinoproxy.server.dal;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.dromara.neutrinoproxy.server.controller.req.system.PortGroupListReq;
import org.dromara.neutrinoproxy.server.controller.res.system.PortGroupListRes;
import org.dromara.neutrinoproxy.server.dal.entity.PortGroupDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;


@Mapper
public interface PortGroupMapper extends BaseMapper<PortGroupDO> {

    List<PortGroupListRes> selectPortGroupListResList(IPage<PortGroupListRes> page, PortGroupListReq res);


   default void updateEnableStatus(Integer id, Integer enable, Date now){
       this.update(null, Wrappers.lambdaUpdate(PortGroupDO.class)
               .eq(PortGroupDO::getId,id)
               .set(PortGroupDO::getEnable,enable)
               .set(PortGroupDO::getUpdateTime,now)
       );
   }
}
