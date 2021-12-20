package com.thuan.carambola.entitygeneral;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
public class ReportTK {
    @Id
    @Column(name = "SOTK", nullable = false)
    public String soTK;

    @Column(name = "CMND", nullable = false)
    public String cmnd;

    @Column(name = "NGAYMOTK", nullable = false)
    public LocalDateTime ngayMo;

    @Column(name = "MACN", nullable = false)
    public String maCN;
}
