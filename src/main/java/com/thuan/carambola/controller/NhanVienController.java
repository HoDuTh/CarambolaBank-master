package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.Service.Validation;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entitygeneral.NhanVien;
import com.thuan.carambola.entityprimary.VDsPhanmanhEntity;
import com.thuan.carambola.recovery.Handle;
import com.thuan.carambola.repositorygeneral.NhanVienRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Component
public class NhanVienController extends BaseController implements Initializable {


    NhanVienRepository nhanVienRepository;
    PhanManhRepository phanManhRepository;
    Stack<Handle<NhanVien>> stack;
    ObservableList<NhanVien> obList;
    Logger log = LoggerFactory.getLogger(NhanVienController.class);

    @FXML
    private Button btnChuyenCN;
    @FXML
    private Button btnTaoTaiKhoan;
    @FXML
    private ComboBox<VDsPhanmanhEntity> cbChiNhanh;
    @FXML
    private AnchorPane pInput;
    @FXML
    private TableView<NhanVien> tbNhanVien;
    @FXML
    private TableColumn<NhanVien, String> tc1ChiNhanh;
    @FXML
    private TableColumn<NhanVien, String> tc1DiaChi;
    @FXML
    private TableColumn<NhanVien, String> tc1Ho;
    @FXML
    private TableColumn<NhanVien, String> tc1MaNV;
    @FXML
    private TableColumn<NhanVien, String> tc1Phai;
    @FXML
    private TableColumn<NhanVien, String> tc1SoDT;
    @FXML
    private TableColumn<NhanVien, String> tc1Ten;
    @FXML
    private TableColumn<NhanVien, String> tc1TrangThaiXoa;
    @FXML
    private TextField tfDiaChi;
    @FXML
    private TextField tfHo;
    @FXML
    private TextField tfMANV;
    @FXML
    private TextField tfSearch;
    @FXML
    private TextField tfSoDienThoai;
    @FXML
    private TextField tfTen;
    @FXML
    private ToggleGroup tgGioiTinh;

    @Autowired
    public NhanVienController(NhanVienRepository nhanVienRepository,
                              PhanManhRepository phanManhRepository) {
        super(phanManhRepository);
        this.nhanVienRepository = nhanVienRepository;
        this.phanManhRepository = phanManhRepository;
        stack = new Stack<Handle<NhanVien>>();

    }

    private void btnChuyenCN(ActionEvent actionEvent) {
        NhanVien nhanVien = tbNhanVien.getSelectionModel().getSelectedItem();
        if (nhanVien == null) {
            FXAlerts.warning("Bạn chưa chọn nhân viên chuyển");
            return;
        }
        boolean check = FXAlerts.confirm(String.format("Bạn có thực sự muốn chuyển nhân viên %s với số MANV là %s", nhanVien.getHoTen(), nhanVien.getId()));
        if (!check) return;
        TextInputDialog td = new TextInputDialog("");
        td.setHeaderText("Nhập mã nhân viên mới");
        Validation.valideMaNV(td.getEditor());
        td.showAndWait();
        String n = td.getEditor().getText();
        if (n.isBlank()) {
            FXAlerts.warning("Mã nhân viên mới rỗng");
            return;
        }
        new Thread(() -> {
            load();
            Map<String, String> result = nhanVienRepository.move(nhanVien.getId(), n);
            String msg = result.get("MSG");
            Platform.runLater(() -> {
                if (result.get("ISSUCCESS").equals("1")) {
                    Handle<NhanVien> handle = new Handle<>();
                    handle.setEntity(nhanVien);
                    handle.setAction("xoa");
                    stack.push(handle);
                    FXAlerts.info(msg);
                } else {
                    FXAlerts.error(msg);
                }
                updateData();
            });
            unload();
        }).start();
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
        tc1TrangThaiXoa.setCellValueFactory(tf -> new SimpleStringProperty(tf.getValue().getTrangThaiXoa()));
    }

