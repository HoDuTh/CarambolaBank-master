package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.Service.DateTimeService;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entityprimary.VDsPhanmanhEntity;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Stack;

@Component
public abstract class BaseController implements Initializable {

    @FXML FlowPane pnInput;
    @FXML FlowPane pnCN;
    @FXML FlowPane pnSSS;

    //-------------------Menu-------------------
    @FXML Menu menuGiaoDich;
    @FXML Menu menuGioiThieu;
    @FXML Menu menuHeThong;
    @FXML Menu menuHuongDan;
    @FXML Menu menuKhachHang;
    @FXML Menu menuNhanVien;
    @FXML Menu menuTaiKhoan;
    @FXML MenuItem menuItemChuyenNhanVien;
    @FXML MenuItem menuItemChuyenTien;
    @FXML MenuItem menuItemGiaoDich;
    @FXML MenuItem menuItemGuiRut;
    @FXML MenuItem menuItemHeThong;
    @FXML MenuItem menuItemKhachHang;
    @FXML MenuItem menuItemTaiKhoan;
    @FXML MenuItem menuItemNhanVien;
    //-------------------Menu-------------------

    //------------------Button------------------
    @FXML Button btnGhi;
    @FXML Button btnHoanTac;
    @FXML Button btnSua;
    @FXML Button btnThem;
    @FXML Button btnUpdate;
    @FXML Button btnXoa;

    //------------------Button------------------

    //---------------LabelNhanVien--------------
    @FXML Label lbMaNV;
    @FXML Label lbNhom;
    @FXML Label lbTenNV;
    //---------------LabelNhanVien--------------

    @FXML DatePicker dpNgay;

    @FXML private Slider sliderHour;
    @FXML private Slider sliderMinute;

    @FXML TextField tfHour;
    @FXML TextField tfMinute;

    @FXML ComboBox<VDsPhanmanhEntity> cbChiNhanh;
    Stack stack;
    PhanManhRepository phanManhRepository;
    Logger log = LoggerFactory.getLogger(BaseController.class);

