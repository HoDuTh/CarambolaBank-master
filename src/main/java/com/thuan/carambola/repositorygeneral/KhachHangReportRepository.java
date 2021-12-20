package com.thuan.carambola.repositorygeneral;

import com.thuan.carambola.entitygeneral.ReportKH;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Lazy
public interface KhachHangReportRepository extends JpaRepository<ReportKH, String> {
    @Query(value = "EXECUTE SP_REPORT_DSKHACHHANG", nativeQuery = true)
    List<ReportKH> report();

    @Query(value = "EXECUTE SP_REPORT_DSKHACHHANG_ONSITE ", nativeQuery = true)
    List<ReportKH> reportOnsite();

    @Query(value = "EXECUTE SP_REPORT_DSKHACHHANG_REMOTE ", nativeQuery = true)
    List<ReportKH> reportRemote();
}
