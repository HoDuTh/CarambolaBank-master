package com.thuan.carambola.repositorygeneral;

import com.thuan.carambola.entitygeneral.GDChuyenTien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
public interface ChuyenTienRepository extends JpaRepository<GDChuyenTien, String> {
    @Query(value = "SELECT * FROM view_remote_GD_CHUYENTIEN  ", nativeQuery = true)
    List<GDChuyenTien> findRemoteAll();

    /**
     * @param soTKChuyenTien (String) số tài khoản để gửi tiền
     * @param soTKNhanTien (String) số tài khoản để nhận tiền
     * @param soTien (BigInteger) số tiền chuyển khoản
     * @param ngayGD (Instant) ngày thực hiện giao dịch
     * @param maNV (String) mã nhân viên thực hiện giao dịch
     **/
    @Transactional
    @Procedure("SP_ChuyenTien")
    Map<String, String> send(@Param("SOTK_CHUYEN") String soTKChuyenTien,
                             @Param("SOTK_NHAN") String soTKNhanTien,
                             @Param("SOTIEN") BigInteger soTien,
                             @Param("NGAYGD") Instant ngayGD,
                             @Param("MANV") String maNV);
}
