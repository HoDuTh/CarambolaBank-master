package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entityprimary.VDsPhanmanhEntity;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

@Component
public abstract class MenuController implements Initializable {
    final long reloadTimer = 300000;
    //-------------------Menu-------------------
    @FXML
    MenuBar menu;
    @FXML
    Menu menuGiaoDich;
    @FXML
    Menu menuGioiThieu;
    @FXML
    Menu menuHeThong;
    @FXML
    Menu menuHuongDan;
    @FXML
    Menu menuKhachHang;
    @FXML
    Menu menuNhanVien;
    @FXML
    Menu menuTaiKhoan;
    @FXML
    Menu menuBaoCaoThongKe;
    @FXML
    MenuItem menuItemChuyenNhanVien;
    @FXML
    MenuItem menuItemChuyenTien;
    @FXML
    MenuItem menuItemGiaoDich;
    @FXML
    MenuItem menuItemGuiRut;
    @FXML
    MenuItem menuItemHeThong;
    @FXML
    MenuItem menuItemKhachHang;
    @FXML
    MenuItem menuItemTaiKhoan;
    @FXML
    MenuItem menuItemNhanVien;
    @FXML
    MenuItem menuItemTaoTKDN;
    @FXML
    MenuItem menuItemDangXuat;
    @FXML
    MenuItem menuItemReportGD;
    @FXML
    MenuItem menuItemReportKH;
    @FXML
    MenuItem menuItemReportTK;

    @FXML ComboBox<VDsPhanmanhEntity> cbChiNhanh;

    PhanManhRepository phanManhRepository;
    public MenuController(PhanManhRepository phanManhRepository) {
        this.phanManhRepository = phanManhRepository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPhanManh(phanManhRepository, cbChiNhanh);
        initMenu();
    }
    void initPhanManh(PhanManhRepository phanManhRepository, ComboBox<VDsPhanmanhEntity> cbChiNhanh) {
        List<VDsPhanmanhEntity> list = phanManhRepository.findAll();
        ObservableList<VDsPhanmanhEntity> options =
                FXCollections.observableArrayList(list);
        cbChiNhanh.setItems(options);
        for(VDsPhanmanhEntity a: list)
        {
            if(Objects.equals(a.getTenserver(), JavaFXApplication.server))
            {
                cbChiNhanh.getSelectionModel().select(a);
            }
        }
    }
    //-------------------Menu-------------------
    void initMenu() {
        menuItemDangXuat.setOnAction(this::setMenuItemDangXuat);
        menuItemChuyenTien.setOnAction(this::setMenuItemChuyenTien);
        menuItemTaiKhoan.setOnAction(this::setMenuItemTaiKhoan);
        menuItemGuiRut.setOnAction(this::setMenuItemGuiRut);
        menuItemNhanVien.setOnAction(this::setMenuItemNhanVien);
        menuItemKhachHang.setOnAction(this::setMenuItemKhachHang);
        menuItemReportGD.setOnAction(this::setMenuItemReportGD);
        menuItemReportKH.setOnAction(this::setMenuItemReportKH);
        menuItemReportTK.setOnAction(this::setMenuItemReportTK);
    }

    private void setMenuItemChuyenTien(ActionEvent actionEvent) {
        try {
            StageInitializer.setScene(StageInitializer.chuyenTien);
        } catch (IOException e) {
            FXAlerts.error("Trang này đang không hoạt động");
        }
    }

    private void setMenuItemTaiKhoan(ActionEvent actionEvent) {
        try {
            StageInitializer.setScene(StageInitializer.taiKhoan);
        } catch (IOException e) {
            FXAlerts.error("Trang này đang không hoạt động");
        }
    }

    private void setMenuItemGuiRut(ActionEvent actionEvent) {
        try {
            StageInitializer.setScene(StageInitializer.guiRut);
        } catch (IOException e) {
            FXAlerts.error("Trang này đang không hoạt động");
        }
    }

    private void setMenuItemNhanVien(ActionEvent actionEvent) {
        try {
            StageInitializer.setScene(StageInitializer.nhanVien);
        } catch (IOException e) {
            FXAlerts.error("Trang này đang không hoạt động");
        }
    }

    private void setMenuItemReportGD(ActionEvent actionEvent) {
        try {
            StageInitializer.setScene(StageInitializer.reportGD);
        } catch (IOException e) {
            e.printStackTrace();
            FXAlerts.error("Trang này đang không hoạt động");
        }
    }

    private void setMenuItemReportKH(ActionEvent actionEvent) {
        try {
            StageInitializer.setScene(StageInitializer.reportKH);
        } catch (IOException e) {
            FXAlerts.error("Trang này đang không hoạt động");
        }
    }

    private void setMenuItemReportTK(ActionEvent actionEvent) {
        try {
            StageInitializer.setScene(StageInitializer.reportTK);
        } catch (IOException e) {
            FXAlerts.error("Trang này đang không hoạt động");
        }
    }

    private void setMenuItemKhachHang(ActionEvent actionEvent) {
        try {
            StageInitializer.setScene(StageInitializer.khachHang);
        } catch (IOException e) {
            FXAlerts.error("Trang này đang không hoạt động");
        }
    }
    private void setMenuItemDangXuat(ActionEvent actionEvent) {
        try {

            StageInitializer.setScene(StageInitializer.login);
        } catch (IOException e) {
            FXAlerts.error("Trang này đang không hoạt động");
        }
    }

}
