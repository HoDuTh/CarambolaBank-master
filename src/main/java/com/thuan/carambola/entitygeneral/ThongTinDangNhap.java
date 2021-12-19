package com.thuan.carambola.entitygeneral;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class ThongTinDangNhap {
    @Id
    String USERNAME;
    String HOTEN;
    String TENNHOM;
}
