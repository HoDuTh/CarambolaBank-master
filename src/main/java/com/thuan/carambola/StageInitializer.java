package com.thuan.carambola;

import com.thuan.carambola.JavaFXApplication.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    private static String applicationTitle;
    private static ApplicationContext applicationContext;
    private static int startHeight;
    private static int startWidth;
    private static Stage primaryStage;
    private static Resource currentResource;

    public static Resource chuyenTien;
    public static Resource login;
    public static Resource taiKhoan;
    public static Resource guiRut;
    public static Resource khachHang;
    public static Resource nhanVien;

    public StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle,
                            ApplicationContext applicationContext,
                            @Value("800") int startHeight,
                            @Value("1518") int startWidth,
                            @Value("classpath:ChuyenTien.fxml")Resource chuyenTien,
                            @Value("classpath:Login.fxml")Resource login,
                            @Value("classpath:TaiKhoan.fxml")Resource taiKhoan,
                            @Value("classpath:GuiRut.fxml")Resource guiRut,
                            @Value("classpath:KhachHang.fxml")Resource khachHang,
                            @Value("classpath:NhanVien.fxml")Resource nhanVien)
    {
        StageInitializer.applicationTitle = applicationTitle;
        StageInitializer.applicationContext = applicationContext;
        StageInitializer.startHeight = startHeight;
        StageInitializer.startWidth = startWidth;
        StageInitializer.chuyenTien = chuyenTien;
        StageInitializer.login = login;
        StageInitializer.taiKhoan = taiKhoan;
        StageInitializer.guiRut = guiRut;
        StageInitializer.khachHang = khachHang;
        StageInitializer.nhanVien = nhanVien;

    }
    public static void setScene(String name) throws IOException {
        Resource resource = switch (name) {
            case "chuyenTien" -> chuyenTien;
            case "taiKhoan" -> taiKhoan;
            case "guiRut" -> guiRut;
            case "khachHang" -> khachHang;
            case "nhanVien" -> nhanVien;
            default -> login;
        };
        setScene(resource);
    }
    public static void setScene(Resource resource) throws IOException {
        if(currentResource == resource) return;
        FXMLLoader loader = new FXMLLoader(resource.getURL());
        currentResource = resource;
        loader.setControllerFactory(applicationContext::getBean);
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent, startWidth, startHeight));
        primaryStage.setTitle(applicationTitle);
        primaryStage.show();
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {
            primaryStage = event.getStage();
            setScene(chuyenTien);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
