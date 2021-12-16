package com.thuan.carambola.repositorygeneral;

import com.thuan.carambola.entitygeneral.GDChuyenTien;
import com.thuan.carambola.entitygeneral.KhachHang;
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
public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    @Query(value = "SELECT * FROM view_remote_KHACHHANG  ", nativeQuery = true)
    List<KhachHang> findRemoteAll();
//    @Query(value = "EXEC SP_TAO_KHACHHANG :CMND, :HO ,:TEN ,:DIACHI ,:PHAI ,:NGAYCAP ,:SODT, :ERROR", nativeQuery = true)

    @Transactional
    @Procedure("SP_TAO_KHACHHANG")
    Map<String, String> add(@Param("CMND") String cmnd,
               @Param("HO") String ho,
               @Param("TEN") String ten,
               @Param("DIACHI") String diaChi,
               @Param("PHAI") String phai,
               @Param("NGAYCAP") Instant ngayCap,
               @Param("SODT") String soDT);

    @Transactional
    @Procedure("SP_CAPNHAT_THONGTIN_KHACHANG") // Chưa check
    Map<String, String> edit(@Param("CMND") String cmnd,
                             @Param("HO") String ho,
                             @Param("TEN") String ten,
                             @Param("DIACHI") String diaChi,
                             @Param("PHAI") String phai,
                             @Param("NGAYCAP") Instant ngayCap,
                             @Param("SODT") String soDT);

    @Transactional
    @Procedure("SP_XOA_KHACHHANG") // Chưa check
    Map<String, String> delete(@Param("CMND") String cmnd);

}
