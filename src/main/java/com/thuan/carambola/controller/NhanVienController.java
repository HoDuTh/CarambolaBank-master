package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.Service.DateTimeService;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entitygeneral.GDChuyenTien;
import com.thuan.carambola.entitygeneral.KhachHang;
import com.thuan.carambola.entitygeneral.NhanVien;
import com.thuan.carambola.entityprimary.VDsPhanmanhEntity;
import com.thuan.carambola.recovery.Handle;
import com.thuan.carambola.repositorygeneral.NhanVienRepository;
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

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.*;

@Component
public class NhanVienController extends BaseController implements Initializable {
    @FXML private ComboBox<VDsPhanmanhEntity> cbChiNhanh;
    @FXML private AnchorPane pInput;

    @FXML private TableView<NhanVien> tbNhanVien;
    @FXML private TableColumn<NhanVien, String> tc1ChiNhanh;
    @FXML private TableColumn<NhanVien, String> tc1DiaChi;
    @FXML private TableColumn<NhanVien, String> tc1Ho;
    @FXML private TableColumn<NhanVien, String> tc1MaNV;
    @FXML private TableColumn<NhanVien, String> tc1Phai;
    @FXML private TableColumn<NhanVien, String> tc1SoDT;
    @FXML private TableColumn<NhanVien, String> tc1Ten;
    @FXML private TableColumn<NhanVien, String> tc1TrangThaiXoa;

    @FXML private TextField tfDiaChi;
    @FXML private TextField tfHo;
    @FXML private TextField tfMANV;
    @FXML private TextField tfSearch;
    @FXML private TextField tfSoDienThoai;
    @FXML private TextField tfTen;
    @FXML private ToggleGroup tgGioiTinh;



    NhanVienRepository nhanVienRepository;
    PhanManhRepository phanManhRepository;

