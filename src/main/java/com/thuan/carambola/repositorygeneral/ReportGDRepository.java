package com.thuan.carambola.repositorygeneral;

import com.thuan.carambola.entitygeneral.GDChuyenTien;
import com.thuan.carambola.entitygeneral.ReportGD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
public interface ReportGDRepository extends JpaRepository<ReportGD, Instant> {
    @Transactional
    @Procedure("SP_REPORT_GD")
    List<ReportGD> get(@Param("SOTK") String soTKChuyenTien,
                        @Param("TO") String soTKNhanTien);
}
