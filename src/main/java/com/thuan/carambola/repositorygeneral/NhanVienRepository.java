package com.thuan.carambola.repositorygeneral;

import com.thuan.carambola.entitygeneral.NhanVien;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
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
    @Procedure("SP_CHUYEN_NHANVIEN")
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    Map<String, String> move(
            @Param("MANV") String maNV,
            @Param("MANV_MOI") String maNVMoi);

    @Transactional
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @Procedure("SP_TAO_NHANVIEN")
    Map<String, String> add(@Param("MANV") String maNV,
                            @Param("HO") String ho,
                            @Param("TEN") String ten,
                            @Param("DIACHI") String diaChi,
                            @Param("PHAI") String phai,
                            @Param("SODT") String soDT,
                            @Param("TRANGTHAIXOA") String ttx);

    @Transactional
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @Procedure("SP_CAPNHAT_THONGTIN_NHANVIEN")
    Map<String, String> edit(@Param("MANV") String maNV,
                             @Param("HO") String ho,
                             @Param("TEN") String ten,
                             @Param("DIACHI") String diaChi,
                             @Param("PHAI") String phai,
                             @Param("SODT") String soDT);

    @Transactional
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @Procedure("SP_XOA_NHANVIEN")
    Map<String, String> delete(@Param("MANV") String maNV);

    @Transactional
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @Procedure("SP_TAOLOGIN")
    String taoLogin(@Param("LGNAME") String loginname,
                               @Param("PASS") String password,
                               @Param("USERNAME") String username,
                               @Param("ROLE") String role);

    @Query(value = "SP_TAOLOGIN :LGNAME, :PASS, :USERNAME, :ROLE",nativeQuery = true)
    Integer createLogin(@Param("LGNAME") String loginname,
                 @Param("PASS") String password,
                 @Param("USERNAME") String username,
                 @Param("ROLE") String role);
}
