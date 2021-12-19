package com.thuan.carambola;

import com.thuan.carambola.config.MultiTenantManager;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EntityScan
@SpringBootApplication
@EnableScheduling
public class CarambolaApplication {
    public static MultiTenantManager tenantManager;

    public CarambolaApplication(MultiTenantManager tenantManager) {
        this.tenantManager = tenantManager;
    }

    public static void main(String[] args) {
        Application.launch(JavaFXApplication.class, args);
    }
}
