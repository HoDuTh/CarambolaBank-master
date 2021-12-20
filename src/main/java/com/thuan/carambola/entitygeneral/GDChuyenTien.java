package com.thuan.carambola.entitygeneral;

import com.thuan.carambola.setting.PatternSetting;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Data
@Table(name = "GD_CHUYENTIEN", indexes = {
        @Index(name = "MSmerge_index_693577509", columnList = "rowguid", unique = true)
})
@Entity
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "GDChuyenTien.send", procedureName = "SP_ChuyenTien", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "SOTK_CHUYEN", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "SOTK_NHAN", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "SOTIEN", type = BigInteger.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NGAYGD", type = LocalDateTime.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "MANV", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ISSUCCESS", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "MSG", type = String.class)
        }),
})
public class GDChuyenTien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MAGD", nullable = false)
    private Integer id;

    @Column(name = "SOTK_CHUYEN", nullable = false)
    private String soTKChuyen;

    @Column(name = "NGAYGD", nullable = false)
    private LocalDateTime ngayGD;

    @Column(name = "SOTIEN", nullable = false)
    private BigInteger soTien;

    @Column(name = "SOTK_NHAN", nullable = false)
    private String soTKNhan;

    @Column(name = "maNV", nullable = false)
    private String maNV;

    @Transient
    private String hoTenNV;

    @Column(name = "rowguid", nullable = false)
    private UUID rowguid;

    public GDChuyenTien() {
        this.soTKChuyen = "";
        this.soTKNhan = "";
        this.maNV = "";
        this.hoTenNV = "";
    }

    public String getSoTien() {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        return currencyVN.format(soTien);
    }

    public StringProperty getSotienProperty() {
        return new SimpleStringProperty(this.getSoTien());
    }

    public StringProperty getMaGDProperty() {
        return new SimpleStringProperty(String.valueOf(this.getId()));
    }

    public StringProperty getNgayGDProperty() {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(PatternSetting.date)
                .withLocale(new Locale("vi", "VN"))
                .withZone(ZoneId.of("UTC"));
        return new SimpleStringProperty(formatter.format(this.getNgayGD()));
    }
}