package com.thuan.carambola.entitygeneral;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;

@Entity
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "ReportTK.get", procedureName = "SP_REPORT_GD", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "FROM", type = Instant.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "TO", type = Instant.class)
        })
})
public class ReportTK {
        @Id
        @Column(name = "SOTK", nullable = false)
        private String soTK;

        @Column(name = "CMND", nullable = false)
        private String cmnd;

        @Column(name = "NGAYMOTK", nullable = false)
        private Instant ngayMoTK;

        @Column(name = "MACN", nullable = false)
        private String maCN;
}
