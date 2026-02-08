package com.ElVacio.runner.repository;

import com.ElVacio.runner.entity.SesionRunning;
import com.ElVacio.runner.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RunningRepository extends JpaRepository<SesionRunning, Long> {
    List<SesionRunning> findByUsuario(Usuario usuarioLogueado);

    // Al extender de JpaRepository, Spring ya sabe cómo guardar, borrar, buscar, etc.
}