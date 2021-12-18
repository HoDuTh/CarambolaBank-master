package com.thuan.carambola;

import com.thuan.carambola.entityprimary.VDsPhanmanhEntity;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class JavaFXApplication extends Application {
    public static ConfigurableApplicationContext applicationContext;
    public static String maNV;
    public static String tenNV;
    public static String nhom;
    public static String phanManh;
    public static String server;

    {
        maNV = "0002";
        tenNV = "NONE";
        nhom = "ChiNhanh";
        phanManh = "Bến Thành";
        server = "BenThanh";
    }
    @Override
    public void init() {
        ApplicationContextInitializer<GenericApplicationContext> initializer =
                context -> {
                    context.registerBean(Parameters.class, this::getParameters);
                    context.registerBean(HostServices.class, this::getHostServices);
                };
        applicationContext = new SpringApplicationBuilder(CarambolaApplication.class)
                .initializers(initializer)
                .run();
    }

    @Override
    public void start(Stage primaryStage) {
        applicationContext.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }

    public static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage primaryStage) {
            super(primaryStage);
        }
        public Stage getStage() {
            return (Stage) getSource();
        }
    }

}
