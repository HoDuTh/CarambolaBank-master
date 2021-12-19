package com.thuan.carambola.controller;

import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
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

    private void validation()
    {
        ngayBatDau.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue.isAfter(LocalDate.now())) {
                FXAlerts.warning("Lựa chọn ngày không phù hợp. " +
                        "Mặc định là ngày giờ hiện tại");
                ngayBatDau.setValue(LocalDate.now());
            }
            if (newValue.isAfter(ngayKetThuc.getValue())) {
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
            if (newValue.isAfter(ngayBatDau.getValue())) {
                FXAlerts.warning("Ngày kết thúc không thể trước ngày bắt đầu được");
                ngayKetThuc.setValue(ngayBatDau.getValue());
            }
        });
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        validation();
    }

    public ReportController(PhanManhRepository phanManhRepository) {
        super(phanManhRepository);
    }
}
