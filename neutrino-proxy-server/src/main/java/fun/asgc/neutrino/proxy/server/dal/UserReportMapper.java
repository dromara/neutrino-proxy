package fun.asgc.neutrino.proxy.server.dal;

import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Param;
import fun.asgc.neutrino.core.aop.Intercept;
import fun.asgc.neutrino.core.db.page.Page;
import fun.asgc.neutrino.proxy.server.controller.res.UserFlowReportRes;

import java.util.Date;

/**
 * @author: aoshiguchen
 * @date: 2023/1/18
 */
@Intercept(ignoreGlobal = true)
@Component
public interface UserReportMapper {

    void userFlowReportPage(Page<UserFlowReportRes> page, @Param("todayBegin") Date  todayBegin, @Param("todayEnd") Date todayEnd);

}
