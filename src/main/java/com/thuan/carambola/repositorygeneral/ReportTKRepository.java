package com.thuan.carambola.repositorygeneral;

import com.thuan.carambola.entitygeneral.ReportTK;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Lazy
@Repository
public interface ReportTKRepository extends JpaRepository<ReportTK, String> {

    @Query(value = " SP_REPORT_TK :FROM, :TO", nativeQuery = true)
    List<ReportTK> get(@Param("FROM") LocalDate from,
                       @Param("TO") LocalDate to);

    @Query(value = " SP_REPORT_TK_ONSITE :FROM, :TO  ", nativeQuery = true)
    List<ReportTK> getOnsite(@Param("FROM") LocalDate from,
                       @Param("TO") LocalDate to);

    @Query(value = " SP_REPORT_TK_REMOTE :FROM, :TO", nativeQuery = true)
    List<ReportTK> getRemote(@Param("FROM") LocalDate from,
                       @Param("TO") LocalDate to);
}
