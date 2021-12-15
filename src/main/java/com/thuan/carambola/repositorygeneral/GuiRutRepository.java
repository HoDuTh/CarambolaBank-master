package com.thuan.carambola.repositorygeneral;

import com.thuan.carambola.entitygeneral.GDChuyenTien;
import com.thuan.carambola.entitygeneral.GDGoiRut;
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
public interface GuiRutRepository extends JpaRepository<GDGoiRut, String> {
    @Query(value = "SELECT * FROM view_remote_GD_GoiRut  ", nativeQuery = true)
    List<GDGoiRut> findRemoteAll();
    /**
     * @param soTK (String) số tài khoản để gửi tiền
     * @param soTien (BigInteger) số tiền chuyển khoản
     * @param ngayGD (Instant) ngày thực hiện giao dịch
     * @param loaiGD (String) Loại giao dịch
     * @param maNV (String) mã nhân viên thực hiện giao dịch
     **/
    @Transactional
    @Procedure("SP_GD_GUIRUT")
    Map<String, String> send(@Param("SOTK") String soTK,
                             @Param("SOTIEN") BigInteger soTien,
                             @Param("NGAYGD") Instant ngayGD,
                             @Param("LOAIGD") String loaiGD,
                             @Param("MANV") String maNV);
}
