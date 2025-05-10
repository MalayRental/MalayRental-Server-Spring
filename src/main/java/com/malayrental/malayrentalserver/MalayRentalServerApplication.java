package com.malayrental.malayrentalserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.malayrental.malayrentalserver.service.UserAccountService;

@SpringBootApplication
public class MalayRentalServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MalayRentalServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner setAllUserOffline(UserAccountService userAccountService) {
        return args -> userAccountService.setAllUserOffline();
    }
}