    abstract void updateData();
    abstract void filtered();
    abstract void initTableView();
    abstract void btnThem(ActionEvent actionEvent);
    abstract void btnXoa(ActionEvent actionEvent);
    abstract void btnSua(ActionEvent actionEvent);
    abstract void btnGhi(ActionEvent actionEvent);
    abstract void btnHoanTac(ActionEvent actionEvent);
    abstract void initCBChiNhanhEvent();
    abstract void unFiltered();
    abstract void initTableEvent();
    abstract void initValidation();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPhanManh(phanManhRepository, cbChiNhanh);
        updateData();
        filtered();
        initCBChiNhanhEvent();
        initPermission();
        initTableView();
        intitButton();
        intitPanel();
        initMenu();
        initNV();
        initTableEvent();
        initValidation();
    }
    String formatCurrency(BigInteger soTien)
    {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        return currencyVN.format(soTien);
    }
    void formatSoTienToLabel(TextField tf, Label lb)
    {
        lb.textProperty().bindBidirectional(tf.textProperty(), new StringConverter<>() {
            @Override
            public String toString(String string) {
                if(tf.getText().isBlank()) return "0";
                return formatCurrency(new BigInteger(string));
            }

            @Override
            public String fromString(String string) {
                return string;
            }

        });
    }
    private void btnUpdate(ActionEvent actionEvent){
        updateData();
    }
    void clearDateTime(){
        dpNgay.getEditor().clear();
        dpNgay.setValue(null);

        tfHour.setText("0");
        tfMinute.setText("0");
    }
    Instant getDateTime(){
        int hours = Integer.parseInt(tfHour.getText());
        int  minutes = Integer.parseInt(tfMinute.getText());

        return  dpNgay.getValue()
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC)
                .plus(hours, ChronoUnit.HOURS)
                .plus(minutes, ChronoUnit.MINUTES);
    }
    void initTimePicker(){
        sliderHour.setMax(23);
        sliderMinute.setMax(59);

        LocalDateTime ldt = LocalDateTime.now();
        tfHour.setText(String.valueOf(ldt.getHour()));
        tfMinute.setText(String.valueOf(ldt.getMinute()));

        dpNgay.setValue(LocalDate.now());

        dpNgay.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(newValue.isAfter(LocalDate.now()))
            {
                FXAlerts.warning("Lựa chọn ngày không phù hợp. " +
                        "Mặc định là ngày giờ hiện tại");
                dpNgay.setValue(LocalDate.now());
                tfHour.setText(String.valueOf(LocalTime.now().getHour()));
                tfMinute.setText(String.valueOf(LocalTime.now().getMinute()));
            }});
        tfHour.textProperty().bindBidirectional(sliderHour.valueProperty(), new StringConverter<Number>()
        {
            @Override
            public String toString(Number t)
            {
                return String.valueOf(t.intValue());
            }
            @Override
            public Number fromString(String string)
            {
                return Integer.parseInt(string);
            }

        });
        tfHour.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    tfHour.setText(newValue.replaceAll("[^\\d]", ""));
                }
                else if(Integer.parseInt(newValue) > 23)
                    tfHour.setText("23");
                else if(dpNgay.getValue().isEqual(LocalDate.now())
                        && DateTimeService.isAfterTime(tfHour.getText(), tfMinute.getText()))
                {
                    tfHour.setText(String.valueOf(LocalTime.now().getHour()));
                    tfMinute.setText(String.valueOf(LocalTime.now().getMinute()));
                }
            }
        });
        tfMinute.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    tfMinute.setText(newValue.replaceAll("[^\\d]", ""));
                }
                else if(Integer.parseInt(newValue) > 59)
                    tfMinute.setText("59");
                else if(dpNgay.getValue().isEqual(LocalDate.now())
                        && DateTimeService.isAfterTime(tfHour.getText(), tfMinute.getText()))
                {
                    tfHour.setText(String.valueOf(LocalTime.now().getHour()));
                    tfMinute.setText(String.valueOf(LocalTime.now().getMinute()));
                }
            }
        });
        tfMinute.textProperty().bindBidirectional(sliderMinute.valueProperty(), new StringConverter<Number>()
        {
            @Override
            public String toString(Number t)
            {
                return String.valueOf(t.intValue());
            }
            @Override
            public Number fromString(String string)
            {
                return Integer.parseInt(string);
            }

        });
    }

    void valideSoTK(TextField soTK){
        soTK.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    soTK.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        valideTextFieldLength(soTK, 9);
    }
    void valideMaNV(TextField soTK){
        valideTextFieldLength(soTK, 10);
    }
    void valideNumberField(TextField tfOnlyNumber){
        tfOnlyNumber.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                  tfOnlyNumber.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }
    void valideCurrencyField(TextField tfOnlyMoney){
        tfOnlyMoney.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue.matches("\\d*")) {
                    DecimalFormat formatter = new DecimalFormat("#,###,###,###,###,###,###");
                    String newValueStr = formatter.format(Double.parseDouble(newValue));
                    tfOnlyMoney.setText(newValueStr);
                }
            }
        });
    }
    void valideSoTien(TextField soTien, BigInteger max,  BigInteger min){
        valideNumberField(soTien);
        soTien.focusedProperty().addListener(new ChangeListener<Boolean>() // giá trị nhỏ nhất chỉ báo lỗi khi người dùng thoát ra khỏi textfield
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (!newPropertyValue)
                {
                    if(!soTien.getText().isBlank() && new BigInteger(soTien.getText()).compareTo(min) < 0)
                    {
                        soTien.setText(min.toString());
                        FXAlerts.warning(String.format("Giá trị giao dịch nhỏ nhất là %d.\nĐặt mặc định là giá trị nhỏ nhất %d", min, min));
                    }
                }
            }
        });
        soTien.textProperty().addListener(new ChangeListener<String>() // giá trị lớn nhất báo khi người dùng nhập vào
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if(soTien.getText().isBlank()) return;
                BigInteger soTienValue = new BigInteger(soTien.getText());
                if(soTienValue.compareTo(max) > 0) {
                    FXAlerts.warning(String.format("Giá trị giao dịch lớn nhất là %d ", max));
                    soTien.setText(oldValue);
                }
                else  if(soTienValue.compareTo(min) > 0 && soTienValue.compareTo(max) < 0)
                    soTien.setText(soTienValue.toString());
            }
        });
    }
    void valideTextFieldLength(TextField textField, int length)
    {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
               if(textField.getLength() > length)
                    textField.setText(oldValue);
            }
        });
    }
    void initPhanManh(PhanManhRepository phanManhRepository, ComboBox<VDsPhanmanhEntity> cbChiNhanh){
        List<VDsPhanmanhEntity> list = phanManhRepository.findAll();
        ObservableList<VDsPhanmanhEntity> options =
                FXCollections.observableArrayList(list);
        cbChiNhanh.setItems(options);
    }
    void intitButton(){
        btnThem.setOnAction(this::btnThem);
        btnXoa.setOnAction(this::btnXoa);
        btnSua.setOnAction(this::btnSua);
        btnGhi.setOnAction(this::btnGhi);
        btnHoanTac.setOnAction(this::btnHoanTac);
        btnUpdate.setOnAction(this::btnUpdate);
    }
    void intitPanel(){
        pnSSS.setOnMousePressed(event -> {
            unFiltered();
        });
    }

    public BaseController(PhanManhRepository phanManhRepository) {
        this.phanManhRepository = phanManhRepository;
    }
    private void setMenuItemChuyenTien(ActionEvent actionEvent)
    {try {StageInitializer.setScene("chuyenTien");} catch (IOException e)
    {FXAlerts.error("Trang này đang không hoạt động");}}

    private void setMenuItemTaiKhoan(ActionEvent actionEvent)
    {try {StageInitializer.setScene(StageInitializer.taiKhoan);} catch (IOException e)
    {FXAlerts.error("Trang này đang không hoạt động");}}

    private void setMenuItemGuiRut(ActionEvent actionEvent)
    {try {StageInitializer.setScene(StageInitializer.guiRut);} catch (IOException e)
    {FXAlerts.error("Trang này đang không hoạt động");}}

    private void setMenuItemNhanVien(ActionEvent actionEvent)
    {try {StageInitializer.setScene(StageInitializer.nhanVien);} catch (IOException e)
    {FXAlerts.error("Trang này đang không hoạt động");}}

    private void setMenuItemKhachHang(ActionEvent actionEvent)
    {try {StageInitializer.setScene(StageInitializer.khachHang);} catch (IOException e)
    {FXAlerts.error("Trang này đang không hoạt động");}}

    void initMenu()
    {
        menuItemChuyenTien.setOnAction(this::setMenuItemChuyenTien);
        menuItemTaiKhoan.setOnAction(this::setMenuItemTaiKhoan);
        menuItemGuiRut.setOnAction(this::setMenuItemGuiRut);
        menuItemNhanVien.setOnAction(this::setMenuItemNhanVien);
        menuItemKhachHang.setOnAction(this::setMenuItemKhachHang);
    }
    void initNV() //cài đặt thông tin nhân viên đang sử dụng phần mềm
    {
        lbMaNV.setText(JavaFXApplication.maNV);
        lbTenNV.setText(JavaFXApplication.tenNV);
        lbNhom.setText(JavaFXApplication.nhom);
    }
    void initPermission()
    {
        String permissions = JavaFXApplication.nhom;
        switch (permissions) {
            case "NganHang" -> pnCN.setDisable(false);
            case "ChiNhanh" -> pnInput.setDisable(false);
            default -> {
            }
        }
    }

}
