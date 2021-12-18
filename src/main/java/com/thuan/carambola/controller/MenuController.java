package com.thuan.carambola.controller;

import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public abstract class MenuController implements Initializable {
    //-------------------Menu-------------------
    @FXML MenuBar menu;
    @FXML Menu menuGiaoDich;
    @FXML Menu menuGioiThieu;
    @FXML Menu menuHeThong;
    @FXML Menu menuHuongDan;
    @FXML Menu menuKhachHang;
    @FXML Menu menuNhanVien;
    @FXML Menu menuTaiKhoan;
    @FXML Menu menuBaoCaoThongKe;
    @FXML MenuItem menuItemChuyenNhanVien;
    @FXML MenuItem menuItemChuyenTien;
    @FXML MenuItem menuItemGiaoDich;
    @FXML MenuItem menuItemGuiRut;
    @FXML MenuItem menuItemHeThong;
    @FXML MenuItem menuItemKhachHang;
    @FXML MenuItem menuItemTaiKhoan;
    @FXML MenuItem menuItemNhanVien;
    @FXML MenuItem menuItemTaoTKDN;
    @FXML MenuItem menuItemDangXuat;
    @FXML MenuItem menuItemReportGD;
    @FXML MenuItem menuItemReportKH;
    @FXML MenuItem menuItemReportTK;
    //-------------------Menu-------------------
    void initMenu()
    {
        menuItemChuyenTien.setOnAction(this::setMenuItemChuyenTien);
        menuItemTaiKhoan.setOnAction(this::setMenuItemTaiKhoan);
        menuItemGuiRut.setOnAction(this::setMenuItemGuiRut);
        menuItemNhanVien.setOnAction(this::setMenuItemNhanVien);
        menuItemKhachHang.setOnAction(this::setMenuItemKhachHang);
        menuItemReportGD.setOnAction(this::setMenuItemReportGD);
        menuItemReportKH.setOnAction(this::setMenuItemReportKH);
        menuItemReportTK.setOnAction(this::setMenuItemReportTK);
    }
        private void setMenuItemChuyenTien(ActionEvent actionEvent)
        {try {
            StageInitializer.setScene(StageInitializer.chuyenTien);} catch (IOException e)
        {
            FXAlerts.error("Trang này đang không hoạt động");}}

        private void setMenuItemTaiKhoan(ActionEvent actionEvent)
        {try {StageInitializer.setScene(StageInitializer.taiKhoan);} catch (IOException e)
        {FXAlerts.error("Trang này đang không hoạt động");}}

        private void setMenuItemGuiRut(ActionEvent actionEvent)
        {try {StageInitializer.setScene(StageInitializer.guiRut);} catch (IOException e)
        {FXAlerts.error("Trang này đang không hoạt động");}}

        private void setMenuItemNhanVien(ActionEvent actionEvent)
        {try {StageInitializer.setScene(StageInitializer.nhanVien);} catch (IOException e)
        {FXAlerts.error("Trang này đang không hoạt động");}}

        private void setMenuItemReportGD(ActionEvent actionEvent)
        {try {StageInitializer.setScene(StageInitializer.reportGD);} catch (IOException e)
        {FXAlerts.error("Trang này đang không hoạt động");}}

        private void setMenuItemReportKH(ActionEvent actionEvent)
        {try {StageInitializer.setScene(StageInitializer.reportKH);} catch (IOException e)
        {FXAlerts.error("Trang này đang không hoạt động");}}

        private void setMenuItemReportTK(ActionEvent actionEvent)
        {try {StageInitializer.setScene(StageInitializer.reportTK);} catch (IOException e)
        {FXAlerts.error("Trang này đang không hoạt động");}}

        private void setMenuItemKhachHang(ActionEvent actionEvent)
        {try {StageInitializer.setScene(StageInitializer.khachHang);} catch (IOException e)
        {FXAlerts.error("Trang này đang không hoạt động");}}
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMenu();
    }
}
