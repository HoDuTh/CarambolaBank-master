package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.Service.DateTimeService;
import com.thuan.carambola.Service.Validation;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entitygeneral.KhachHang;
import com.thuan.carambola.recovery.Handle;
import com.thuan.carambola.repositorygeneral.KhachHangRepository;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.application.Platform;
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

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Component
public class KhachHangController extends BaseController implements Initializable {

    @FXML
    DatePicker dpNgay;
    KhachHangRepository khachHangRepository;
    ObservableList<KhachHang> obList;
    Stack<Handle<KhachHang>> stack;
    Logger log = LoggerFactory.getLogger(KhachHangController.class);
    @FXML
    private AnchorPane pInput;
    @FXML
    private TextField tfCMND;
    @FXML
    private TextField tfDiaChi;
    @FXML
    private TextField tfHo;
    @FXML
    private TextField tfSearch;
    @FXML
    private TextField tfSoDienThoai;
    @FXML
    private TextField tfTen;
    @FXML
    private ToggleGroup tgGioiTinh;
    @FXML
    private RadioButton rbNam;
    @FXML
    private RadioButton rbNu;
    @FXML
    private TableView<KhachHang> tbKhachHang;
    @FXML
    private TableColumn<KhachHang, String> tc1ChiNhanh;
    @FXML
    private TableColumn<KhachHang, String> tc1DiaChi;
    @FXML
    private TableColumn<KhachHang, String> tc1Ho;
    @FXML
    private TableColumn<KhachHang, String> tc1NgayCap;
    @FXML
    private TableColumn<KhachHang, String> tc1Phai;
    @FXML
    private TableColumn<KhachHang, String> tc1SoDienThoai;
    @FXML
    private TableColumn<KhachHang, String> tc1Ten;
    @FXML
    private TableColumn<KhachHang, String> tc1CMND;

