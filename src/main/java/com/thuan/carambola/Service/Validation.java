package com.thuan.carambola.Service;

import com.thuan.carambola.component.FXAlerts;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import java.math.BigInteger;
import java.text.DecimalFormat;

public class Validation {
    public static void valideSoTK(TextField soTK) {
        valideNumberField(soTK);
        valideTextFieldLength(soTK, 9);
    }

    public static void valideCMND(TextField soTK) {
        valideNumberField(soTK);
        valideTextFieldLength(soTK, 10);
    }

    public static void valideHo(TextField ho) {
        valideTextFieldLength(ho, 50);
    }

    public static void valideTen(TextField ten) {
        valideTextFieldLength(ten, 10);
    }

    public static void valideDiaChi(TextField diaChi) {
        valideTextFieldLength(diaChi, 100);
    }

    public static void valideSoDT(TextField soDT) {
        valideNumberField(soDT);
        valideTextFieldLength(soDT, 15);
    }

    static void valideMaNV(TextField soTK) {
        valideTextFieldLength(soTK, 10);
    }

    static void valideNumberField(TextField tfOnlyNumber) {
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

    static void valideCurrencyField(TextField tfOnlyMoney) {
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

    public static void valideSoTien(TextField soTien, BigInteger max, BigInteger min) {
        valideNumberField(soTien);
        soTien.focusedProperty().addListener(new ChangeListener<Boolean>() // giá trị nhỏ nhất chỉ báo lỗi khi người dùng thoát ra khỏi textfield
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    if (!soTien.getText().isBlank() && new BigInteger(soTien.getText()).compareTo(min) < 0) {
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
                if (soTien.getText().isBlank()) return;
                BigInteger soTienValue = new BigInteger(soTien.getText());
                if (soTienValue.compareTo(max) > 0) {
                    FXAlerts.warning(String.format("Giá trị giao dịch lớn nhất là %d ", max));
                    soTien.setText(oldValue);
                } else if (soTienValue.compareTo(min) > 0 && soTienValue.compareTo(max) < 0)
                    soTien.setText(soTienValue.toString());
            }
        });
    }

    static void valideTextFieldLength(TextField textField, int length) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (textField.getLength() > length)
                    textField.setText(oldValue);
            }
        });
    }
}
