package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.entitygeneral.KhachHang;
import com.thuan.carambola.entitygeneral.TaiKhoan;
import com.thuan.carambola.repositorygeneral.TaiKhoanRepository;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

@Component
public class ReportGDController extends MenuController {

    @FXML private TableView<TaiKhoan> tbTaiKhoan;
    @FXML private TableColumn<TaiKhoan, String> tc2CMNDTK;
    @FXML private TableColumn<TaiKhoan, String> tc2ChiNhanhTK;
    @FXML private TableColumn<TaiKhoan, String> tc2NgayMoTK;
    @FXML private TableColumn<TaiKhoan, String> tc2SoDuTK;
    @FXML private TableColumn<TaiKhoan, String> tc2SoTK;

    @FXML private Button btnInKQ;

    TaiKhoanRepository taiKhoanRepository;
    ObservableList<TaiKhoan> obListTK;

    @Autowired
    public ReportGDController(TaiKhoanRepository taiKhoanRepository)
    {
        this.taiKhoanRepository = taiKhoanRepository;
    }
    void updateData()
    {
        List<TaiKhoan> listTK = taiKhoanRepository.findAll();
        obListTK = FXCollections.observableArrayList(listTK);
        initTableView();
    }
    void initTableView() {
        TaiKhoanController.list2TableTK(tc2SoTK, tc2CMNDTK, tc2SoDuTK, tc2ChiNhanhTK, tc2NgayMoTK);
        tbTaiKhoan.setItems(obListTK);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        updateData();
        btnInKQ.setOnAction(this::btnInKQ);
    }

    private void btnInKQ(ActionEvent actionEvent) {

    }
}
