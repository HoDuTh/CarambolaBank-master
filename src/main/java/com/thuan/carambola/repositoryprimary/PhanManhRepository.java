package com.thuan.carambola.repositoryprimary;

import com.thuan.carambola.entityprimary.VDsPhanmanhEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhanManhRepository extends JpaRepository<VDsPhanmanhEntity, String> {
}
