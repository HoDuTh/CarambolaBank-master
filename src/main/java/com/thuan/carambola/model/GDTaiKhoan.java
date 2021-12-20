package com.thuan.carambola.model;

import com.thuan.carambola.entitygeneral.ReportGD;
import lombok.Data;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class GDTaiKhoan {
    private BigInteger soDuDau;
    private LocalDateTime ngayGD;
    private BigInteger SoDuSau;
    private String loaiGD;
    private BigInteger soTien;
    public GDTaiKhoan(ReportGD gd)
    {
        this.ngayGD = gd.ngay;
        switch (gd.loaiGD) {
            case "CT" -> this.loaiGD = "Chuyển tiền";
            case "NT" -> this.loaiGD = "Nhận tiền";
            case "GT" -> this.loaiGD = "Gửi tiền";
            case "RT" -> this.loaiGD = "Rút tiền";
            default -> this.loaiGD = "Lỗi";
        }
        this.soTien = gd.soTien;
    }

}
