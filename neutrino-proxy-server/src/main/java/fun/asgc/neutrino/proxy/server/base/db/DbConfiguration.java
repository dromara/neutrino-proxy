package fun.asgc.neutrino.proxy.server.base.db;

import com.alibaba.druid.pool.DruidDataSource;
import fun.asgc.neutrino.core.db.template.JdbcTemplate;
import fun.asgc.neutrino.proxy.server.base.rest.config.DbConfig;
import fun.asgc.neutrino.proxy.server.constant.DbTypeEnum;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

/**
 * @author: aoshiguchen
 * @date: 2023/3/10
 */
@Configuration
public class DbConfiguration {

    @Bean(value = "dataSource", typed = true)
    public DataSource dataSource(@Inject DbConfig dbConfig) {
        DbTypeEnum dbTypeEnum = DbTypeEnum.of(dbConfig.getType());
        if (DbTypeEnum.SQLITE == dbTypeEnum) {
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl(dbConfig.getUrl());
            dataSource.setJournalMode(SQLiteConfig.JournalMode.WAL.getValue());
            return dataSource;
        } else if (DbTypeEnum.MYSQL == dbTypeEnum) {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setDriverClassName(dbConfig.getDriverClass());
            dataSource.setUrl(dbConfig.getUrl());
            dataSource.setInitialSize(5);
            dataSource.setMinIdle(5);
            dataSource.setMaxActive(20);
            dataSource.setMaxWait(60000);
            dataSource.setPoolPreparedStatements(true);
            dataSource.setUsername(dbConfig.getUsername());
            dataSource.setPassword(dbConfig.getPassword());
            return dataSource;
        }

        return null;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Inject("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
