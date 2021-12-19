package com.thuan.carambola.entityprimary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "V_DS_PHANMANH", schema = "dbo", catalog = "NGANHANG")
public class VDsPhanmanhEntity {
    @Id
    @Column(name = "TENCN", nullable = true, length = 255)
    private String tencn;
    @Basic
    @Column(name = "TENSERVER", nullable = true)
    private String tenserver;

    public String toString() {
        return tencn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VDsPhanmanhEntity phanmanh = (VDsPhanmanhEntity) o;
        return tencn != null && Objects.equals(tencn, phanmanh.tencn);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
