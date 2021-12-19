package com.thuan.carambola.repositorygeneral;

import com.thuan.carambola.entitygeneral.ReportGD;
import com.thuan.carambola.entitygeneral.ReportTK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReportTKRepository extends JpaRepository<ReportTK, String> {
    @Transactional
    @Procedure("SP_REPORT_GD")
    List<ReportGD> get(@Param("SOTK") String soTKChuyenTien,
                       @Param("TO") String soTKNhanTien);
}
