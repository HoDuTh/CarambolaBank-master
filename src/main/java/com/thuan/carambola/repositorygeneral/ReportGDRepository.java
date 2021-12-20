package com.thuan.carambola.repositorygeneral;

import com.thuan.carambola.entitygeneral.ReportGD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportGDRepository extends JpaRepository<ReportGD, Instant> {
    @Transactional("general2TransactionManager")
    @Query(value = "SP_REPORT_GD :SOTK, :TO", nativeQuery = true)
    List<ReportGD> get(@Param("SOTK") String soTKChuyenTien,
                       @Param("TO") LocalDate soTKNhanTien);

    @Query(value = "SP_REPORT_GD_REMOTE :SOTK, :TO", nativeQuery = true)
    List<ReportGD> getRemote(@Param("SOTK") String soTKChuyenTien,
                       @Param("TO") LocalDate soTKNhanTien);
}
