package fun.asgc.neutrino.proxy.server.base.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import fun.asgc.neutrino.core.db.template.JdbcTemplate;
import fun.asgc.neutrino.proxy.server.constant.DbTypeEnum;
import org.apache.ibatis.solon.annotation.Db;
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

    @Bean(value = "db", typed = true)
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
    public JdbcTemplate jdbcTemplate(@Inject("db") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public void db1_ext(@Db("db") GlobalConfig globalConfig) {
        MetaObjectHandler metaObjectHandler = new MetaObjectHandlerImpl();
        globalConfig.setMetaObjectHandler(metaObjectHandler);
    }

    @Bean
    public void db1_ext2(@Db("db") MybatisConfiguration config){
        config.getTypeHandlerRegistry().register("fun.asgc.neutrino.proxy.server.dal");
        config.setDefaultEnumTypeHandler(null);
    }

    @Bean
    public MybatisSqlSessionFactoryBuilder factoryBuilderNew(){
        return new MybatisSqlSessionFactoryBuilderImpl();
    }

}
