package com.thuan.carambola.entitygeneral;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "NHANVIEN", indexes = {
        @Index(name = "MSmerge_index_62623266", columnList = "rowguid", unique = true)
})
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "NhanVien.add", procedureName = "SP_TAO_NHANVIEN", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "MANV", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "HO", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "TEN", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "DIACHI", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "PHAI", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "SODT", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "TRANGTHAIXOA", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ISSUCCESS", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "MSG", type = String.class)
        }),
        @NamedStoredProcedureQuery(name = "NhanVien.edit", procedureName = "SP_CAPNHAT_THONGTIN_NHANVIEN", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "MANV", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "HO", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "TEN", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "DIACHI", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "PHAI", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "SODT", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ISSUCCESS", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "MSG", type = String.class)
        }),
        @NamedStoredProcedureQuery(name = "NhanVien.delete", procedureName = "SP_XOA_NHANVIEN", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "MANV", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ISSUCCESS", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "MSG", type = String.class)
        })// ISERROR = 1 là lỗi, 2 là đánh dấu xóa, 0 là xóa thành công
})
@Cacheable(false)
public class NhanVien {
    @Id
    @Column(name = "MANV", nullable = false, length = 10)
    private String id;

    @Column(name = "HO", nullable = false, length = 40)
    private String ho;

    @Column(name = "TEN", nullable = false, length = 10)
    private String ten;

    @Column(name = "DIACHI", nullable = false, length = 100)
    private String diaChi;

    @Column(name = "PHAI", length = 3)
    private String phai;

    @Column(name = "SODT", length = 15)
    private String soDT;

    @Column(name = "MACN")
    private String maCN;

    @Column(name = "TRANGTHAIXOA")
    private Integer trangThaiXoa;

    @Column(name = "rowguid", nullable = false)
    private UUID rowguid;

    public NhanVien() {
        this.id = "";
        this.ho = "";
        this.ten = "";
        this.diaChi = "";
        this.phai = "";
        this.soDT = "";
        this.maCN = "";
    }

    public String getTrangThaiXoa() {
        return trangThaiXoa == 0 ? "Hoạt Động" : "Đã nghỉ";
    }

    public String getHoTen() {
        return (this.getHo() + " " + this.getTen()).replaceAll("\\s\\s+", " ").trim();
    }

    public StringProperty getMaNVProperty() {
        return new SimpleStringProperty(this.getId());
    }

    public StringProperty getHoProperty() {
        return new SimpleStringProperty(this.getHo());
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

    public StringProperty getTTXProperty() {
        return new SimpleStringProperty(String.valueOf(this.getTrangThaiXoa()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        NhanVien nhanVien = (NhanVien) o;
        return id != null && Objects.equals(id, nhanVien.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}