package com.ElVacio.runner.repository;

import com.ElVacio.runner.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Esto nos servira luego para el Login: buscar por nombre
    Usuario findByUsername(String username);
}