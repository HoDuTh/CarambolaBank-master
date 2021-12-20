package com.thuan.carambola;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

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
        nhom = "NganHang";
        phanManh = "Bến Thành";
        server = "DLI323\\SERVER1";
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
                .headless(false)
                .run();
    }
    public static void removeBean()
    {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
        for(String beanName : applicationContext.getBeanDefinitionNames()){
            System.out.println(beanName);
          //  registry.removeBeanDefinition(beanName);
        }
        registry.removeBeanDefinition("generalTransactionManager");
        BeanDefinitionBuilder.genericBeanDefinition().addAutowiredProperty("").getBeanDefinition();
    }

    public static void addBeanDefinition()
    {

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
