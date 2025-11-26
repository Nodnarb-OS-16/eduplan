package com.eduplan.eduplan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduplan.eduplan.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    Optional<Usuario> findByCorreoElectronicoAndContrasena(String correoElectronico, String contrasena);
    
    // Método adicional para verificar si existe un correo
    boolean existsByCorreoElectronico(String correoElectronico);
    
    // Método para buscar por correo
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);
    
    // Método para buscar por rol
    List<Usuario> findByRol_Id(Integer rolId);
}