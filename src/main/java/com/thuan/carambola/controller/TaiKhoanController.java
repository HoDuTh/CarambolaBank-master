package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.Service.DateTimeService;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entitygeneral.GDChuyenTien;
import com.thuan.carambola.entitygeneral.KhachHang;
import com.thuan.carambola.entitygeneral.NhanVien;
import com.thuan.carambola.entitygeneral.TaiKhoan;
import com.thuan.carambola.entityprimary.VDsPhanmanhEntity;
import com.thuan.carambola.recovery.Handle;
import com.thuan.carambola.repositorygeneral.KhachHangRepository;
import com.thuan.carambola.repositorygeneral.TaiKhoanRepository;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Component
public class TaiKhoanController extends BaseController implements Initializable {
    @FXML private ComboBox<VDsPhanmanhEntity> cbChiNhanh;

    @FXML private TableView<KhachHang> tbKhachHang;
    @FXML private TableColumn<KhachHang, String> tc1CMND;
    @FXML private TableColumn<KhachHang, String> tc1DiaChi;
    @FXML private TableColumn<KhachHang, String> tc1Ho;
    @FXML private TableColumn<KhachHang, String> tc1NgayCap;
    @FXML private TableColumn<KhachHang, String> tc1Phai;
    @FXML private TableColumn<KhachHang, String> tc1SoDienThoai;
    @FXML private TableColumn<KhachHang, String> tc1Ten;
    @FXML private TableColumn<KhachHang, String> tc1ChiNhanh;

    @FXML private TableView<TaiKhoan> tbTaiKhoan;
    @FXML private TableColumn<TaiKhoan, String> tc2CMNDTK;
    @FXML private TableColumn<TaiKhoan, String> tc2ChiNhanhTK;
    @FXML private TableColumn<TaiKhoan, String> tc2NgayMoTK;
    @FXML private TableColumn<TaiKhoan, String> tc2SoDuTK;
    @FXML private TableColumn<TaiKhoan, String> tc2SoTK;

    @FXML private FlowPane pnSSS;
    @FXML private TextField tfCMDN;
    @FXML private TextField tfSearch;
    @FXML private TextField tfSoDu;
    @FXML private TextField tfSoTK;

    TaiKhoanRepository taiKhoanRepository;
    KhachHangRepository khachHangRepository;
    PhanManhRepository phanManhRepository;
    Stack<Handle<TaiKhoan>> stack;
    ObservableList<TaiKhoan> obListTK;
    ObservableList<KhachHang> obListKH;
    Logger log = LoggerFactory.getLogger(TaiKhoanController.class);
    @Autowired
    public TaiKhoanController( TaiKhoanRepository taiKhoanRepository,
                               KhachHangRepository khachHangRepository,
                             PhanManhRepository phanManhRepository) {
        super(phanManhRepository);
        this.taiKhoanRepository = taiKhoanRepository;
        this.khachHangRepository = khachHangRepository;
        this.phanManhRepository = phanManhRepository;
        stack = new Stack<Handle<TaiKhoan>>();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTimePicker();
        super.initialize(location, resources);
    }
    @Scheduled(fixedRate = reloadTimer)
    public void scheduleTaskWithFixedRate() {
        Platform.runLater(() -> {
            if(StageInitializer.currentResource == StageInitializer.taiKhoan) {
                updateData();
            }
        });
    }
    @Override
    void initTableView() {
        list2TableTK(tc2SoTK, tc2CMNDTK, tc2SoDuTK, tc2ChiNhanhTK, tc2NgayMoTK);
        KhachHangController.list2Table(tc1ChiNhanh, tc1DiaChi, tc1Ho, tc1NgayCap,  tc1Phai, tc1SoDienThoai, tc1Ten, tc1CMND);
        tbTaiKhoan.setItems(obListTK);
        tbKhachHang.setItems(obListKH);
        filtered();
    }

    @Override
    void updateData()
    {
        List<KhachHang> listKH = khachHangRepository.findAll();
        List<TaiKhoan> listTK = taiKhoanRepository.findAll();
        obListKH = FXCollections.observableArrayList(listKH);
        obListTK = FXCollections.observableArrayList(listTK);
        initTableView();
    }
    @Override
    void btnThem(ActionEvent actionEvent) {
        boolean check = FXAlerts.confirm("Bạn có chắc chắn muốn tạo phiên làm việc mới");
        if(!check) return;
        tfCMDN.setText("");
        tfSoTK.setText("");
        dpNgay.getEditor().clear();
        dpNgay.setValue(null);
    }

