package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entitygeneral.ReportTK;
import com.thuan.carambola.repositorygeneral.ReportTKRepository;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class ReportTKController extends ReportController implements Initializable {
    ReportTKRepository reportTKRepository;

    @FXML
    private Button btnInChiNhanhCucBo;

    @FXML
    private Button btnInToanBoCacChiNhanh;

    @Autowired
    public ReportTKController(PhanManhRepository phanManhRepository,
                              ReportTKRepository reportTKRepository
                              ) {
        super(phanManhRepository);
        this.reportTKRepository = reportTKRepository;
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

        LocalDate start =  ngayBatDau.getValue();
        LocalDate end =  ngayKetThuc.getValue();
        if(start.isAfter(end))
        {
            FXAlerts.warning("Ngày bắt đầu không thể ở sau ngày kết thúc");
            return;
        }
        new Thread(() -> {
            try {
                JasperReport jasperReport = JasperCompileManager.compileReport(StageInitializer.zReportTKCN.getInputStream());
                List<ReportTK> list = reportTKRepository.get(start, end.plusDays(1));
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("createdBy", JavaFXApplication.tenNV);
                parameters.put("createdDate", LocalDateTime.now());
                parameters.put("from", start);
                parameters.put("to", end);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
                JasperViewer viewer = new JasperViewer(jasperPrint, false);
                viewer.setVisible(true);
            } catch (JRException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void btnInChiNhanhCucBo(ActionEvent actionEvent){
        LocalDate start =  ngayBatDau.getValue();
        LocalDate end =  ngayKetThuc.getValue();
        if(start.isAfter(end))
        {
            FXAlerts.warning("Ngày bắt đầu không thể ở sau ngày kết thúc");
            return;
        }

        new Thread(() -> {
            Platform.runLater(() -> {
                try {
                    List<ReportTK> list;
                    if (cbChiNhanh.getSelectionModel().isEmpty()) {
                        FXAlerts.warning("Vui lòng chọn chi nhánh");
                        return;
                    }
                    String server = cbChiNhanh.getSelectionModel().getSelectedItem().getTenserver();
                    if(server.equals(JavaFXApplication.server))
                        list = reportTKRepository.getOnsite(start, end);
                    else
                    {
                        list = reportTKRepository.getRemote(start, end);
                    }
                    JasperReport jasperReport = JasperCompileManager.compileReport(StageInitializer.zReportTKCN.getInputStream());

                    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("createdBy", JavaFXApplication.tenNV);
                    parameters.put("createdDate", LocalDateTime.now());
                    parameters.put("from", start);
                    parameters.put("to", end);
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