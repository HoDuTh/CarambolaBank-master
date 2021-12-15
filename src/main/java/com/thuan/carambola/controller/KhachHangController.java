package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.Service.DateTimeService;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entitygeneral.GDChuyenTien;
import com.thuan.carambola.entitygeneral.GDGoiRut;
import com.thuan.carambola.entitygeneral.KhachHang;
import com.thuan.carambola.entityprimary.VDsPhanmanhEntity;
import com.thuan.carambola.recovery.Handle;
import com.thuan.carambola.repositorygeneral.KhachHangRepository;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Component
public class KhachHangController  extends BaseController implements Initializable {

    @FXML private AnchorPane pInput;

    @FXML private TextField tfCMND;
    @FXML private TextField tfDiaChi;
    @FXML private TextField tfHo;
    @FXML private TextField tfSearch;
    @FXML private TextField tfSoDienThoai;
    @FXML private TextField tfTen;
    @FXML private ToggleGroup tgGioiTinh;
    @FXML private RadioButton rbNam;
    @FXML private RadioButton rbNu;

    @FXML private TableView<KhachHang> tbKhachHang;
    @FXML private TableColumn<KhachHang, String> tc1ChiNhanh;
    @FXML private TableColumn<KhachHang, String> tc1DiaChi;
    @FXML private TableColumn<KhachHang, String> tc1Ho;
    @FXML private TableColumn<KhachHang, String> tc1NgayCap;
    @FXML private TableColumn<KhachHang, String> tc1Phai;
    @FXML private TableColumn<KhachHang, String> tc1SoDienThoai;
    @FXML private TableColumn<KhachHang, String> tc1Ten;
    @FXML private TableColumn<KhachHang, String> tc1CMND;

    KhachHangRepository khachHangRepository;
    ObservableList<KhachHang> obList;