    ObservableList<NhanVien> obList;
    @Autowired
    public NhanVienController (NhanVienRepository nhanVienRepository,
                               PhanManhRepository phanManhRepository)
    {
        super(phanManhRepository);
        this.nhanVienRepository = nhanVienRepository;
        this.phanManhRepository = phanManhRepository;
        stack = new Stack<Handle<NhanVien>>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }
    @Override
    void initTableEvent()
    {
        initTableDoubleCLickOnRow();
    }
    private void initTableDoubleCLickOnRow()
    {
        tbNhanVien.setRowFactory(tv -> {
            TableRow<NhanVien> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    NhanVien nhanVien = row.getItem();
                    if(nhanVien.getId().equals(tfMANV.getText())) return;
                    boolean check;
                    if(!checkEmtyInput())
                    {
                        check = FXAlerts.confirm(String.format("Chỉnh sửa nhân viên %s", nhanVien.getHoTen()));
                        if(!check) return;
                    }
                    insertDataToInputBox(nhanVien);
                }
            });
            return row ;
        });
    }
    private void insertDataToInputBox(NhanVien nhanVien)
    {
        tfDiaChi.setText(nhanVien.getDiaChi());
        tfHo.setText(nhanVien.getHo());
        tfMANV.setText(nhanVien.getId());
        tfSoDienThoai.setText(nhanVien.getSoDT());
        tfTen.setText(nhanVien.getTen());
    }
    private boolean checkEmtyInput()
    {
        return tfDiaChi.getText().isBlank()
                || tfHo.getText().isBlank()
                || tfMANV.getText().isBlank()
                || tfHo.getText().isBlank()
                || tfSoDienThoai.getText().isBlank()
                || tfTen.getText().isBlank();
    }
    @Override
    void updateData()
    {
        List<NhanVien> list = nhanVienRepository.findAll();
        obList = FXCollections.observableArrayList(list);
        initTableView();
    }
    @Override
    void btnThem(ActionEvent actionEvent) {
        tfMANV.setText("");
        tfDiaChi.setText("");
        tfHo.setText("");
        tfSoDienThoai.setText("");
        tfTen.setText("");
    }

    @Override
    void btnXoa(ActionEvent actionEvent) {

    }

    @Override
    void btnSua(ActionEvent actionEvent) {

    }

    @Override
    void btnGhi(ActionEvent actionEvent) {

    }

    @Override
    void btnHoanTac(ActionEvent actionEvent) {

    }
    @Override
    void filtered()
    {
        filteredNhanVien();
    }
    @Override
    void unFiltered() {
//        tbNhanVien.getSelectionModel().clearSelection();
        tbNhanVien.setItems(obList);
    }
    @Override
    void initValidation()
    {

    }
    @Override
    void initTableView()
    {
        list2Table(tc1ChiNhanh, tc1DiaChi, tc1Ho, tc1MaNV,  tc1Phai, tc1SoDT, tc1Ten, tc1TrangThaiXoa);
        tbNhanVien.setItems(obList);
        filtered();
    }

    void addNV(ActionEvent actionEvent)
    {
        Map<String, String> s = nhanVienRepository.add("32243", "Hoàng", "Đức Thuận", "2324", "Nam", "73242020", "0");
        updateData();
        FXAlerts.info(s.get("MSG"));
    }

    @Override
    void initCBChiNhanhEvent()
    {
        cbChiNhanh.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            new Thread(() -> { // Lambda Expression
                pnCN.setDisable(true);
                List<NhanVien> lst;
                try{
                    if(!Objects.equals(newValue.getTencn(), JavaFXApplication.phanManh))
                    {
                        lst = nhanVienRepository.findRemoteAll();
                        obList = FXCollections.observableArrayList(lst);
                    }
                    else
                    {
                        lst = nhanVienRepository.findAll();
                        obList = FXCollections.observableArrayList(lst);
                    }
                    tbNhanVien.setItems(obList);
                }
                catch(Exception e)
                {
                    FXAlerts.error("Có lỗi bất thường");
                }

                pnCN.setDisable(false);
            }).start();
        });
    }

    private void filteredNhanVien() {
        FilteredList<NhanVien> fd = new FilteredList<>(obList, b -> true);
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
                        else if (model.getDiaChi().toLowerCase().contains(searchKeyword))
                            return true;
                        else if (model.getSoDT().toLowerCase().contains(searchKeyword))
                            return true;
                        else if (model.getMaCN().toLowerCase().contains(searchKeyword))
                            return true;
                        else return model.getPhai().toLowerCase().contains(searchKeyword);
                    }
            );
        });
        SortedList<NhanVien> sortedData = new SortedList<>(fd);
        sortedData.comparatorProperty().bind(tbNhanVien.comparatorProperty());
        tbNhanVien.setItems(sortedData);
    }


    static void list2Table(TableColumn<NhanVien, String> tc1ChiNhanh,
                           TableColumn<NhanVien, String> tc1DiaChi,
                           TableColumn<NhanVien, String> tc1Ho,
                           TableColumn<NhanVien, String> tc1MaNV,
                           TableColumn<NhanVien, String> tc1Phai,
                           TableColumn<NhanVien, String> tc1SoDT,
                           TableColumn<NhanVien, String> tc1Ten,
                           TableColumn<NhanVien, String> tc1TrangThaiXoa) {
        tc1ChiNhanh.setCellValueFactory(tf -> new SimpleStringProperty(tf.getValue().getMaCN()));
        tc1DiaChi.setCellValueFactory(tf -> tf.getValue().getDiaChiProperty());
        tc1Ho.setCellValueFactory(tf -> tf.getValue().getHoProperty());
        tc1MaNV.setCellValueFactory(tf -> tf.getValue().getMaNVProperty());
        tc1Phai.setCellValueFactory(tf -> tf.getValue().getPhaiProperty());
        tc1SoDT.setCellValueFactory(tf -> tf.getValue().getSoDTProperty());
        tc1Ten.setCellValueFactory(tf -> tf.getValue().getTenProperty());
        tc1TrangThaiXoa.setCellValueFactory(tf ->  new SimpleStringProperty(tf.getValue().getTrangThaiXoa()));
    }

}
