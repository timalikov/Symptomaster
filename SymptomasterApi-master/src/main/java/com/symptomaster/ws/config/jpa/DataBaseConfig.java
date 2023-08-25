package com.symptomaster.ws.config.jpa;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.beans.PropertyVetoException;

/**
 * Created by Maxim Kasyanov on 05/02/16.
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:config.properties")
public class DataBaseConfig {

    @Value("${core.jdbc.driverName}")
    private String driverName;

    @Value("${core.jdbc.url}")
    private String url;

    @Value("${core.jdbc.host}")
    private String host;

    @Value("${core.jdbc.port}")
    private String port;

    @Value("${core.jdbc.db}")
    private String db;

    @Value("${core.jdbc.user}")
    private String user;

    @Value("${core.jdbc.password}")
    private String password;

    @Value("${core.jdbc.pool.acquireIncrement}")
    private int acquireIncrement;

    @Value("${core.jdbc.pool.maxPoolSize}")
    private int maxPoolSize;

    @Value("${core.jdbc.pool.minPoolSize}")
    private int minPoolSize;

    @Value("${core.jdbc.pool.initialPoolSize}")
    private int initialPoolSize;

    @Bean
    public ComboPooledDataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();

        dataSource.setDriverClass(driverName);
        dataSource.setJdbcUrl(url + host + ":" + port + "/" + db + "?characterEncoding=UTF-8");
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setAcquireIncrement(acquireIncrement);
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setMinPoolSize(minPoolSize);
        dataSource.setInitialPoolSize(initialPoolSize);

        return dataSource;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAcquireIncrement() {
        return acquireIncrement;
    }

    public void setAcquireIncrement(int acquireIncrement) {
        this.acquireIncrement = acquireIncrement;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }
}
