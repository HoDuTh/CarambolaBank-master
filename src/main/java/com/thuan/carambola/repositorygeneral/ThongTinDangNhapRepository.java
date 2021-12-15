package com.thuan.carambola.repositorygeneral;

import com.thuan.carambola.entitygeneral.ThongTinDangNhap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ThongTinDangNhapRepository extends JpaRepository<ThongTinDangNhap, String> {
    @Query(value = "EXECUTE SP_DANGNHAP :TENLOGIN ", nativeQuery = true)
    List<ThongTinDangNhap> get(@Param("TENLOGIN") String tenLogin);
}
