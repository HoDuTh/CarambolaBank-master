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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Instant;
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

    ObservableList<TaiKhoan> obListTK;
    ObservableList<KhachHang> obListKH;

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
        tfCMDN.setText("");
//        tfSoDu.setText("");
        tfSoTK.setText("");
        dpNgay.getEditor().clear();
        dpNgay.setValue(null);
    }

    @Override
    void btnXoa(ActionEvent actionEvent) {
//       TaiKhoan taiKhoan = tbTaiKhoan.getSelectionModel().getSelectedItem();
        Map<String, String> result = taiKhoanRepository.delete("0324234243");
        FXAlerts.info(result.get("MSG") + result.get("ISERROR"));
    }

    @Override
    void btnSua(ActionEvent actionEvent) {

    }

    @Override
    void btnGhi(ActionEvent actionEvent) {
        addTK();
    }

    @Override
    void btnHoanTac(ActionEvent actionEvent) {

    }
    void addTK()
    {
        Map<String, String> s = taiKhoanRepository.add("32243", "", "9/19/2021");
        updateData();
        FXAlerts.info(s.get("MSG"));
    }
    @Override
    public void filtered() //sử dụng filer để tìm kiếm
    {
        KhachHangController.filteredKhachHang( obListKH, tfSearch, tbKhachHang); // tìm kiếm khách hàng sử dụng textbox tìm kiếm
        filteredTaiKhoanDetail(obListTK, tbKhachHang, tbTaiKhoan); // tìm kiếm tài khoản của khách hàng sử dụng tableselection
        unFilteredTaiKhoanDetail();
    }
    @Override
    void initValidation()
    {

    }
    @Override
    void initTableEvent()
    {
        initTableDoubleCLickOnRow();
    }
    private void initTableDoubleCLickOnRow()
    {
        tbTaiKhoan.setRowFactory(tv -> {
            TableRow<TaiKhoan> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    TaiKhoan taiKhoan = row.getItem();
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
