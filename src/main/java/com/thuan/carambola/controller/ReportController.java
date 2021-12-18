package com.thuan.carambola.controller;

import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public abstract class ReportController extends BaseController {
    @FXML DatePicker ngayBatDau;
    @FXML DatePicker ngayKetThuc;
    @FXML TextField soTK;


    public ReportController(PhanManhRepository phanManhRepository) {
        super(phanManhRepository);
    }
    //---------------------------------------------------------Bỏ qua----------
    @Override
    void updateData() {
    }
    @Override
    void filtered() {

    }
    @Override
    void initTableView() {

    }
    @Override
    void btnThem(ActionEvent actionEvent) {

    }
    @Override
    void btnXoa(ActionEvent actionEvent) {
    }
    @Override
    void btnSua(ActionEvent actionEvent) {
    }
    @Override
    void btnGhi(ActionEvent actionEvent) {
    }
    @Override
    void btnHoanTac(ActionEvent actionEvent) {

    }
    @Override
    void initCBChiNhanhEvent() {

    }
    @Override
    void unFiltered() {

    }
    @Override
    void initTableEvent() {

    }
    @Override
    void initValidation() {
    }
}
