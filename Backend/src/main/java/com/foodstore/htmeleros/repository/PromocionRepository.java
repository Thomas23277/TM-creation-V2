package com.foodstore.htmeleros.repository;

import com.foodstore.htmeleros.entity.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    List<Promocion> findByActivoTrueOrderByOrdenAsc();

    List<Promocion> findAllByOrderByOrdenAsc();
}