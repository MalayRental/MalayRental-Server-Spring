package com.malayrental.malayrentalserver;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionChecker implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseConnectionChecker(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            jdbcTemplate.execute("SELECT 1");
            System.out.println("数据库连接成功");
        } catch (Exception e) {
            System.out.println("数据库连接失败: " + e.getMessage());
        }
    }

    @PostConstruct
    public void printDbName() {
        String dbName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        System.out.println("当前连接的数据库：" + dbName);
    }
}
