package fun.asgc.neutrino.proxy.server.dal;

import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Param;
import fun.asgc.neutrino.core.aop.Intercept;
import fun.asgc.neutrino.core.db.page.PageInfo;
import fun.asgc.neutrino.proxy.server.controller.res.UserFlowReportRes;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

/**
 * @author: aoshiguchen
 * @date: 2023/1/18
 */
@Intercept(ignoreGlobal = true)
@Component
@Mapper
public interface UserReportMapper {

    void userFlowReportPage(PageInfo<UserFlowReportRes> pageInfo, @Param("todayBegin") Date  todayBegin, @Param("todayEnd") Date todayEnd);

}
