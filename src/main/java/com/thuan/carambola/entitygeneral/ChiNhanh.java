package com.thuan.carambola.entitygeneral;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Table(name = "CHINHANH", indexes = {
        @Index(name = "MSmerge_index_581577110", columnList = "rowguid", unique = true),
        @Index(name = "UK_ChiNhanh", columnList = "TENCN", unique = true)
})
@Entity
public class ChiNhanh {
    @Id
    @Column(name = "MACN", nullable = false, length = 10)
    private String id;

    @Column(name = "TENCN", nullable = false, length = 100)
    private String tenCN;

    @Column(name = "DIACHI", nullable = false, length = 100)
    private String diaChi;

    @Column(name = "SoDT", nullable = false, length = 15)
    private String soDT;

    @Column(name = "rowguid", nullable = false)
    private UUID rowguid;

    public ChiNhanh() {
        this.id = "";
        this.tenCN = "";
        this.diaChi = "";
        this.soDT = "";
    }

    @Override
    public String toString() {
        return tenCN;
    }

    public StringProperty getMaCNProperty() {
        return new SimpleStringProperty(this.getId());
    }

    public StringProperty getTenCNProperty() {
        return new SimpleStringProperty(this.getTenCN());
    }

    public StringProperty getDiaChiProperty() {
        return new SimpleStringProperty(this.getDiaChi());
    }

    public StringProperty getSoDTProperty() {
        return new SimpleStringProperty(this.getSoDT());
    }
}