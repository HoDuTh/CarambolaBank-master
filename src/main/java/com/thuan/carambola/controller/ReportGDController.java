package com.thuan.carambola.controller;

import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entitygeneral.TaiKhoan;
import com.thuan.carambola.repositorygeneral.TaiKhoanRepository;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class ReportGDController extends ReportController {

    TaiKhoanRepository taiKhoanRepository;
    ObservableList<TaiKhoan> obListTK;
    @FXML
    private TableView<TaiKhoan> tbTaiKhoan;
    @FXML
    private TableColumn<TaiKhoan, String> tc2CMNDTK;
    @FXML
    private TableColumn<TaiKhoan, String> tc2ChiNhanhTK;
    @FXML
    private TableColumn<TaiKhoan, String> tc2NgayMoTK;
    @FXML
    private TableColumn<TaiKhoan, String> tc2SoDuTK;
    @FXML
    private TableColumn<TaiKhoan, String> tc2SoTK;
    @FXML
    private Button btnInKQ;
    @FXML
    private TextField tfSoTK;

    @Autowired
    public ReportGDController(PhanManhRepository phanManhRepository,
                              TaiKhoanRepository taiKhoanRepository) {
        super(phanManhRepository);
        this.taiKhoanRepository = taiKhoanRepository;
    }

    void updateData() {
        List<TaiKhoan> listTK = taiKhoanRepository.findAll();
        obListTK = FXCollections.observableArrayList(listTK);
        initTableView();
    }

    void initTableView() {
        TaiKhoanController.list2TableTK(tc2SoTK, tc2CMNDTK, tc2SoDuTK, tc2ChiNhanhTK, tc2NgayMoTK);
        tbTaiKhoan.setItems(obListTK);
    }
    @Scheduled(fixedRate = reloadTimer)
    public void scheduleTaskWithFixedRate() {
        Platform.runLater(() -> {
            if (StageInitializer.currentResource == StageInitializer.chuyenTien) {
                updateData();
            }
        });
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        updateData();
        initDoubleClickOnTable();
        btnInKQ.setOnAction(this::btnInKQ);
    }

    private void initDoubleClickOnTable()
    {
        tbTaiKhoan.setRowFactory(tv -> {
            TableRow<TaiKhoan> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    TaiKhoan taiKhoan = row.getItem();
                    if (taiKhoan == null) return;
                    boolean check;
                    if (taiKhoan.getId().equals(tfSoTK.getText())) return;
                        check = FXAlerts.confirm(String.format("Lựa chọn tài khoản %s", taiKhoan.getId()));
                        if (!check) return;
                    tfSoTK.setText(taiKhoan.getId());
                }
            });
            return row;
        });
    }
    private void btnInKQ(ActionEvent actionEvent) {

    }
}
