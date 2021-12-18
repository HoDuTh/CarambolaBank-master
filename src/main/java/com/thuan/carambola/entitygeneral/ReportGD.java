package com.thuan.carambola.entitygeneral;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;

@Entity
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "ReportGD.get", procedureName = "SP_REPORT_GD", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "SOTK", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "TO", type = Instant.class)
        })
})
public class ReportGD {
        @Id
        @Column(name = "NGAYGD", nullable = false)
        private Instant id;

        @Column(name = "SOTIEN", nullable = false)
        private BigInteger from;

        @Column(name = "LOAIGD", nullable = false)
        private String to;
}
