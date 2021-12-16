package com.thuan.carambola.component;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class FXAlerts {
    public static void warning(String massage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(massage);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
    public static void error(String massage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(massage);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
    public static void info(String massage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(massage);
        alert.showAndWait();
    }
    public static boolean confirm(String massage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(massage);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> option =  alert.showAndWait();
        return option.isPresent() && option.get() == ButtonType.OK;
    }
    public static String confirm(String massage, List<String> btnTitles) {
        Locale.setDefault(new Locale("vi", "VN"));
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(massage);
        alert.getButtonTypes().clear();
        for(String a: btnTitles)
        {
            alert.getButtonTypes().add(new ButtonType(a));
        }
        alert.getButtonTypes().add(new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE));
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> option =  alert.showAndWait();
        return option.get().getText();
    }
}
