package com.thuan.carambola.entitygeneral;

import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

@Data
@Entity
public class ThongTinDangNhap {
    @Id
    String USERNAME;
    String HOTEN;
    String TENNHOM;
}
