package com.thuan.carambola.entitygeneral;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "ThongTinDangNhap.getMaCN", procedureName = "SP_MACN", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "MACN", type = String.class)
        }),
})
@Entity
public class ThongTinDangNhap {
    @Id
    String USERNAME;
    String HOTEN;
    String TENNHOM;
}
