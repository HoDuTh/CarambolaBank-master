package com.thuan.carambola.repositorygeneral;

import com.thuan.carambola.entitygeneral.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {
    @Query(value = "SELECT * FROM view_remote_KHACHHANG  ", nativeQuery = true)
    List<TaiKhoan> findRemoteAll();

    @Transactional
    @Procedure("SP_TAO_TAIKHOAN")
    Map<String, String> add(@Param("CMND") String cmnd,
                            @Param("SOTK") String soTK,
                            @Param("NGAYMOTK") Instant ngayMoTK);

    @Transactional
    @Procedure("SP_CAPNHAT_THONGTIN_TAIKHOAN")
        // chưa check
    Map<String, String> edit(@Param("CMND") String cmnd,
                             @Param("SOTK") String soTK,
                             @Param("NGAYMOTK") Instant ngayMoTK);

    @Transactional
    @Procedure("SP_XOA_TAIKHOAN")
        // chưa check
    Map<String, String> delete(@Param("SOTK") String soTK);

}