    @Override
    void btnXoa(ActionEvent actionEvent) {
        TaiKhoan taiKhoan = tbTaiKhoan.getSelectionModel().getSelectedItem();
        if(taiKhoan == null)
        {
            FXAlerts.warning("Chưa chọn tài khoản để xóa");
            return;
        }
        boolean check = FXAlerts.confirm(String.format("Bạn có thực sự muốn xóa tài khoản %s", taiKhoan.getId()));
        if(!check) return;
        new Thread(()->{
            load();
            Map<String, String> result = taiKhoanRepository.delete(taiKhoan.getId());
            String msg = result.get("MSG");
            Platform.runLater(() -> {
                if(result.get("ISSUCCESS").equals("1")) {
                    Handle<TaiKhoan> handler = new Handle<>();
                    handler.setEntity(taiKhoan);
                    handler.setAction("xoa");
                    stack.add(handler);
                    FXAlerts.info(msg);
                }
                else {
                    FXAlerts.error(msg);
                }
            });
            updateData();
            unload();
        }).start();

    }

    @Override
    void btnSua(ActionEvent actionEvent) {
        TaiKhoan taiKhoan = tbTaiKhoan.getSelectionModel().getSelectedItem();
        if(taiKhoan == null)
        {
            FXAlerts.warning("Chưa chọn tài khoản để sửa");
            return;
        }
        String cmnd = tfCMDN.getText();
        String soTK = taiKhoan.getId();
        Instant ngay = getDateTime();
        if(cmnd.isBlank())
        {
            FXAlerts.warning("Chứng minh nhân dân không được để trống");
            return;
        }
        boolean check = FXAlerts.confirm(String.format("""
                Bạn có muốn thay đổi thông tin:
                Số tài khoản: %s\s
                CMND:  %s \t\t\t-> %s
                Ngày tạo:  %s \t-> %s""", soTK, taiKhoan.getCmnd(), cmnd, taiKhoan.getNgayMoTK(), ngay.toString()));
        if(!check) return;
        new Thread(()->{
            load();
            Map<String, String> result = taiKhoanRepository.edit(cmnd, soTK, ngay);
            String msg = result.get("MSG");
            Platform.runLater(() -> {
                if(result.get("ISSUCCESS").equals("1")) {
                    Handle<TaiKhoan> handler = new Handle<>();
                    handler.setEntity(taiKhoan);
                    handler.setAction("sua");
                    stack.add(handler);
                    FXAlerts.info(msg);
                }
                else {
                    FXAlerts.error(msg);
                }
                updateData();
            });
            unload();
        }).start();

    }

    @Override
    void btnGhi(ActionEvent actionEvent) {
        String cmnd = tfCMDN.getText();
        String soTK = tfSoTK.getText();
        Instant ngay = getDateTime();
        if(cmnd.isBlank())
        {
            FXAlerts.warning("Chứng minh nhân dân không được để trống");
            return;
        }
        if(soTK.isBlank())
        {
            FXAlerts.warning("Số tài khoản không được để trống");
            return;
        }
        boolean check = FXAlerts.confirm(String.format("""
                Bạn có thực sự muốn tạo tài khoản %s
                CMND: %s\s
                Ngày tạo: %s""", soTK, cmnd, ngay.toString()));
        if(!check) return;
        new Thread(()->{
            load();
            Map<String, String> result = taiKhoanRepository.add(cmnd, soTK, ngay);
            String msg = result.get("MSG");
            Platform.runLater(() -> {
                if(result.get("ISSUCCESS").equals("1")) {
                    TaiKhoan tk = new TaiKhoan(soTK, cmnd, ngay);
                    Handle<TaiKhoan> handler = new Handle<>();
                    handler.setEntity(tk);
                    handler.setAction("ghi");
                    stack.add(handler);
                    FXAlerts.info(msg);
                }
                else {
                    FXAlerts.error(msg);
                }
            });
            updateData();
            unload();
        }).start();
    }

