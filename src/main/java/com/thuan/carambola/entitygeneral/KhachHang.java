package com.thuan.carambola.entitygeneral;

import com.thuan.carambola.setting.PatternSetting;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Table(name = "KHACHHANG", indexes = {
        @Index(name = "MSmerge_index_302624121", columnList = "rowguid", unique = true)
})
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "KhachHang.add", procedureName = "SP_TAO_KHACHHANG", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CMND", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "HO", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "TEN", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "DIACHI", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "PHAI", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NGAYCAP", type = Instant.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "SODT", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ISSUCCESS", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "MSG", type = String.class)
        }),
        @NamedStoredProcedureQuery(name = "KhachHang.edit", procedureName = "SP_CAPNHAT_THONGTIN_KHACHHANG", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CMND", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "HO", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "TEN", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "DIACHI", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "PHAI", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NGAYCAP", type = Instant.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "SODT", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ISSUCCESS", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "MSG", type = String.class)
        }),
        @NamedStoredProcedureQuery(name = "KhachHang.delete", procedureName = "SP_XOA_KHACHHANG", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CMND", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ISSUCCESS", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "MSG", type = String.class)
        })
})


@Entity
public class KhachHang {
    @Id
    @Column(name = "CMND", nullable = false, length = 10)
    private String id;

    @Column(name = "HO", nullable = false, length = 50)
    private String ho;

    @Column(name = "TEN", nullable = false, length = 10)
    private String ten;

    @Column(name = "DIACHI", nullable = false, length = 100)
    private String diaChi;

    @Column(name = "PHAI", length = 3)
    private String phai;

    @Column(name = "NGAYCAP", nullable = false)
    private Instant ngayCap;

    @Column(name = "SODT", length = 15)
    private String soDT;

    @Column(name = "MACN")
    private String maCN;

    @Column(name = "rowguid")
    private UUID rowguid;

    public KhachHang() {
        this.id = "";
        this.ho = "";
        this.ten = "";
        this.diaChi = "";
        this.phai = "";
        this.soDT = "";
        this.maCN = "";
    }

    public KhachHang(String id,
                     String ho,
                     String ten,
                     String diaChi,
                     String phai,
                     Instant ngayCap,
                     String soDT,
                     String maCN) {
        this.id = id;
        this.ho = ho;
        this.ten = ten;
        this.diaChi = diaChi;
        this.phai = phai;
        this.ngayCap = ngayCap;
        this.soDT = soDT;
        this.maCN = maCN;
    }

    public String getHoTen() {
        return (this.getHo() + " " + this.getTen()).replaceAll("\\s\\s+", " ").trim() ;
    }
    public StringProperty getCMNDProperty() {return new SimpleStringProperty(this.getId());}
    public StringProperty getHoProperty() {
        return new SimpleStringProperty(this.getHo());
    }
    public StringProperty getMaCNProperty() {
        return new SimpleStringProperty(this.getMaCN());
    }
    public StringProperty getTenProperty() {
        return new SimpleStringProperty(this.getTen());
    }
    public StringProperty getDiaChiProperty() {
        return new SimpleStringProperty(this.getDiaChi());
    }
    public StringProperty getPhaiProperty() {
        return new SimpleStringProperty(this.getPhai());
    }
    public StringProperty getSoDTProperty() {
        return new SimpleStringProperty(this.getSoDT());
    }
    public StringProperty getHoTenProperty() {
        return new SimpleStringProperty(this.getHoTen());
    }
    public StringProperty getNgayCapProperty() {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(PatternSetting.date)
                .withLocale(new Locale("vi", "VN"))
                .withZone(ZoneId.of("UTC"));
        return new SimpleStringProperty(formatter.format(this.getNgayCap()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        KhachHang khachHang = (KhachHang) o;
        return id != null && Objects.equals(id, khachHang.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}