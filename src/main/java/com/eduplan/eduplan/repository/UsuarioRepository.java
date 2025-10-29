package com.eduplan.eduplan.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.eduplan.eduplan.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreoElectronicoAndContrasena(String correoElectronico, String contrasena);
    
}