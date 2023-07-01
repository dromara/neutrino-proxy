package org.dromara.neutrinoproxy.server.base.db;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.solon.annotation.Db;
import org.dromara.neutrinoproxy.server.constant.DbTypeEnum;
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
            String driver = "com.mysql.cj.jdbc.Driver";
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException e) {
                // 对类名的判断,异常则说明不存在:
                driver = "com.mysql.jdbc.Driver";
            }
            return newHikariDataSource(dbConfig, driver);
        } else if (DbTypeEnum.MARIADB == dbTypeEnum) {
            return newHikariDataSource(dbConfig, "org.mariadb.jdbc.Driver");
        }

        return null;
    }

    private HikariDataSource newHikariDataSource(DbConfig dbConfig, String driverClass) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setJdbcUrl(dbConfig.getUrl());
        dataSource.setMinimumIdle(5);
        dataSource.setMaximumPoolSize(20);
        dataSource.setMaxLifetime(60000);
        dataSource.setUsername(dbConfig.getUsername());
        dataSource.setPassword(dbConfig.getPassword());
        return dataSource;
    }

    @Bean
    public void db1_ext(@Db("db") GlobalConfig globalConfig) {
        MetaObjectHandler metaObjectHandler = new MetaObjectHandlerImpl();
        globalConfig.setMetaObjectHandler(metaObjectHandler);
    }

    @Bean
    public void db1_ext2(@Db("db") MybatisConfiguration config) {
        config.getTypeHandlerRegistry().register("fun.asgc.neutrino.proxy.server.dal");
        config.setDefaultEnumTypeHandler(null);
    }

    @Bean
    public MybatisSqlSessionFactoryBuilder factoryBuilderNew() {
        return new MybatisSqlSessionFactoryBuilderImpl();
    }

}