    @Autowired
    public KhachHangController (KhachHangRepository khachHangRepository,
                                PhanManhRepository phanManhRepository)
    {
        super(phanManhRepository);
        this.khachHangRepository = khachHangRepository;
        this.phanManhRepository = phanManhRepository;
        stack = new Stack<Handle<KhachHang>>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTimePicker();
        super.initialize(location, resources);
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
                    boolean check;
                    if(khachHang.getId().equals(tfCMND.getText())) return;
                    if(!checkEmtyInput()){
                        check = FXAlerts.confirm(String.format("Chỉnh sửa khách hàng %s", khachHang.getHoTen()));
                        if(!check) return;
                    }
                    insertDataToInputBox(khachHang);
                }
            });
            return row ;
        });
    }
    private void insertDataToInputBox(KhachHang khachHang)
    {
        tfCMND.setText(khachHang.getId());
        tfDiaChi.setText(khachHang.getDiaChi());
        tfHo.setText(khachHang.getHo());
        tfSoDienThoai.setText(khachHang.getSoDT());
        tfTen.setText(khachHang.getTen());
        if (Objects.equals(khachHang.getPhai(), "Nam")) {
            rbNam.setSelected(true);
        } else {
            rbNu.setSelected(true);
        }
        Instant ngayCap = khachHang.getNgayCap();
        dpNgay.setValue(DateTimeService.get(ngayCap));
        tfHour.setText(String.valueOf(DateTimeService.getHour(ngayCap)));
        tfMinute.setText(String.valueOf(DateTimeService.getMinute(ngayCap)));
    }
    @Override
    void updateData()
    {
        List<KhachHang> list = khachHangRepository.findAll();
        obList = FXCollections.observableArrayList(list);
        initTableView();
    }

    @Override
    void btnThem(ActionEvent actionEvent) {
        tfCMND.setText("");
        tfDiaChi.setText("");
        tfHo.setText("");
        tfSoDienThoai.setText("");
        tfTen.setText("");
        tfHour.setText("");
        tfMinute.setText("");
        dpNgay.getEditor().clear();
        dpNgay.setValue(null);
    }
    private boolean checkEmtyInput() // kiểm tra người dùng có đang làm việc
    {
        return tfCMND.getText().isBlank()
                || tfCMND.getText().isBlank()
                || tfDiaChi.getText().isBlank()
                || tfHo.getText().isBlank()
                || tfSoDienThoai.getText().isBlank()
                || tfTen.getText().isBlank();
    }
    @Override
    void btnXoa(ActionEvent actionEvent) {

    }

    @Override
    void btnSua(ActionEvent actionEvent) {
        Map<String, String> s = khachHangRepository.add("322243", "Hoàng", "Đức Thuận", "2324", "Nam", "13/12/2020", "2344324");
        updateData();
        FXAlerts.info(s.get("MSG"));
    }

    @Override
    void btnGhi(ActionEvent actionEvent) {

    }

    @Override
    void btnHoanTac(ActionEvent actionEvent) {

    }
    @Override
    void filtered() {
        filteredKhachHang( obList, tfSearch, tbKhachHang);
    }
    @Override
    void unFiltered() {
        tbKhachHang.setItems(obList);
        tbKhachHang.getSelectionModel().clearSelection();
    }
    @Override
    void initValidation()
    {

    }
    @Override
    void initTableView()
    {
        list2Table(tc1ChiNhanh, tc1DiaChi, tc1Ho, tc1NgayCap,  tc1Phai, tc1SoDienThoai, tc1Ten, tc1CMND);
        tbKhachHang.setItems(obList);
        filtered();
    }

    void addKH(ActionEvent actionEvent)
    {
//        RadioButton selectedRadioButton = (RadioButton) tgGioiTinh.getSelectedToggle();
//        String toogleGroupValue = selectedRadioButton.getText();

    }
    static void list2Table(TableColumn<KhachHang, String> tc1ChiNhanh,
                           TableColumn<KhachHang, String> tc1DiaChi,
                           TableColumn<KhachHang, String> tc1Ho,
                           TableColumn<KhachHang, String> tc1NgayCap,
                           TableColumn<KhachHang, String> tc1Phai,
                           TableColumn<KhachHang, String> tc1SoDienThoai,
                           TableColumn<KhachHang, String> tc1Ten,
                           TableColumn<KhachHang, String> tc1CMND) {
        tc1ChiNhanh.setCellValueFactory(tf -> tf.getValue().getMaCNProperty());
        tc1DiaChi.setCellValueFactory(tf -> tf.getValue().getDiaChiProperty());
        tc1Ho.setCellValueFactory(tf -> tf.getValue().getHoProperty());
        tc1NgayCap.setCellValueFactory(tf -> tf.getValue().getNgayCapProperty());
        tc1Phai.setCellValueFactory(tf -> tf.getValue().getPhaiProperty());
        tc1SoDienThoai.setCellValueFactory(tf -> tf.getValue().getSoDTProperty());
        tc1Ten.setCellValueFactory(tf -> tf.getValue().getTenProperty());
        tc1CMND.setCellValueFactory(tf -> tf.getValue().getCMNDProperty());
    }
    static void filteredKhachHang(ObservableList<KhachHang> obList,
                                  TextField tfSearch,
                                  TableView<KhachHang> tbKhachHang) {
        FilteredList<KhachHang> fd = new FilteredList<>(obList, b -> true);
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            fd.setPredicate(model -> {
                        if (newValue.isEmpty() || newValue.isBlank())
                            return true;
                        String searchKeyword = newValue.toLowerCase();
                        if (model.getId().toLowerCase().contains(searchKeyword))
                            return true;
                        else if (model.getHo().toLowerCase().contains(searchKeyword))
                            return true;
                        else if (model.getTen().toLowerCase().contains(searchKeyword))
                            return true;
                        else if (model.getHoTen().toLowerCase().contains(searchKeyword))
                            return true;
                        else if (model.getDiaChi().toLowerCase().contains(searchKeyword))
                            return true;
                        else if (model.getNgayCap().toString().toLowerCase().contains(searchKeyword))
                            return true;
                        else if (model.getMaCN().toLowerCase().contains(searchKeyword))
                            return true;
                        else return model.getPhai().toLowerCase().contains(searchKeyword);
                    }
            );
        });
        SortedList<KhachHang> sortedData = new SortedList<>(fd);
        sortedData.comparatorProperty().bind(tbKhachHang.comparatorProperty());
        tbKhachHang.setItems(sortedData);
    }
    @Override
    void initCBChiNhanhEvent()
    {
        cbChiNhanh.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            new Thread(() -> { // Lambda Expression
                pnCN.setDisable(true);
                List<KhachHang> lst;
                System.out.println(newValue);
                System.out.println(JavaFXApplication.phanManh);
                try{
                    if(!Objects.equals(newValue.getTencn(), JavaFXApplication.phanManh))
                        {
                            lst = khachHangRepository.findRemoteAll();
                            obList = FXCollections.observableArrayList(lst);
                        }
                        else
                        {
                            lst = khachHangRepository.findAll();
                            obList = FXCollections.observableArrayList(lst);
                        }
                            tbKhachHang.setItems(obList);
                        }
                catch(Exception e)
                {
                    FXAlerts.error("Có lỗi bất thường");
                }
                pnCN.setDisable(false);
            }).start();
        });
    }
}
