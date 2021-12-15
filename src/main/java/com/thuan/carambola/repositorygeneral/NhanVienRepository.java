package com.thuan.carambola.repositorygeneral;

import com.thuan.carambola.entitygeneral.KhachHang;
import com.thuan.carambola.entitygeneral.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
    @Query(value = "SELECT * FROM view_remote_NHANVIEN  ", nativeQuery = true)
    List<NhanVien> findRemoteAll();

    @Transactional
    @Procedure("SP_TAO_NHANVIEN")
    Map<String, String> add(@Param("MANV") String maNV,
                            @Param("HO") String ho,
                            @Param("TEN") String ten,
                            @Param("DIACHI") String diaChi,
                            @Param("PHAI") String phai,
                            @Param("SODT") String soDT,
                            @Param("TRANGTHAIXOA") String ttx);

    @Transactional
    @Procedure("SP_CAPNHAT_THONGTIN_NHANVIEN") // Chưa check
    Map<String, String> edit(@Param("MANV") String maNV,
               @Param("HO") String ho,
               @Param("TEN") String ten,
               @Param("DIACHI") String diaChi,
               @Param("PHAI") String phai,
               @Param("SODT") String soDT);

    @Transactional
    @Procedure("SP_XOA_NHANVIEN") // Chưa check
    Map<String, String> delete(@Param("MANV") String maNV);
}
