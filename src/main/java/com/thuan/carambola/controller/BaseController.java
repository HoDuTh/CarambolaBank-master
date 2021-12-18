package com.thuan.carambola.controller;

import com.thuan.carambola.JavaFXApplication;
import com.thuan.carambola.StageInitializer;
import com.thuan.carambola.Service.DateTimeService;
import com.thuan.carambola.component.FXAlerts;
import com.thuan.carambola.entityprimary.VDsPhanmanhEntity;
import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import javafx.application.Platform;
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
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public abstract class BaseController extends MenuController implements Initializable {

    @FXML FlowPane pnInput;
    @FXML FlowPane pnCN;
    @FXML FlowPane pnSSS;

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
    @FXML Label lbLoading;
    //---------------LabelNhanVien--------------

    @FXML DatePicker dpNgay;

    @FXML private Slider sliderHour;
    @FXML private Slider sliderMinute;

    @FXML TextField tfHour;
    @FXML TextField tfMinute;
    @FXML ToolBar tbButton;
    @FXML ComboBox<VDsPhanmanhEntity> cbChiNhanh;

    PhanManhRepository phanManhRepository;
    Logger log = LoggerFactory.getLogger(TaiKhoanController.class);
    final long reloadTimer = 300000;

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
        intitButton(); // Cài chức năng của các button
        intitPanel(); // Cài các chức năng của panel vd panelSS để clear table selection
        initMenu(); // Cài đặt điều hướng menu
        initNV(); // Cài đặt thông tin nhân viên đang sử dụng chương trình
        if(Objects.equals(JavaFXApplication.nhom, "ChiNhanh")) {
            initTableEvent();
        } // Chỉ có quyền chi nhánh mới khởi tạo các event table
        initValidation();
    }
    void load()
    {
        Platform.runLater(() -> {
            menu.setDisable(true);
            tbButton.setDisable(true);
            lbLoading.setText("Loading...");
        });
    }
    void showResult(String isSuccess, String msg)
    {
        Platform.runLater(() -> {
            if(isSuccess.equals("1")) {
                FXAlerts.info(msg);
            }
            else {
                FXAlerts.error(msg);
            }
        });
    }
    void unload()
    {
        Platform.runLater(() -> {
            menu.setDisable(false);
            tbButton.setDisable(false);
            lbLoading.setText("");
        });
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
        return Instant.now();
    }
    void initTimePicker(){
        sliderHour.setMax(23);
        sliderMinute.setMax(59);
        dpNgay.setValue(LocalDate.now());
        tfHour.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    tfHour.setText(newValue.replaceAll("[^\\d]", ""));
                }
                else if(newValue.isBlank())
                {
                    tfHour.setText(String.valueOf(LocalTime.now().getHour()));
                    tfMinute.setText(String.valueOf(LocalTime.now().getMinute()));
                }
                else if(Integer.parseInt(newValue) > 23)
                    tfHour.setText("0");
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
                    tfMinute.setText("0");
                else if(dpNgay.getValue().isEqual(LocalDate.now())
                        && DateTimeService.isAfterTime(tfHour.getText(), tfMinute.getText()))
                {
                    tfHour.setText(String.valueOf(LocalTime.now().getHour()));
                    tfMinute.setText(String.valueOf(LocalTime.now().getMinute()));
                }
            }
        });
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
        sliderHour.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(dpNgay.getValue().isEqual(LocalDate.now())
                        && newValue.intValue() > LocalTime.now().getHour())
                {
                    tfHour.setText(String.valueOf(LocalTime.now().getHour()));
                }
            }
        });
        sliderMinute.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(dpNgay.getValue().isEqual(LocalDate.now())
                        && Integer.parseInt(tfHour.getText()) >= LocalTime.now().getHour()
                        && newValue.intValue() > LocalTime.now().getMinute())
                {
                    tfMinute.setText(String.valueOf(LocalTime.now().getMinute()));
                }
            }
        });

        dpNgay.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(newValue.isAfter(LocalDate.now()))
            {
                FXAlerts.warning("Lựa chọn ngày không phù hợp. " +
                        "Mặc định là ngày giờ hiện tại");
                dpNgay.setValue(LocalDate.now());
                sliderHour.setValue(LocalTime.now().getHour());
                sliderMinute.setValue(LocalTime.now().getMinute());
            }
            else if(dpNgay.getValue().isEqual(LocalDate.now())
                    && DateTimeService.isAfterTime(tfHour.getText(), tfMinute.getText()))
            {
                tfHour.setText(String.valueOf(LocalTime.now().getHour()));
                tfMinute.setText(String.valueOf(LocalTime.now().getMinute()));
            }
        });
        tfHour.setText(String.valueOf(LocalTime.now().getHour()));
        tfMinute.setText(String.valueOf(LocalTime.now().getMinute()));
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
