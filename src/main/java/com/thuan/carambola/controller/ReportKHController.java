package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entitygeneral.KhachHang;
import com.thuan.carambola.entitygeneral.ReportKH;
import com.thuan.carambola.repositorygeneral.KhachHangReportRepository;
import com.thuan.carambola.repositorygeneral.KhachHangRepository;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class ReportKHController extends ReportController {

    KhachHangRepository khachHangRepository;
    KhachHangReportRepository khachHangReportRepository;

    @FXML
    private Button btnInChiNhanhCucBo;

    @FXML
    private Button btnInToanBoCacChiNhanh;


    @Autowired
    public ReportKHController(PhanManhRepository phanManhRepository,
                              KhachHangRepository khachHangRepository,
                              KhachHangReportRepository khachHangReportRepository) {
        super(phanManhRepository);
        this.khachHangRepository = khachHangRepository;
        this.khachHangReportRepository = khachHangReportRepository;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        btnInChiNhanhCucBo.setOnAction(this::btnInChiNhanhCucBo);
        btnInToanBoCacChiNhanh.setOnAction(this::btnInToanBoCacChiNhanh);
        phanQuyen();
    }
    void phanQuyen()
    {
        super.phanQuyen();
        String permissions = JavaFXApplication.nhom;
        if ("NganHang".equals(permissions)) {
            btnInToanBoCacChiNhanh.setDisable(false);
        }
    }
    private void btnInToanBoCacChiNhanh(ActionEvent actionEvent){
        new Thread(() -> {
            try {
                JasperReport jasperReport = JasperCompileManager.compileReport(StageInitializer.zReportKH.getInputStream());
                List<ReportKH> list = khachHangReportRepository.report();
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("createdBy", JavaFXApplication.tenNV);
                parameters.put("createdDate", LocalDateTime.now());
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
                JasperViewer viewer = new JasperViewer(jasperPrint, false);
                viewer.setVisible(true);
            } catch (JRException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void btnInChiNhanhCucBo(ActionEvent actionEvent){
        new Thread(() -> {
            Platform.runLater(()->{
                try {
                    if (cbChiNhanh.getSelectionModel().isEmpty()) {
                        FXAlerts.warning("Vui lòng chọn chi nhánh");
                        return;
                    }
                    String server = cbChiNhanh.getSelectionModel().getSelectedItem().getTenserver();
                    List<ReportKH> list;
                    if(server.equals(JavaFXApplication.server))
                        list= khachHangReportRepository.reportOnsite();
                    else
                    {
                        list= khachHangReportRepository.reportRemote();
                    }
                    if(list.isEmpty())
                    {
                        FXAlerts.warning("Không tìm thấy dữ liệu báo cáo");
                        return;
                    }
                    JasperReport jasperReport = JasperCompileManager.compileReport(StageInitializer.zReportKH.getInputStream());
                    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("createdBy", JavaFXApplication.tenNV);
                    parameters.put("createdDate", LocalDateTime.now());
                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
                    JasperViewer viewer = new JasperViewer(jasperPrint, false);
                    viewer.setVisible(true);
                } catch (JRException | IOException e) {
                    e.printStackTrace();
                }
            });

        }).start();
    }
}