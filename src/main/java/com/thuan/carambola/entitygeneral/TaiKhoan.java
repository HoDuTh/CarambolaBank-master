package com.thuan.carambola.entitygeneral;


import com.thuan.carambola.setting.PatternSetting;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Data
@Table(name = "TAIKHOAN", indexes = {
        @Index(name = "MSmerge_index_1333579789", columnList = "rowguid", unique = true)
})
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "TaiKhoan.add", procedureName = "SP_TAO_TAIKHOAN", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CMND", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "SOTK", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NGAYMOTK", type = Instant.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ISSUCCESS", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "MSG", type = String.class)
        }),
        @NamedStoredProcedureQuery(name = "TaiKhoan.edit", procedureName = "SP_CAPNHAT_THONGTIN_TAIKHOAN", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CMND", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "SOTK", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NGAYMOTK", type = Instant.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ISSUCCESS", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "MSG", type = String.class)
        }),
        @NamedStoredProcedureQuery(name = "TaiKhoan.delete", procedureName = "SP_XOA_TAIKHOAN", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "SOTK", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ISSUCCESS", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "MSG", type = String.class)
        })
})
@Entity
public class TaiKhoan {
    @Id
    @Column(name = "SOTK", nullable = false, length = 9)
    private String id;

    @Column(name = "SODU", nullable = false)
    private BigInteger sodu;

    @Column(name = "CMND")
    private String cmnd;

    @Transient
    private String hoTen;
    @Transient
    private String soDT;
    @Transient
    private String hoTenNV;

    @Column(name = "MACN")
    private String maCN;

    @Column(name = "NGAYMOTK")
    private Instant ngayMoTK;

    @Column(name = "rowguid", nullable = false)
    private UUID rowguid;

    public TaiKhoan() {
        this.id = "";
        this.cmnd = "";
        this.hoTen = "";
        this.soDT = "";
        this.hoTenNV = "";
        this.maCN = "";
    }

    public TaiKhoan(String sotk, String cmnd, Instant ngay) {
        this.id = sotk;
        this.cmnd = cmnd;
        this.ngayMoTK = ngay;
        this.hoTen = "";
        this.soDT = "";
        this.hoTenNV = "";
        this.maCN = "";
    }

    public String getSoDu() {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        return currencyVN.format(sodu);
    }

    public StringProperty getNgayMoTKProperty() {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(PatternSetting.date)
                .withLocale(new Locale("vi", "VN"))
                .withZone(ZoneId.of("UTC"));
        return new SimpleStringProperty(formatter.format(this.getNgayMoTK()));
    }

    public StringProperty getSoduProperty() {

        return new SimpleStringProperty(this.getSoDu());
    }

    public StringProperty getSoTKProperty() {
        return new SimpleStringProperty(this.getId());
    }

}