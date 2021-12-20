package com.thuan.carambola.entitygeneral;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Entity
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "ReportGD.get", procedureName = "SP_REPORT_GD", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "SOTK", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "TO", type = Instant.class)
        }),
        @NamedStoredProcedureQuery(name = "ReportGD.getRemote", procedureName = "SP_REPORT_GD_REMOTE", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "SOTK", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "TO", type = Instant.class)
        })
})
public class ReportGD {
    @Id
    @Column(name = "NGAYGD", nullable = false)
    public LocalDateTime ngay;

    @Column(name = "SOTIEN", nullable = false)
    public BigInteger soTien;

    @Column(name = "LOAIGD", nullable = false)
    public String loaiGD;
}