    @Override
    void btnHoanTac(ActionEvent actionEvent) {
        if(stack.isEmpty()) {
            FXAlerts.info("Không còn tác vụ để hòan tác");
            return;
        }
        Handle<TaiKhoan> handler = stack.pop();
        TaiKhoan taiKhoan = handler.getEntity();
        boolean check;
        try{
            switch (handler.getAction()) {
                case "ghi" -> {
                    check = FXAlerts.confirm(String.format("""
                            Bạn có thực sự muốn xóa tài khoản %s
                            CMND:       %s\s
                            Ngày tạo:   %s""",
                            taiKhoan.getId(), taiKhoan.getCmnd(), taiKhoan.getNgayMoTK().toString()));
                    if (!check) {
                        stack.push(handler);
                        return;
                    };

                    new Thread(() -> {



                        load();
                        Map<String, String> result = taiKhoanRepository.delete(taiKhoan.getId());
                        showResult(result.get("ISSUCCESS"), result.get("MSG"));
                        updateData();
                        unload();
                    }).start();
                }
                case "sua" -> {
                    check = FXAlerts.confirm(String.format("""
                            Bạn có muốn hoàn tác thay đổi thông tin:
                            Số tài khoản:   %s\s
                            CMND:           %s\040
                            Ngày tạo:       %s""",
                            taiKhoan.getId(),
                            taiKhoan.getCmnd(),
                            taiKhoan.getNgayMoTK().toString()));
                    if (!check) {
                        stack.push(handler);
                        return;
                    };
                    new Thread(() -> {
                        load();
                        Map<String, String> result =
                                taiKhoanRepository.edit(
                                        taiKhoan.getId(),
                                        taiKhoan.getCmnd(),
                                        taiKhoan.getNgayMoTK());
                        showResult(result.get("ISSUCCESS"), result.get("MSG"));
                        updateData();
                        unload();
                    }).start();
                } case "xoa" -> {
                    check = FXAlerts.confirm(String.format("""
                            Bạn có thực sự muốn khôi phục tài khoản %s
                            CMND:       %s\s
                            Ngày tạo:   %s""",
                            taiKhoan.getId(),
                            taiKhoan.getCmnd(),
                            taiKhoan.getNgayMoTK().toString()));
                    if (!check) {
                        stack.push(handler);
                        return;
                    };
                    new Thread(() -> {
                        load();
                        Map<String, String> result =
                                taiKhoanRepository.add(
                                        taiKhoan.getId(),
                                        taiKhoan.getCmnd(),
                                        taiKhoan.getNgayMoTK());
                        showResult(result.get("ISSUCCESS"), result.get("MSG"));
                        updateData();
                        unload();
                    }).start();
                } default -> FXAlerts.info("Tác vụ đã lưu không phù hợp");
            }
        }
        catch(Exception e)
        {
            unload();
        }
    }

