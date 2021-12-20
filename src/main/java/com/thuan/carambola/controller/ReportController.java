package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.FlowPane;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
public abstract class ReportController extends MenuController {
    @FXML
    DatePicker ngayBatDau;
    @FXML
    DatePicker ngayKetThuc;
    @FXML FlowPane pnCN;
    private void validation()
    {
        ngayBatDau.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue.isAfter(LocalDate.now())) {
                FXAlerts.warning("Lựa chọn ngày không phù hợp. " +
                        "Mặc định là ngày giờ hiện tại");
                ngayBatDau.setValue(LocalDate.now());
            }
            if (ngayKetThuc.getValue()!= null && newValue.isAfter(ngayKetThuc.getValue())) {
                FXAlerts.warning("Ngày bắt đầu không thể sau ngày kết thúc được");
                ngayBatDau.setValue(ngayKetThuc.getValue());
            }
        });
        ngayKetThuc.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue.isAfter(LocalDate.now())) {
                FXAlerts.warning("Lựa chọn ngày không phù hợp. " +
                        "Mặc định là ngày giờ hiện tại");
                ngayKetThuc.setValue(LocalDate.now());
            }
            if (ngayBatDau.getValue()!= null &&newValue.isBefore(ngayBatDau.getValue())) {
                FXAlerts.warning("Ngày kết thúc không thể trước ngày bắt đầu được");
                ngayBatDau.setValue(LocalDate.now());
                ngayKetThuc.setValue(LocalDate.now());
            }
        });
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        phanQuyen();
        validation();
        ngayBatDau.setValue(LocalDate.now().minusDays(7));
        ngayKetThuc.setValue(LocalDate.now());
    }
    void phanQuyen()
    {
        String permissions = JavaFXApplication.nhom;
        if ("NganHang".equals(permissions)) {
            pnCN.setDisable(false);
        }
    }
    public ReportController(PhanManhRepository phanManhRepository) {
        super(phanManhRepository);
    }
}
