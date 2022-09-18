package fun.asgc.neutrino.proxy.server.dal;

import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.aop.Intercept;
import fun.asgc.neutrino.core.db.annotation.Delete;
import fun.asgc.neutrino.core.db.mapper.SqlMapper;

import java.util.Date;

/**
 * @author: aoshiguchen
 * @date: 2022/9/17
 */
@Intercept(ignoreGlobal = true)
@Component
public interface DataCleanMapper extends SqlMapper {

    @Delete("delete from `job_log` where create_time < ?")
    void cleanJobLog(long date);

}
