package fun.asgc.solon.extend.orika;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author: aoshiguchen
 * @date: 2023/3/11
 */
@Configuration
public class OrikaConfiguration {
    @Bean
    public MapperFacade mapperFacade(@Inject MapperFactory factory) {
        return factory.getMapperFacade();
    }
}