    @Override
    public void filtered() //sử dụng filer để tìm kiếm
    {
        KhachHangController.filteredKhachHang( obListKH, tfSearch, tbKhachHang); // tìm kiếm khách hàng sử dụng textbox tìm kiếm
        filteredTaiKhoanDetail(obListTK, tbKhachHang, tbTaiKhoan); // tìm kiếm tài khoản của khách hàng sử dụng tableselection
        unFilteredTaiKhoanDetail();
        ChuyenTienController.filteredTaiKhoan(obListTK, tfSoTK, tbTaiKhoan);
    }
    @Override
    void initValidation()
    {
        valideSoTK(tfSoTK);
        valideCMND(tfCMDN);
    }
    @Override
    void initTableEvent()
    {
        initTableDoubleCLickOnRow();
    }
    private void initTableDoubleCLickOnRow()
    {
        tbKhachHang.setRowFactory(tv -> {
            TableRow<KhachHang> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    KhachHang khachHang = row.getItem();
                    if(khachHang == null) return;
                    boolean check;
                    String newCMND = khachHang.getId();
                    String tfCMNDValue = tfCMDN.getText();
                    if(khachHang.getId().equals(tfCMDN.getText())) return;
                    if(!tfCMNDValue.isBlank()){
                        if(!Objects.equals(newCMND, tfCMNDValue))
                        {
                            check = FXAlerts.confirm(String.format("Thay đổi số cmnd thành %s", newCMND));
                            if(!check) return;
                        }
                    }
                    tfCMDN.setText(newCMND);
                }
            });
            return row ;
        });
        tbTaiKhoan.setRowFactory(tv -> {
            TableRow<TaiKhoan> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    TaiKhoan taiKhoan = row.getItem();
                    if(taiKhoan == null) return;
                    boolean check;
                    if(taiKhoan.getId().equals(tfSoTK.getText())) return;
                    if(!checkEmtyInput())
                    {
                        check = FXAlerts.confirm(String.format("Chỉnh sửa tài khoản %s", taiKhoan.getId()));
                        if(!check) return;
                    }
                    insertDataToInputBox(taiKhoan);
                }
            });
            return row ;
        });
    }
    private void insertDataToInputBox(TaiKhoan taiKhoan)
    {
        tfSoTK.setText(taiKhoan.getId());
        tfCMDN.setText(taiKhoan.getCmnd());

        Instant ngayCap = taiKhoan.getNgayMoTK();
        dpNgay.setValue(DateTimeService.get(ngayCap));
        tfHour.setText(String.valueOf(DateTimeService.getHour(ngayCap)));
        tfMinute.setText(String.valueOf(DateTimeService.getMinute(ngayCap)));
    }
    private boolean checkEmtyInput() // kiểm tra người dùng có đang làm việc
    {
        return tfSoTK.getText().isBlank()
                || tfCMDN.getText().isBlank();
    }
    @Override
    void unFiltered() {
        tbTaiKhoan.setItems(obListTK);
    }
    static void filteredTaiKhoanDetail(ObservableList<TaiKhoan> obList,
                                       TableView<KhachHang> tbKhachHang,
                                       TableView<TaiKhoan> tbTaiKhoan) {
        FilteredList<TaiKhoan> fd = new FilteredList<>(obList, b -> true);
        tbKhachHang.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
                fd.setPredicate(model -> model.getCmnd().equals(newSelection.getId()));
        });
        SortedList<TaiKhoan> sortedData = new SortedList<>(fd);
        sortedData.comparatorProperty().bind(tbTaiKhoan.comparatorProperty());
        tbTaiKhoan.setItems(sortedData);
    }

    void unFilteredTaiKhoanDetail() {
        pnSSS.setOnMousePressed(event -> {
            tbKhachHang.getSelectionModel().clearSelection();
            tbTaiKhoan.setItems(obListTK);
            filteredTaiKhoanDetail(obListTK, tbKhachHang, tbTaiKhoan);
        });
    }

    @Override
    void initCBChiNhanhEvent()
    {
        cbChiNhanh.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            new Thread(() -> { // Lambda Expression
                pnCN.setDisable(true);
                List<KhachHang> lst;
                try{
                    if(!Objects.equals(newValue.getTencn(), JavaFXApplication.phanManh))
                    {
                        lst = khachHangRepository.findRemoteAll();
                        obListKH = FXCollections.observableArrayList(lst);
                    }
                    else
                    {
                        lst = khachHangRepository.findAll();
                        obListKH = FXCollections.observableArrayList(lst);
                    }
                    tbKhachHang.setItems(obListKH);
                }
                catch(Exception e)
                {
                    FXAlerts.error("Có lỗi bất thường");
                }
                pnCN.setDisable(false);
            }).start();
        });
    }
    static void list2TableTK(TableColumn<TaiKhoan, String> tcSoTK,
                             TableColumn<TaiKhoan, String> tcCMNDTK,
                             TableColumn<TaiKhoan, String> tcSoDuTK,
                             TableColumn<TaiKhoan, String> tcChiNhanhTK,
                             TableColumn<TaiKhoan, String> tcNgayMoTK,
                             TableColumn<TaiKhoan, String> tcHoTen,
                             TableColumn<TaiKhoan, String> tcSoDienThoai) {
        list2TableTK(tcSoTK, tcCMNDTK, tcSoDuTK, tcChiNhanhTK, tcNgayMoTK);
        tcHoTen.setCellValueFactory(tf -> new SimpleStringProperty(tf.getValue().getHoTen()));
        tcSoDienThoai.setCellValueFactory(tf -> new SimpleStringProperty(tf.getValue().getSoDT()));
    }
    static void list2TableTK(TableColumn<TaiKhoan, String> tcSoTK,
                             TableColumn<TaiKhoan, String> tcCMNDTK,
                             TableColumn<TaiKhoan, String> tcSoDuTK,
                             TableColumn<TaiKhoan, String> tcChiNhanhTK,
                             TableColumn<TaiKhoan, String> tcNgayMoTK) {
        tcSoTK.setCellValueFactory(tf -> tf.getValue().getSoTKProperty());
        tcCMNDTK.setCellValueFactory(tf -> new SimpleStringProperty(tf.getValue().getCmnd()));
        tcSoDuTK.setCellValueFactory(tf -> tf.getValue().getSoduProperty());
        tcChiNhanhTK.setCellValueFactory(tf -> new SimpleStringProperty(tf.getValue().getMaCN()));
        tcNgayMoTK.setCellValueFactory(tf -> tf.getValue().getNgayMoTKProperty());
    }
}
