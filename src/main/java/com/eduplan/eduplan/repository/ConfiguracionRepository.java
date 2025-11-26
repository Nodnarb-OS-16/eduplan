package com.eduplan.eduplan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduplan.eduplan.model.Configuracion;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, Long> {
    
    /**
     * Busca una configuración por su clave única
     * @param clave La clave de la configuración (ej: "max_evaluaciones_por_dia")
     * @return Optional con la configuración si existe
     */
    Optional<Configuracion> findByClave(String clave);
    
    /**
     * Verifica si existe una configuración con la clave dada
     * @param clave La clave a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByClave(String clave);
}