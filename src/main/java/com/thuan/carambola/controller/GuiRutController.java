package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.Service.Validation;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entitygeneral.GDGoiRut;
import com.thuan.carambola.entitygeneral.TaiKhoan;
import com.thuan.carambola.recovery.Handle;
import com.thuan.carambola.repositorygeneral.GuiRutRepository;
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
import java.util.*;

@Component
public class GuiRutController extends BaseController implements Initializable {
    TaiKhoanRepository taiKhoanRepository;
    GuiRutRepository guiRutRepository;
    ObservableList<TaiKhoan> obListTK;
    ObservableList<GDGoiRut> obListGD;
    Stack stack;
    Logger log = LoggerFactory.getLogger(GuiRutController.class);
    @FXML
    private AnchorPane pInput;
    @FXML
    private RadioButton rbGuiTien;
    @FXML
    private RadioButton rbRutTien;
    //--------------table1:TaiKhoan------------------
    @FXML
    private TableView<TaiKhoan> tbTaiKhoan;
    @FXML
    private TableColumn<TaiKhoan, String> tc1CMNDTK;
    @FXML
    private TableColumn<TaiKhoan, String> tc1ChiNhanhTK;
    @FXML
    private TableColumn<TaiKhoan, String> tc1HoTen;
    @FXML
    private TableColumn<TaiKhoan, String> tc1NgayMoTK;
    @FXML
    private TableColumn<TaiKhoan, String> tc1SoDienThoai;
    @FXML
    private TableColumn<TaiKhoan, String> tc1SoDuTK;
    @FXML
    private TableColumn<TaiKhoan, String> tc1SoTK;
    //--------------table1-------------------------
    //--------------table2:GuiRut------------------
    @FXML
    private TableView<GDGoiRut> tbGuiRut;
    @FXML
    private TableColumn<GDGoiRut, String> tc2HoTenNV;
    @FXML
    private TableColumn<GDGoiRut, String> tc2LoaiGD;
    @FXML
    private TableColumn<GDGoiRut, String> tc2MaNV;
    @FXML
    private TableColumn<GDGoiRut, String> tc2NgayGD;
    @FXML
    private TableColumn<GDGoiRut, String> tc2SoTK;
    @FXML
    private TableColumn<GDGoiRut, String> tc2SoTien;
    @FXML
    private TableColumn<GDGoiRut, String> tc2MaGD;
    //--------------table2-------------------------
    @FXML
    private TextField tfSoTK;
    @FXML
    private TextField tfSoTien;
    @FXML
    private ToggleGroup tgLoaiGD;
    @FXML
    private Label lableSoTienFormated;
    @FXML
    private TextField tfSearch;

    @Autowired
    public GuiRutController(TaiKhoanRepository taiKhoanRepository,
                            GuiRutRepository guiRutRepository,
                            PhanManhRepository phanManhRepository) {
        super(phanManhRepository);
        this.taiKhoanRepository = taiKhoanRepository;
        this.guiRutRepository = guiRutRepository;
        this.phanManhRepository = phanManhRepository;
        stack = new Stack<Handle<GDGoiRut>>();
    }

