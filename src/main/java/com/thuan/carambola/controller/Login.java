package com.thuan.carambola.controller;

import com.thuan.carambola.CarambolaApplication;
import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entitygeneral.ThongTinDangNhap;
import com.thuan.carambola.entityprimary.VDsPhanmanhEntity;
import com.thuan.carambola.repositorygeneral.ThongTinDangNhapRepository;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class Login implements Initializable {

    PhanManhRepository phanManhRepository;
    ThongTinDangNhapRepository thongTinDangNhapRepository;
    Logger log = LoggerFactory.getLogger(Login.class);

    @FXML private Button btnExit;
    @FXML private Button btnLogin;
    @FXML private ComboBox<VDsPhanmanhEntity> cbChiNhanh;
    @FXML private Label lbError;
    @FXML private PasswordField tfPassword;
    @FXML private TextField tfUsername;

    @Autowired
    public Login(PhanManhRepository phanManhRepository,
                 ThongTinDangNhapRepository thongTinDangNhapRepository)
    {
        this.thongTinDangNhapRepository = thongTinDangNhapRepository;
        this.phanManhRepository = phanManhRepository;
    }
    static void initPhanManh(PhanManhRepository phanManhRepository, ComboBox<VDsPhanmanhEntity> cbChiNhanh)
    {
        List<VDsPhanmanhEntity> list = phanManhRepository.findAll();
        ObservableList<VDsPhanmanhEntity> options =
                FXCollections.observableArrayList(list);
        cbChiNhanh.setItems(options);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPhanManh(phanManhRepository, cbChiNhanh);
        btnLogin.setOnAction(this::buttonLogin);
        btnExit.setOnAction(this::btnExit);
    }
    @Value("${server1.port}")
    String portServer1;
    @Value("${server2.port}")
    String portServer2;
    void btnExit(ActionEvent actionEvent)
    {
        JavaFXApplication.applicationContext.close();
        Platform.exit();
    }
    @FXML @Transactional(readOnly = true)
    void buttonLogin(ActionEvent actionEvent)  {
        if(cbChiNhanh.getSelectionModel().isEmpty())
        {
            FXAlerts.warning("Vui lòng chọn chi nhánh");
            return;
        }
        String server = cbChiNhanh.getSelectionModel().getSelectedItem().getTenserver();
        String phanmanh = cbChiNhanh.getSelectionModel().getSelectedItem().getTencn();
        String username = tfUsername.getText();
        String password = tfPassword.getText();
        if(username.isEmpty()||username.isBlank()||password.isEmpty()||password.isBlank())
        {
            FXAlerts.warning("Vui lòng điền đủ thông tin đăng nhập");
            return;
        }
        String scene = "chuyenTien";
        String port = switch (phanmanh) {
            case "Bến Thành" -> portServer1;
            case "Tân Định" -> portServer2;
            default -> "0";
        };
        log.info(phanmanh);
        String url = String.format("jdbc:sqlserver://%s:%s;database=NGANHANG", server, port);
        log.info(String.format("Log to database(%s:%s): {%s}", username, password, url) );
        Platform.runLater(() -> {
            btnLogin.setDisable(true);
            try {
                CarambolaApplication.tenantManager.removeTenant("tenant");
                CarambolaApplication.tenantManager.addTenant("tenant",
                        url,
                        username,
                        password);
                CarambolaApplication.tenantManager.setCurrentTenant("tenant");
                List<ThongTinDangNhap> listThongTinDangNhap = thongTinDangNhapRepository.get(username);
                if (!listThongTinDangNhap.isEmpty()) {
                    JavaFXApplication.tenNV = listThongTinDangNhap.get(0).getHOTEN();
                    JavaFXApplication.nhom = listThongTinDangNhap.get(0).getTENNHOM();
                    JavaFXApplication.maNV = listThongTinDangNhap.get(0).getUSERNAME();
                    JavaFXApplication.phanManh = phanmanh;
                    JavaFXApplication.server = server;
                    StageInitializer.setScene(scene);
                } else FXAlerts.error(String.format("Lỗi đăng nhập với người dùng '%s'", username));
            } catch (SQLException e) {
                FXAlerts.error(String.format("Lỗi đăng nhập với người dùng '%s'", username));
            } catch (IOException e) {
                FXAlerts.error("Lỗi chuyển scene");
            }
            btnLogin.setDisable(false);
        });
    }
}

