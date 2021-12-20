package com.thuan.carambola.repositorygeneral;

import com.thuan.carambola.entitygeneral.ThongTinDangNhap;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;
import java.util.List;
import java.util.Map;

@Repository
public interface ThongTinDangNhapRepository extends JpaRepository<ThongTinDangNhap, String> {
    @Query(value = "EXECUTE SP_DANGNHAP :TENLOGIN ", nativeQuery = true)
    List<ThongTinDangNhap> get(@Param("TENLOGIN") String tenLogin);

}