    static void list2Table(TableColumn<GDGoiRut, String> tc2HoTenNV,
                           TableColumn<GDGoiRut, String> tc2LoaiGD,
                           TableColumn<GDGoiRut, String> tc2NgayGD,
                           TableColumn<GDGoiRut, String> tc2MaNV,
                           TableColumn<GDGoiRut, String> tc2SoTK,
                           TableColumn<GDGoiRut, String> tc2SoTien,
                           TableColumn<GDGoiRut, String> tc2MaGD) {
        tc2MaGD.setCellValueFactory(tf -> tf.getValue().getMaGDProperty());
        tc2SoTien.setCellValueFactory(tf -> tf.getValue().getSotienProperty());
        tc2SoTK.setCellValueFactory(tf -> new SimpleStringProperty(tf.getValue().getSoTK()));
        tc2MaNV.setCellValueFactory(tf -> new SimpleStringProperty(tf.getValue().getMaNV()));
        tc2NgayGD.setCellValueFactory(tf -> tf.getValue().getNgayGDProperty());
        tc2LoaiGD.setCellValueFactory(tf -> tf.getValue().getLoaiGDProperty());
        tc2HoTenNV.setCellValueFactory(tf -> new SimpleStringProperty(tf.getValue().getHoTenNV()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    @Scheduled(fixedRate = reloadTimer)
    public void scheduleTaskWithFixedRate() {
        Platform.runLater(() -> {
            if (StageInitializer.currentResource == StageInitializer.guiRut) {
                updateData();
            }
        });
    }

    @Override
    void updateData() {
        List<TaiKhoan> list = this.taiKhoanRepository.findAll();
        List<GDGoiRut> listGD = this.guiRutRepository.findAll();
        this.obListGD = FXCollections.observableArrayList(listGD);
        this.obListTK = FXCollections.observableArrayList(list);
        initTableView();
    }

    @Override
    void filtered() {
        filteredGD();
        ChuyenTienController.filteredTaiKhoan(obListTK, tfSearch, tbTaiKhoan);
    }

    @Override
    void unFiltered() {
        tbTaiKhoan.getSelectionModel().clearSelection();
        tbGuiRut.setItems(obListGD);
        filteredGD();
    }

    @Override
    void btnThem(ActionEvent actionEvent) {
        boolean check = FXAlerts.confirm("Bạn có chắc chắn muốn tạo phiên làm việc mới");
        if (!check) return;
        tfSoTK.setText("");
        tfSoTien.setText("");
        clearDateTime();
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
        //Không làm gì hết
    void btnHoanTac(ActionEvent actionEvent) {

    }

    @Override
    void btnGhi(ActionEvent actionEvent) {
        String soTK = tfSoTK.getText().replaceAll(" ", "").trim();
        BigInteger soTien = new BigInteger("0");
        if (soTK.isBlank()) {
            FXAlerts.warning("Chưa chọn số tài khoản");
            return;
        }
        if (tfSoTien.getText().isBlank()) {
            FXAlerts.warning("Chưa nhập số tiền cần chuyển");
            return;
        } else {
            soTien = new BigInteger(tfSoTien.getText());
        }
        String maNV = JavaFXApplication.maNV;
        if (maNV.isBlank()) {
            FXAlerts.warning("Thiếu nhân viên thực hiện công việc");
            return;
        }
        Instant ngayGD = getDateTime();
        RadioButton selectedRadioButton = (RadioButton) tgLoaiGD.getSelectedToggle();
        if (selectedRadioButton == null) {
            FXAlerts.warning("Chưa chọn loại giao dịch");
            return;
        }
        String loaiGD = selectedRadioButton.getText();
        if (loaiGD.equals("Gửi tiền"))
            loaiGD = "GT";
        else loaiGD = "RT";

        BigInteger finalSoTien = soTien;
        String finalLoaiGD = loaiGD;
        boolean check = FXAlerts.confirm(String.format("""
                Bạn có chắc chắn muốn thực hiện %s\040
                Với tài khoản:              %s\040
                Với số tiền là:             %s
                Ngày thực hiện giao dịch:   %s""", selectedRadioButton.getText(), soTK, formatCurrency(soTien), ngayGD.toString()));
        if (!check) return;
        new Thread(() -> {
            load();
            Map<String, String> result = guiRutRepository.send(soTK, finalSoTien, ngayGD, finalLoaiGD, maNV);
            showResult(result.get("MSG"), result.get("ISSUCCESS"));
            updateData();
            unload();
        }).start();
    }

    @Override
    void initValidation() {
        Validation.valideSoTK(tfSoTK);
        Validation.valideSoTien(tfSoTien, ValidationValue.maxGD, ValidationValue.minGDGuiRut);
        formatSoTienToLabel(tfSoTien, lableSoTienFormated);
    }

    @Override
    void initTableView() {
        list2Table(tc2HoTenNV, tc2LoaiGD, tc2NgayGD, tc2MaNV, tc2SoTK, tc2SoTien, tc2MaGD);
        TaiKhoanController.list2TableTK(tc1SoTK, tc1CMNDTK, tc1SoDuTK, tc1ChiNhanhTK, tc1NgayMoTK, tc1HoTen, tc1SoDienThoai);
        tbTaiKhoan.setItems(obListTK);
        tbGuiRut.setItems(obListGD);
        filtered();
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
                    if (tfSoTK.getText().equals(taiKhoan.getId())) return;
                    boolean check;
                    if (!tfSoTK.getText().isBlank()) {
                        check = FXAlerts.confirm(String.format("Tạo giao dịch gửi rút với  %s", taiKhoan.getId()));
                        if (!check) return;
                    }
                    tfSoTK.setText(taiKhoan.getId());
                }
            });
            return row;
        });
    }

    void filteredGD() {
        FilteredList<GDGoiRut> fd = new FilteredList<>(obListGD, b -> true);
        tbTaiKhoan.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            fd.setPredicate(model -> {
                        if (newSelection == null)
                            return true;
                        String id = newSelection.getId();
                        return model.getSoTK().equals(id);
                    }
            );
        });
        SortedList<GDGoiRut> sortedData = new SortedList<>(fd);
        sortedData.comparatorProperty().bind(tbGuiRut.comparatorProperty());
        tbGuiRut.setItems(sortedData);
    }

    @Override
    void initCBChiNhanhEvent() {
        cbChiNhanh.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            new Thread(() -> {
                pnCN.setDisable(true);
                List<GDGoiRut> lst;
                try {
                    if (!Objects.equals(newValue.getTencn(), JavaFXApplication.phanManh)) {
                        lst = guiRutRepository.findRemoteAll();
                        obListGD = FXCollections.observableArrayList(lst);
                    } else {
                        lst = guiRutRepository.findAll();
                        obListGD = FXCollections.observableArrayList(lst);
                    }
                    tbGuiRut.setItems(obListGD);
                } catch (Exception e) {
                    FXAlerts.error("Có lỗi bất thường");
                }
                pnCN.setDisable(false);
            }).start();
        });
    }
}