    @Autowired
    public KhachHangController(KhachHangRepository khachHangRepository,
                               PhanManhRepository phanManhRepository) {
        super(phanManhRepository);
        this.khachHangRepository = khachHangRepository;
        this.phanManhRepository = phanManhRepository;
        stack = new Stack<Handle<KhachHang>>();
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
    public void initialize(URL location, ResourceBundle resources) {
//        initTimePicker();
        super.initialize(location, resources);
    }

    @Scheduled(fixedRate = reloadTimer)
    public void scheduleTaskWithFixedRate() {
        Platform.runLater(() -> {
            if (StageInitializer.currentResource == StageInitializer.khachHang) {
                updateData();
            }
        });
    }

    @Override
    void initTableEvent() {
        initTableDoubleCLickOnRow();
    }

    private void initTableDoubleCLickOnRow() {
        tbKhachHang.setRowFactory(tv -> {
            TableRow<KhachHang> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    KhachHang khachHang = row.getItem();
                    boolean check;
                    if (khachHang.getId().equals(tfCMND.getText())) return;
                    if (!checkEmtyInput()) {
                        check = FXAlerts.confirm(String.format("Chỉnh sửa khách hàng %s", khachHang.getHoTen()));
                        if (!check) return;
                    }
                    insertDataToInputBox(khachHang);
                }
            });
            return row;
        });
    }

    private void insertDataToInputBox(KhachHang khachHang) {
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
    }

    @Override
    void updateData() {
        List<KhachHang> list = khachHangRepository.findAll();
        obList = FXCollections.observableArrayList(list);
        initTableView();
    }

    @Override
    void btnThem(ActionEvent actionEvent) {
        boolean check = FXAlerts.confirm("Bạn có chắc chắn muốn tạo phiên làm việc mới");
        if (!check) return;

        tfCMND.setText("");
        tfDiaChi.setText("");
        tfHo.setText("");
        tfSoDienThoai.setText("");
        tfTen.setText("");
        dpNgay.getEditor().clear();
        dpNgay.setValue(LocalDate.now());
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
        KhachHang khachHang = tbKhachHang.getSelectionModel().getSelectedItem();
        if (khachHang == null) {
            FXAlerts.warning("Bạn chưa chọn khách hàng để xóa");
            return;
        }
        boolean check = FXAlerts.confirm(String.format("Bạn có thực sự muốn xóa khách hàng %s với số CMND là %s", khachHang.getHoTen(), khachHang.getId()));
        if (!check) return;

        new Thread(() -> {
            load();
            Map<String, String> result = khachHangRepository.delete(khachHang.getId());
            String msg = result.get("MSG");
            Platform.runLater(() -> {
                if (result.get("ISSUCCESS").equals("1")) {
                    Handle<KhachHang> handle = new Handle<>();
                    handle.setEntity(khachHang);
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
        KhachHang khachHang = tbKhachHang.getSelectionModel().getSelectedItem();
        if (khachHang == null) {
            FXAlerts.warning("Chưa chọn khách hàng để sửa");
            return;
        }
        String cmnd = khachHang.getId();
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
        Instant ngay = dpNgay.getValue().atStartOfDay(ZoneId.of("UTC")).toInstant();
        RadioButton selectedRadioButton = (RadioButton) tgGioiTinh.getSelectedToggle();
        if (selectedRadioButton == null) {
            FXAlerts.warning("Chưa chọn giới tính");
            return;
        }
        String phai = selectedRadioButton.getText();
        String hoTen = ho + " " + ten;
        boolean check = FXAlerts.confirm(String.format("""
                        Bạn có thực sự muốn sửa khách hàng với thông tin:
                        CMND: %s
                        Họ và tên:  %s -> %s
                        Địa chỉ:    %s -> %s
                        Phái:       %s -> %s
                        Ngày cấp:   %s -> %s
                        Số điện thoại: %s -> %s\s""",
                cmnd,
                khachHang.getHoTen(), hoTen,
                khachHang.getDiaChi(), diaChi,
                khachHang.getPhai(), phai,
                khachHang.getNgayCap(), ngay.toString(),
                khachHang.getSoDT(), soDT));
        if (!check) return;
        new Thread(() -> {
            load();
            Map<String, String> result = khachHangRepository.edit(cmnd, ho, ten, diaChi, phai, ngay, soDT);
            String msg = result.get("MSG");
            Platform.runLater(() -> {
                if (result.get("ISSUCCESS").equals("1")) {
                    Handle<KhachHang> handle = new Handle<>();
                    handle.setEntity(khachHang);
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
        String cmnd = tfCMND.getText();
        if (cmnd.isBlank()) {
            FXAlerts.warning("Thiếu CMND");
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
        Instant ngay = dpNgay.getValue().atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        RadioButton selectedRadioButton = (RadioButton) tgGioiTinh.getSelectedToggle();
        if (selectedRadioButton == null) {
            FXAlerts.warning("Chưa chọn giới tính");
            return;
        }
        String phai = selectedRadioButton.getText();
        String hoTen = ho + " " + ten;
        boolean check = FXAlerts.confirm(String.format("""
                Bạn có thực sự muốn tạo khách hàng với thông tin:
                CMND: %s
                Họ và tên: %s
                Địa chỉ: %s
                Phái: %s
                Ngày cấp: %s
                Số điện thoại: %s\s""", cmnd, hoTen, diaChi, phai, ngay.toString(), soDT));
        if (!check) return;
        new Thread(() -> {
            load();
            Map<String, String> result = khachHangRepository.add(cmnd, ho, ten, diaChi, phai, ngay, soDT);
            String msg = result.get("MSG");
            Platform.runLater(() -> {
                if (result.get("ISSUCCESS").equals("1")) {
                    KhachHang kh = new KhachHang();
                    kh.setId(cmnd);
                    kh.setHo(ho);
                    kh.setTen(ten);
                    kh.setDiaChi(diaChi);
                    kh.setPhai(phai);
                    kh.setNgayCap(ngay);
                    kh.setSoDT(soDT);
                    Handle<KhachHang> handle = new Handle<>();
                    handle.setEntity(kh);
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
            FXAlerts.info("Không còn tác vụ để hòan tác");
            return;
        }
        Handle<KhachHang> handler = stack.pop();
        KhachHang khachHang = handler.getEntity();
        boolean check;
        switch (handler.getAction()) {
            case "ghi" -> {
                check = FXAlerts.confirm(String.format("""
                                Bạn có thực sự muốn loại bỏ việc thêm khách hàng với thông tin:
                                CMND: %s
                                Họ và tên:  %s
                                Địa chỉ:    %s
                                Phái:       %s
                                Ngày cấp:   %s
                                Số điện thoại: %s\s""",
                        khachHang.getId(),
                        khachHang.getHoTen(),
                        khachHang.getDiaChi(),
                        khachHang.getPhai(),
                        khachHang.getNgayCap(),
                        khachHang.getSoDT()));
                if (!check) {
                    stack.push(handler);
                    return;
                }
                ;
                new Thread(() -> {
                    load();
                    Map<String, String> result = khachHangRepository.delete(khachHang.getId());
                    showResult(result.get("ISSUCCESS"), result.get("MSG"));
                    updateData();
                    unload();
                }).start();
            }
            case "sua" -> {
                check = FXAlerts.confirm(String.format("""
                                Bạn có thực sự muốn hoàn lại khách hàng với thông tin:
                                CMND: %s
                                Họ và tên:  %s
                                Địa chỉ:    %s
                                Phái:       %s
                                Ngày cấp:   %s
                                Số điện thoại: %s\s""",
                        khachHang.getId(),
                        khachHang.getHoTen(),
                        khachHang.getDiaChi(),
                        khachHang.getPhai(),
                        khachHang.getNgayCap(),
                        khachHang.getSoDT()));
                if (!check) {
                    stack.push(handler);
                    return;
                }
                ;
                new Thread(() -> {
                    load();
                    Map<String, String> result = khachHangRepository.edit(
                            khachHang.getId(),
                            khachHang.getHo(),
                            khachHang.getTen(),
                            khachHang.getDiaChi(),
                            khachHang.getPhai(),
                            khachHang.getNgayCap(),
                            khachHang.getSoDT()
                    );
                    showResult(result.get("ISSUCCESS"), result.get("MSG"));
                    updateData();
                    unload();
                }).start();
            }
            case "xoa" -> {
                check = FXAlerts.confirm(String.format("""
                                Bạn có thực sự muốn hoàn tác việc xóa khách hàng với thông tin:
                                CMND: %s
                                Họ và tên: %s
                                Địa chỉ: %s
                                Phái: %s
                                Ngày cấp: %s
                                Số điện thoại: %s\s""",
                        khachHang.getId(),
                        khachHang.getHoTen(),
                        khachHang.getDiaChi(),
                        khachHang.getPhai(),
                        khachHang.getNgayCap().toString(),
                        khachHang.getSoDT()));
                if (!check) {
                    stack.push(handler);
                    return;
                }
                ;
                new Thread(() -> {
                    load();
                    Map<String, String> result = khachHangRepository.undelete(
                            khachHang.getId(),
                            khachHang.getHo(),
                            khachHang.getTen(),
                            khachHang.getDiaChi(),
                            khachHang.getPhai(),
                            khachHang.getNgayCap(),
                            khachHang.getSoDT());
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
        filteredKhachHang(obList, tfSearch, tbKhachHang);
    }

    @Override
    void unFiltered() {
        tbKhachHang.setItems(obList);
        tbKhachHang.getSelectionModel().clearSelection();
    }

    @Override
    void initValidation() {
        Validation.valideCMND(tfCMND);
        Validation.valideHo(tfHo);
        Validation.valideTen(tfTen);
        Validation.valideDiaChi(tfDiaChi);
        Validation.valideSoDT(tfSoDienThoai);
        dpNgay.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue.isAfter(LocalDate.now())) {
                FXAlerts.warning("Lựa chọn ngày không phù hợp. " +
                        "Mặc định là ngày giờ hiện tại");
                dpNgay.setValue(LocalDate.now());
            }
        });
    }

    @Override
    void initTableView() {
        list2Table(tc1ChiNhanh, tc1DiaChi, tc1Ho, tc1NgayCap, tc1Phai, tc1SoDienThoai, tc1Ten, tc1CMND);
        tbKhachHang.setItems(null);
        tbKhachHang.setItems(obList);
        filtered();
    }

    @Override
    void initCBChiNhanhEvent() {
        cbChiNhanh.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            new Thread(() -> { // Lambda Expression
                pnCN.setDisable(true);
                List<KhachHang> lst;
                System.out.println(newValue);
                System.out.println(JavaFXApplication.phanManh);
                try {
                    if (!Objects.equals(newValue.getTencn(), JavaFXApplication.phanManh)) {
                        lst = khachHangRepository.findRemoteAll();
                        obList = FXCollections.observableArrayList(lst);
                    } else {
                        lst = khachHangRepository.findAll();
                        obList = FXCollections.observableArrayList(lst);
                    }
                    tbKhachHang.setItems(obList);
                } catch (Exception e) {
                    FXAlerts.error("Có lỗi bất thường");
                }
                pnCN.setDisable(false);
            }).start();
        });
    }
}
