package com.thuan.carambola.entitygeneral;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "ReportKH.reportOnsite", procedureName = "SP_REPORT_DSKHACHHANG_ONSITE"),
        @NamedStoredProcedureQuery(name = "ReportKH.reportRemote", procedureName = "SP_REPORT_DSKHACHHANG_REMOTE")
})
@Table(name = "KHACHHANG")
@Entity
public class ReportKH {
    @Id
    @Column(name = "CMND", nullable = false, length = 10)
    private String id;

    @Column(name = "HO", nullable = false, length = 50)
    private String ho;

    @Column(name = "TEN", nullable = false, length = 10)
    private String ten;

    @Column(name = "DIACHI", nullable = false, length = 100)
    private String diaChi;

    @Column(name = "NGAYCAP", nullable = false)
    private Instant ngayCap;

    @Column(name = "MACN")
    private String maCN;

    public ReportKH(KhachHang khachHang) {
        this.id = khachHang.getId();
        this.ho = khachHang.getHo();
        this.ten = khachHang.getTen();
        this.diaChi = khachHang.getDiaChi();
        this.ngayCap = khachHang.getNgayCap();
        this.maCN = khachHang.getMaCN();
    }

    public ReportKH() {

    }
}