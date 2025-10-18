package com.linkticinventario.linkticinventario.repository;

import com.linkticinventario.linkticinventario.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByProductoId(Long productoId);
}
