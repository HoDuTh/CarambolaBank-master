package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entitygeneral.KhachHang;
import com.thuan.carambola.entitygeneral.ReportGD;
import com.thuan.carambola.entitygeneral.ReportKH;
import com.thuan.carambola.entitygeneral.TaiKhoan;
import com.thuan.carambola.model.GDTaiKhoan;
import com.thuan.carambola.repositorygeneral.KhachHangRepository;
import com.thuan.carambola.repositorygeneral.ReportGDRepository;
import com.thuan.carambola.repositorygeneral.TaiKhoanRepository;
import com.thuan.carambola.repositorygeneral.ThongTinDangNhapRepository;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class ReportGDController extends ReportController {
    TaiKhoanRepository taiKhoanRepository;
    ReportGDRepository reportGDRepository;
    KhachHangRepository khachHangRepository;
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
    String maCN;
    @Autowired
    public ReportGDController(PhanManhRepository phanManhRepository,
                              TaiKhoanRepository taiKhoanRepository,
                              ReportGDRepository reportGDRepository,
                              KhachHangRepository khachHangRepository) {
        super(phanManhRepository);
        this.taiKhoanRepository = taiKhoanRepository;
        this.reportGDRepository = reportGDRepository;
        this.khachHangRepository = khachHangRepository;
    }

    void updateData() {
        List<TaiKhoan> listTK = taiKhoanRepository.findAll();
        List<KhachHang> listKH = khachHangRepository.findAll();
        if(listKH !=  null)
            maCN = listKH.get(0).getMaCN();
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
        btnInKQ.setOnAction(this::btnInChiNhanhCucBo);
        ChuyenTienController.filteredTaiKhoan(obListTK, tfSoTK, tbTaiKhoan);
        FilteredList<TaiKhoan> fd = new FilteredList<>(obListTK, b -> true);
        if(Objects.equals(cbChiNhanh.getSelectionModel().getSelectedItem().getTenserver(), JavaFXApplication.server))
        {
            fd.setPredicate(model -> model.getMaCN().equals(maCN));
        }
        else
        {
            fd.setPredicate(model -> !model.getMaCN().equals(maCN));
        }
        SortedList<TaiKhoan> sortedData = new SortedList<>(fd);
        sortedData.comparatorProperty().bind(tbTaiKhoan.comparatorProperty());
        tbTaiKhoan.setItems(sortedData);
        cbChiNhanh.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            FilteredList<TaiKhoan> A = new FilteredList<>(obListTK, b -> true);
            if(Objects.equals(newValue.getTenserver(), JavaFXApplication.server))
            {
                fd.setPredicate(model -> model.getMaCN().equals(maCN));
            }
            else
            {
                fd.setPredicate(model -> !model.getMaCN().equals(maCN));
            }
            SortedList<TaiKhoan> b = new SortedList<>(A);
            b.comparatorProperty().bind(tbTaiKhoan.comparatorProperty());
            tbTaiKhoan.setItems(b);
        });
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
    private List<GDTaiKhoan> getReport(List<ReportGD> list, LocalDate from)
    {
        List<GDTaiKhoan> listGD = new ArrayList<>();
        BigInteger soDu = BigInteger.ZERO;
        BigInteger soTien;
        for(ReportGD a : list)
        {
            GDTaiKhoan b = new GDTaiKhoan(a);
            b.setSoDuDau(soDu);
            soTien = a.soTien;
            switch (a.loaiGD) {
                case "CT", "RT" -> soDu = soDu.subtract(soTien);
                case "NT", "GT" -> soDu = soDu.add(soTien);
            }
            b.setSoDuSau(soDu);
            if(b.getNgayGD().isAfter(from.atStartOfDay()))
                listGD.add(b);
        }
        return listGD;
    }
    private void btnInChiNhanhCucBo(ActionEvent actionEvent){
        TaiKhoan tk = tbTaiKhoan.getSelectionModel().getSelectedItem();
        if(tk == null)
        {
            FXAlerts.warning("Vui lòng chọn tài khoản");
            return;
        }
        String soTK = tk.getId();
        LocalDate start =  ngayBatDau.getValue();
        LocalDate end =  ngayKetThuc.getValue();
        System.out.println(maCN);
        new Thread(() -> {
            Platform.runLater(()->{
                try {
                    if (cbChiNhanh.getSelectionModel().isEmpty()) {
                        FXAlerts.warning("Vui lòng chọn chi nhánh");
                        return;
                    }
                    String server = cbChiNhanh.getSelectionModel().getSelectedItem().getTenserver();
                    List<ReportGD> list;
                    System.out.println(server);
                    if(server.equals(JavaFXApplication.server))
                        list= reportGDRepository.get(soTK, end.plusDays(1));
                    else
                    {
                        list= reportGDRepository.getRemote(soTK, end.plusDays(1));
                    }
                    if(list.isEmpty())
                    {
                        FXAlerts.warning("Không tìm thấy dữ liệu báo cáo");
                        return;
                    }
                    for(ReportGD a : list)
                    {
                        System.out.println(a);
                    }
                    List<GDTaiKhoan> listGD = getReport(list, start);
                    System.out.println(listGD);
                    JasperReport jasperReport = JasperCompileManager.compileReport(StageInitializer.zReportGD.getInputStream());
                    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listGD);
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("createDate", LocalDateTime.now());
                    parameters.put("createdBy", JavaFXApplication.tenNV);
                    parameters.put("soTK", soTK);
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
