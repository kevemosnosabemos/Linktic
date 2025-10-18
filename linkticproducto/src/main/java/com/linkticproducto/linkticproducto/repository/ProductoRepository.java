package com.linkticproducto.linkticproducto.repository;

import com.linkticproducto.linkticproducto.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}