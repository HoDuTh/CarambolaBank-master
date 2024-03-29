package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.Service.Validation;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entitygeneral.GDChuyenTien;
import com.thuan.carambola.entitygeneral.TaiKhoan;
import com.thuan.carambola.recovery.Handle;
import com.thuan.carambola.repositorygeneral.ChuyenTienRepository;
import com.thuan.carambola.repositorygeneral.TaiKhoanRepository;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import com.thuan.carambola.setting.ValidationValue;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class ChuyenTienController extends BaseController implements Initializable {

    @FXML
    public TableColumn<TaiKhoan, String> tcHoTen;
    TaiKhoanRepository taiKhoanRepository;
    ChuyenTienRepository chuyenTienRepository;
    PhanManhRepository phanManhRepository;
    ObservableList<TaiKhoan> obListTK;
    ObservableList<GDChuyenTien> obListGD;
    Stack<Handle<GDChuyenTien>> stack;
    Logger log = LoggerFactory.getLogger(ChuyenTienController.class);
    @FXML
    private AnchorPane pInput;
    //Table Tài khoản
    @FXML
    private TableView<TaiKhoan> tbTaiKhoan;
    @FXML
    private TableColumn<TaiKhoan, String> tcSoTK;
    @FXML
    private TableColumn<TaiKhoan, String> tcCMNDTK;
    @FXML
    private TableColumn<TaiKhoan, String> tcHo;
    @FXML
    private TableColumn<TaiKhoan, String> tcTen;
    //Table Chuyển tiền
    @FXML
    private TableColumn<TaiKhoan, String> tcChiNhanhTK;
    @FXML
    private TableColumn<TaiKhoan, String> tcNgayMoTK;
    @FXML
    private TableColumn<TaiKhoan, String> tcSoDienThoai;
    @FXML
    private TableColumn<TaiKhoan, String> tcSoDuTK;
    @FXML
    private TableView<GDChuyenTien> tbChuyenTien;
    @FXML
    private TableColumn<GDChuyenTien, String> tcMaGD;
    @FXML
    private TableColumn<GDChuyenTien, String> tcMaNV;
    @FXML
    private TableColumn<GDChuyenTien, String> tcNgayGD;
    @FXML
    private TableColumn<GDChuyenTien, String> tcSoTKGui;
    @FXML
    private TableColumn<GDChuyenTien, String> tcSoTKNhan;
    @FXML
    private TableColumn<GDChuyenTien, String> tcSoTien;
    @FXML
    private TableColumn<GDChuyenTien, String> tcHoTenNV;
    @FXML
    private Label lableSoTienFormated;
    @FXML
    private TextField tfSoTKChuyen;
    @FXML
    private TextField tfSoTien;
    @FXML
    private TextField tfSoTKNhan;
    @FXML
    private TextField tfSearch;

    @Autowired
    public ChuyenTienController(ChuyenTienRepository chuyenTienRepository,
                                PhanManhRepository phanManhRepository,
                                TaiKhoanRepository taiKhoanRepository) {
        super(phanManhRepository);
        this.chuyenTienRepository = chuyenTienRepository;
        this.phanManhRepository = phanManhRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        stack = new Stack<Handle<GDChuyenTien>>();
    }

    static void list2Table(TableColumn<GDChuyenTien, String> tcMaGD,
                           TableColumn<GDChuyenTien, String> tcMaNV,
                           TableColumn<GDChuyenTien, String> tcNgayGD,
                           TableColumn<GDChuyenTien, String> tcSoTKGui,
                           TableColumn<GDChuyenTien, String> tcSoTKNhan,
                           TableColumn<GDChuyenTien, String> tcSoTien,
                           TableColumn<GDChuyenTien, String> tcHoTenNV) {
        tcMaGD.setCellValueFactory(tf -> tf.getValue().getMaGDProperty());
        tcMaNV.setCellValueFactory(tf -> new SimpleStringProperty(tf.getValue().getMaNV()));
        tcNgayGD.setCellValueFactory(tf -> tf.getValue().getNgayGDProperty());
        tcSoTKGui.setCellValueFactory(tf -> new SimpleStringProperty(tf.getValue().getSoTKChuyen()));
        tcSoTKNhan.setCellValueFactory(tf -> new SimpleStringProperty(tf.getValue().getSoTKNhan()));
        tcSoTien.setCellValueFactory(tf -> tf.getValue().getSotienProperty());
        tcHoTenNV.setCellValueFactory(tf -> new SimpleStringProperty(tf.getValue().getHoTenNV()));
    }

    static void filteredTaiKhoan(ObservableList<TaiKhoan> obListTK, TextField tfSearch, TableView<TaiKhoan> tbTaiKhoan) {
        FilteredList<TaiKhoan> fd = new FilteredList<>(obListTK, b -> true);
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            fd.setPredicate(model -> {
                        if (newValue.isEmpty() || newValue.isBlank())
                            return true;
                        String searchKeyword = newValue.toLowerCase();
                        if (model.getId().toLowerCase().contains(searchKeyword))
                            return true;
                        if (model.getMaCN().toLowerCase().contains(searchKeyword))
                            return true;
                        if (model.getSoDT().toLowerCase().contains(searchKeyword))
                            return true;
                        if (model.getNgayMoTK().toString().toLowerCase().contains(searchKeyword))
                            return true;
                        else return (model.getCmnd().toLowerCase().contains(searchKeyword));
                    }
            );
        });
        SortedList<TaiKhoan> sortedData = new SortedList<>(fd);
        sortedData.comparatorProperty().bind(tbTaiKhoan.comparatorProperty());
        tbTaiKhoan.setItems(sortedData);
    }

    @Override
    void initTableEvent() {
        initTableDoubleCLickOnRow();
    }

    private void initTableDoubleCLickOnRow() {
        tbTaiKhoan.setRowFactory(tv -> {
            TableRow<TaiKhoan> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    TaiKhoan taiKhoan = row.getItem();
                    String id = taiKhoan.getId();
                    boolean check;

                    List<String> list = new ArrayList<>();

                    String send = "Tài khoản chuyển";
                    String received = "Tài khoản nhận";

                    list.add(send);
                    list.add(received);

                    String type = FXAlerts.confirm(String.format("%s là tài khoản chuyển hay nhận", taiKhoan.getId()), list);
                    if (Objects.equals(type, send)) {
                        if (tfSoTKChuyen.getText().equals(id)) return;
                        if (!tfSoTKChuyen.getText().isBlank() && !id.equals(tfSoTKChuyen.getText())) {
                            check = FXAlerts.confirm(String.format("Thay đổi số tài khoản chuyển %s", taiKhoan.getId()));
                            if (!check) return;
                        }
                        tfSoTKChuyen.setText(id);
                    } else if (Objects.equals(type, received)) {
                        if (tfSoTKNhan.getText().equals(id)) return;
                        if (!tfSoTKNhan.getText().isBlank() && !id.equals(tfSoTKNhan.getText())) {
                            check = FXAlerts.confirm(String.format("Thay đổi số tài khoản nhận %s", taiKhoan.getId()));
                            if (!check) return;
                        }
                        tfSoTKNhan.setText(id);
                    }
                }
            });
            return row;
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
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
    void btnThem(ActionEvent actionEvent) {
        boolean check = FXAlerts.confirm("Bạn có chắc chắn muốn tạo phiên làm việc mới");
        if (!check) return;
        tfSoTKChuyen.setText("");
        tfSoTien.setText("");
        tfSoTKNhan.setText("");
    }

    @Override
        //Không làm gì hết
    void btnXoa(ActionEvent actionEvent) {

    }

    @Override
        //Không làm gì hết
    void btnSua(ActionEvent actionEvent) {
    }

    @Override
    void btnGhi(ActionEvent actionEvent) {
        String soTKChuyen = tfSoTKChuyen.getText().replaceAll(" ", "").trim();
        String soTKNhan = tfSoTKNhan.getText().replaceAll(" ", "").trim();
        BigInteger soTien = new BigInteger("0");
        if (soTKChuyen.isBlank()) {
            FXAlerts.warning("Chưa nhập số tài khoản chuyển");
            return;
        }
        if (soTKNhan.isBlank()) {
            FXAlerts.warning("Chưa nhập số tài khoản nhận");
            return;
        }
        if (soTKNhan.equals(soTKChuyen)) {
            FXAlerts.warning("Số tài khoản chuyển và nhận trùng nhau. \nNếu muốn gửi tiền vui lòng chọn dịch vụ gửi tiền");
            return;
        }
        if (tfSoTien.getText().isBlank()) {
            FXAlerts.warning("Chưa nhập số tiền cần chuyển");
            return;
        } else {
            soTien = new BigInteger(tfSoTien.getText());
        }
        boolean check = false;
        String maNV = JavaFXApplication.maNV;
        if (maNV.isBlank()) {
            FXAlerts.warning("Thiếu nhân viên thực hiện công việc");
            return;
        }
        check = FXAlerts.confirm(String.format("""
                Bạn có chắc chắn muốn thực hiện chuyển tiền\040
                Từ tài khoản:               %s\040
                Đến tài khoản:              %s
                Với số tiền là:             %s
                Ngày thực hiện giao dịch:   %s""", soTKChuyen, soTKNhan, formatCurrency(soTien), LocalDateTime.now().toString()));
        if (!check) return;
        BigInteger finalSoTien = soTien; // Do cái java nó yêu cầu chứ em không có muốn làm như vậy đâu
        new Thread(() -> {
            load();
            Map<String, String> result = chuyenTienRepository.send(soTKChuyen, soTKNhan, finalSoTien, LocalDateTime.now(), maNV);
            showResult(result.get("ISSUCCESS"), result.get("MSG"));
            Platform.runLater(this::updateData);
            unload();
        }).start();
    }

    @Override
    void btnHoanTac(ActionEvent actionEvent) {

    }

    @Override
    void updateData() {
        List<TaiKhoan> listTK = taiKhoanRepository.findAll();
        List<GDChuyenTien> list = chuyenTienRepository.findAll();
        obListGD = FXCollections.observableArrayList(list);
        obListTK = FXCollections.observableArrayList(listTK);
        initTableView();
    }

    @Override
    void initValidation() {
        Validation.valideSoTK(tfSoTKChuyen);
        Validation.valideSoTK(tfSoTKNhan);
        Validation.valideSoTien(tfSoTien, ValidationValue.maxGD, ValidationValue.minGDChuyenTien);
        formatSoTienToLabel(tfSoTien, lableSoTienFormated);
    }

    @Override
    void initTableView() {
        list2Table(tcMaGD, tcMaNV, tcNgayGD, tcSoTKGui, tcSoTKNhan, tcSoTien, tcHoTenNV);
        TaiKhoanController.list2TableTK(tcSoTK, tcCMNDTK, tcSoDuTK, tcChiNhanhTK, tcNgayMoTK, tcHoTen, tcSoDienThoai);
        tbTaiKhoan.setItems(obListTK);
        tbChuyenTien.setItems(obListGD);
        filtered();
    }

    @Override
    public void filtered() {
        filteredTaiKhoan();
        filteredGD();
    }

    void filteredTaiKhoan() {
        filteredTaiKhoan(obListTK, tfSearch, tbTaiKhoan);
    }

    @Override
    void unFiltered() {
        tbTaiKhoan.getSelectionModel().clearSelection();
        tbChuyenTien.setItems(obListGD);
    }

    void filteredGD() {
        FilteredList<GDChuyenTien> fd = new FilteredList<>(obListGD, b -> true);
        tbTaiKhoan.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            fd.setPredicate(model -> {
                        if (newSelection == null)
                            return true;
                        String id = newSelection.getId();
                        if (model.getSoTKChuyen().equals(id))
                            return true;
                        else return model.getSoTKNhan().equals(id);
                    }
            );
        });
        SortedList<GDChuyenTien> sortedData = new SortedList<>(fd);
        sortedData.comparatorProperty().bind(tbChuyenTien.comparatorProperty());
        tbChuyenTien.setItems(sortedData);
    }

    @Override
    void initCBChiNhanhEvent() {
        cbChiNhanh.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            new Thread(() -> { // Lambda Expression
                pnCN.setDisable(true);
                List<GDChuyenTien> lst;
                try {
                    if (!Objects.equals(newValue.getTencn(), JavaFXApplication.phanManh)) {
                        lst = chuyenTienRepository.findRemoteAll();
                        obListGD = FXCollections.observableArrayList(lst);
                    } else {
                        lst = chuyenTienRepository.findAll();
                        obListGD = FXCollections.observableArrayList(lst);
                    }
                    tbChuyenTien.setItems(obListGD);
                } catch (Exception e) {
                    FXAlerts.error("Có lỗi bất thường");
                }
                pnCN.setDisable(false);
            }).start();
        });
    }
}