    @Scheduled(fixedRate = reloadTimer)
    public void scheduleTaskWithFixedRate() {
        Platform.runLater(() -> {
            if (StageInitializer.currentResource == StageInitializer.nhanVien) {
                updateData();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        if(Objects.equals(JavaFXApplication.nhom, "ChiNhanh"))
        {
            btnTaoTaiKhoan.setDisable(false);
            btnChuyenCN.setDisable(false);
        }
        btnTaoTaiKhoan.setOnAction(this::btnTaoTaiKhoan);
        btnChuyenCN.setOnAction(this::btnChuyenCN);
    }

    private void btnTaoTaiKhoan(ActionEvent actionEvent) {
        NhanVien nhanVien = tbNhanVien.getSelectionModel().getSelectedItem();
        if (nhanVien == null) {
            FXAlerts.warning("Bạn chưa chọn nhân viên để tạo tài khoản đăng nhập");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for(String a: nhanVien.getHoTen().split(" "))
        {
            sb.append(a.toUpperCase().charAt(0));
        }
        String loginName = sb + nhanVien.getId();
        boolean check = FXAlerts.confirm(
                String.format(
                        "Bạn có thực sự muốn tạo tài khoản đăng nhập cho nhân viên %s\n Với  MANV là %s \n Và tên đăng nhập là %s" , nhanVien.getHoTen(), nhanVien.getId(), loginName));
        if (!check) return;


        TextInputDialog td = new TextInputDialog("");
        td.setHeaderText("Nhập mật khẩu");
        Validation.valideMaNV(td.getEditor());
        td.showAndWait();
        String password = td.getEditor().getText();
        if (password.isBlank()) {
            FXAlerts.warning("Mật khẩu không được để trống");
            return;
        }
        new Thread(() -> {
            load();
            Platform.runLater(()->{
                btnChuyenCN.setDisable(true);
                btnTaoTaiKhoan.setDisable(true);
            });
            String result = nhanVienRepository.taoLogin(loginName, password, nhanVien.getId(), JavaFXApplication.nhom);
            Platform.runLater(() -> {
                FXAlerts.warning(result);
                btnChuyenCN.setDisable(false);
                btnTaoTaiKhoan.setDisable(false);
            });
            unload();
        }).start();
    }

    @Override
    void initTableEvent() {
        initTableDoubleCLickOnRow();
    }

    private void initTableDoubleCLickOnRow() {
        tbNhanVien.setRowFactory(tv -> {
            TableRow<NhanVien> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    NhanVien nhanVien = row.getItem();
                    if (nhanVien.getId().equals(tfMANV.getText())) return;
                    boolean check;
                    if (!checkEmtyInput()) {
                        check = FXAlerts.confirm(String.format("Chỉnh sửa nhân viên %s", nhanVien.getHoTen()));
                        if (!check) return;
                    }
                    insertDataToInputBox(nhanVien);
                }
            });
            return row;
        });
    }

    private void insertDataToInputBox(NhanVien nhanVien) {
        tfDiaChi.setText(nhanVien.getDiaChi());
        tfHo.setText(nhanVien.getHo());
        tfMANV.setText(nhanVien.getId());
        tfSoDienThoai.setText(nhanVien.getSoDT());
        tfTen.setText(nhanVien.getTen());
    }

    private boolean checkEmtyInput() {
        return tfDiaChi.getText().isBlank()
                || tfHo.getText().isBlank()
                || tfMANV.getText().isBlank()
                || tfHo.getText().isBlank()
                || tfSoDienThoai.getText().isBlank()
                || tfTen.getText().isBlank();
    }

    @Override
    void updateData() {
        List<NhanVien> list = nhanVienRepository.findAll();
        obList = FXCollections.observableArrayList(list);
        initTableView();
    }

    @Override
    void btnThem(ActionEvent actionEvent) {
        boolean check = FXAlerts.confirm("Bạn có chắc chắn muốn tạo phiên làm việc mới");
        if (!check) return;

        tfMANV.setText("");
        tfDiaChi.setText("");
        tfHo.setText("");
        tfSoDienThoai.setText("");
        tfTen.setText("");
    }

    @Override
    void btnXoa(ActionEvent actionEvent) {
        NhanVien nhanVien = tbNhanVien.getSelectionModel().getSelectedItem();
        if (nhanVien == null) {
            FXAlerts.warning("Bạn chưa chọn nhân viên để xóa");
            return;
        }
        boolean check = FXAlerts.confirm(String.format("Bạn có thực sự muốn xóa nhân viên %s với số MANV là %s", nhanVien.getHoTen(), nhanVien.getId()));
        if (!check) return;

        new Thread(() -> {
            load();
            Map<String, String> result = nhanVienRepository.delete(nhanVien.getId());
            String msg = result.get("MSG");
            Platform.runLater(() -> {
                if (result.get("ISSUCCESS").equals("1")) {
                    Handle<NhanVien> handle = new Handle<>();
                    handle.setEntity(nhanVien);
                    handle.setAction("xoa");
                    stack.push(handle);
                    FXAlerts.info(msg);
                } else {
                    FXAlerts.error(msg);
                }
                updateData();
            });
            unload();
        }).start();
    }

    @Override
    void btnSua(ActionEvent actionEvent) {
        NhanVien nhanVien    = tbNhanVien.getSelectionModel().getSelectedItem();
        if (nhanVien == null) {
            FXAlerts.warning("Chưa chọn nhân viên để sửa");
            return;
        }
        String maNV = nhanVien.getId();
        String ho = tfHo.getText();
        if (ho.isBlank()) {
            FXAlerts.warning("Thiếu họ");
            return;
        }
        String ten = tfTen.getText();
        if (ten.isBlank()) {
            FXAlerts.warning("Thiếu tên");
            return;
        }
        String diaChi = tfDiaChi.getText();
        if (diaChi.isBlank()) {
            FXAlerts.warning("Thiếu địa chỉ");
            return;
        }
        String soDT = tfSoDienThoai.getText();
        RadioButton selectedRadioButton = (RadioButton) tgGioiTinh.getSelectedToggle();
        if (selectedRadioButton == null) {
            FXAlerts.warning("Chưa chọn giới tính");
            return;
        }
        String phai = selectedRadioButton.getText();
        String hoTen = ho + " " + ten;
        boolean check = FXAlerts.confirm(String.format("""
                        Bạn có thực sự muốn sửa nhân viên với thông tin:
                        Mã NV    :  %s
                        Họ và tên:  %s -> %s
                        Địa chỉ:    %s -> %s
                        Phái:       %s -> %s
                        Số điện thoại: %s -> %s\s""",
                maNV,
                nhanVien.getHoTen(), hoTen,
                nhanVien.getDiaChi(), diaChi,
                nhanVien.getPhai(), phai,
                nhanVien.getSoDT(), soDT)
                );
        if (!check) return;
        new Thread(() -> {
            load();
            Map<String, String> result = nhanVienRepository.edit(maNV, ho, ten, diaChi, phai, soDT);
            String msg = result.get("MSG");
            Platform.runLater(() -> {
                if (result.get("ISSUCCESS").equals("1")) {
                    Handle<NhanVien> handle = new Handle<>();
                    handle.setEntity(nhanVien);
                    handle.setAction("sua");
                    stack.push(handle);
                    FXAlerts.info(msg);
                } else {
                    FXAlerts.error(msg);
                }
                updateData();
                unload();
            });
        }).start();
    }

    @Override
    void btnGhi(ActionEvent actionEvent) {
        String maNV = tfMANV.getText();
        if (maNV.isBlank()) {
            FXAlerts.warning("Thiếu mã nhân viên");
            return;
        }
        String ho = tfHo.getText();
        if (ho.isBlank()) {
            FXAlerts.warning("Thiếu họ");
            return;
        }
        String ten = tfTen.getText();
        if (ten.isBlank()) {
            FXAlerts.warning("Thiếu tên");
            return;
        }
        String diaChi = tfDiaChi.getText();
        if (diaChi.isBlank()) {
            FXAlerts.warning("Thiếu địa chỉ");
            return;
        }
        String soDT = tfSoDienThoai.getText();
        RadioButton selectedRadioButton = (RadioButton) tgGioiTinh.getSelectedToggle();
        if (selectedRadioButton == null) {
            FXAlerts.warning("Chưa chọn giới tính");
            return;
        }
        String phai = selectedRadioButton.getText();
        String hoTen = ho + " " + ten;
        boolean check = FXAlerts.confirm(String.format("""
                Bạn có thực sự muốn tạo nhân viên với thông tin:
                Mã nhân viên: %s
                Họ và tên: %s
                Địa chỉ: %s
                Phái: %s
                Số điện thoại: %s\s""", maNV, hoTen, diaChi, phai, soDT));
        if (!check) return;
        new Thread(() -> {
            load();
            Map<String, String> result = nhanVienRepository.add(maNV, ho, ten, diaChi, phai, soDT, "0");
            String msg = result.get("MSG");
            Platform.runLater(() -> {
                if (result.get("ISSUCCESS").equals("1")) {
                    NhanVien nv = new NhanVien();
                    nv.setId(maNV);
                    nv.setHo(ho);
                    nv.setTen(ten);
                    nv.setDiaChi(diaChi);
                    nv.setPhai(phai);
                    nv.setSoDT(soDT);
                    nv.setTrangThaiXoa(0);
                    Handle<NhanVien> handle = new Handle<>();
                    handle.setEntity(nv);
                    handle.setAction("ghi");
                    stack.push(handle);
                    FXAlerts.info(msg);
                } else {
                    FXAlerts.error(msg);
                }
                updateData();
                unload();
            });
        }).start();
    }

    @Override
    void btnHoanTac(ActionEvent actionEvent) {
        if (stack.isEmpty()) {
            FXAlerts.info("Không còn tác vụ để hoàn tác");
            return;
        }
        Handle<NhanVien> handler = stack.pop();
        NhanVien nhanVien = handler.getEntity();
        boolean check;
        switch (handler.getAction()) {
            case "ghi" -> {
                check = FXAlerts.confirm(String.format("""
                                Bạn có thực sự muốn loại bỏ việc thêm nhân viên với thông tin:
                                Mã nhân viên: %s
                                Họ và tên:  %s
                                Địa chỉ:    %s
                                Phái:       %s
                                Số điện thoại: %s\s""",
                        nhanVien.getId(),
                        nhanVien.getHoTen(),
                        nhanVien.getDiaChi(),
                        nhanVien.getPhai(),
                        nhanVien.getSoDT()));
                if (!check) {
                    stack.push(handler);
                    return;
                }
                new Thread(() -> {
                    load();
                    Map<String, String> result = nhanVienRepository.delete(nhanVien.getId());
                    showResult(result.get("ISSUCCESS"), result.get("MSG"));
                    updateData();
                    unload();
                }).start();
            }
            case "sua" -> {
                check = FXAlerts.confirm(String.format("""
                                Bạn có thực sự muốn hoàn lại nhân viên với thông tin:
                                CMND: %s
                                Họ và tên:  %s
                                Địa chỉ:    %s
                                Phái:       %s
                                Số điện thoại: %s\s""",
                        nhanVien.getId(),
                        nhanVien.getHoTen(),
                        nhanVien.getDiaChi(),
                        nhanVien.getPhai(),
                        nhanVien.getSoDT()));
                if (!check) {
                    stack.push(handler);
                    return;
                }
                ;
                new Thread(() -> {
                    load();
                    Map<String, String> result = nhanVienRepository.edit(
                            nhanVien.getId(),
                            nhanVien.getHo(),
                            nhanVien.getTen(),
                            nhanVien.getDiaChi(),
                            nhanVien.getPhai(),
                            nhanVien.getSoDT()
                    );
                    showResult(result.get("ISSUCCESS"), result.get("MSG"));
                    updateData();
                    unload();
                }).start();
            }
            case "xoa" -> {
                check = FXAlerts.confirm(String.format("""
                                Bạn có thực sự muốn hoàn tác việc xóa nhân viên với thông tin:
                                Mã NV: %s
                                Họ và tên: %s
                                Địa chỉ: %s
                                Phái: %s
                                Số điện thoại: %s\s""",
                        nhanVien.getId(),
                        nhanVien.getHoTen(),
                        nhanVien.getDiaChi(),
                        nhanVien.getPhai(),
                        nhanVien.getSoDT()));
                if (!check) {
                    stack.push(handler);
                    return;
                }
                new Thread(() -> {
                    load();
                    Map<String, String> result = nhanVienRepository.add(
                            nhanVien.getId(),
                            nhanVien.getHo(),
                            nhanVien.getTen(),
                            nhanVien.getDiaChi(),
                            nhanVien.getPhai(),
                            nhanVien.getSoDT(),"0");
                    showResult(result.get("ISSUCCESS"), result.get("MSG"));
                    updateData();
                    unload();
                }).start();
            }
            default -> {
                FXAlerts.info("Tác vụ đã lưu không phù hợp");
            }
        }
    }

    @Override
    void filtered() {
        filteredNhanVien();
    }

    @Override
    void unFiltered() {
        tbNhanVien.setItems(obList);
    }

    @Override
    void initValidation() {
        Validation.valideMaNV(tfMANV);
        Validation.valideHo(tfHo);
        Validation.valideTen(tfTen);
        Validation.valideDiaChi(tfDiaChi);
        Validation.valideSoDT(tfSoDienThoai);
    }

    @Override
    void initTableView() {
        list2Table(tc1ChiNhanh, tc1DiaChi, tc1Ho, tc1MaNV, tc1Phai, tc1SoDT, tc1Ten, tc1TrangThaiXoa);
        tbNhanVien.setItems(obList);
        filtered();
    }

    @Override
    void initCBChiNhanhEvent() {
        cbChiNhanh.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            new Thread(() -> { // Lambda Expression
                pnCN.setDisable(true);
                List<NhanVien> lst;
                try {
                    if (!Objects.equals(newValue.getTencn(), JavaFXApplication.phanManh)) {
                        lst = nhanVienRepository.findRemoteAll();
                        obList = FXCollections.observableArrayList(lst);
                    } else {
                        lst = nhanVienRepository.findAll();
                        obList = FXCollections.observableArrayList(lst);
                    }
                    tbNhanVien.setItems(obList);
                } catch (Exception e) {
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

}
