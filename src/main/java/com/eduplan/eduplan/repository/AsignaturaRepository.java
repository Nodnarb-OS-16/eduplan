package com.eduplan.eduplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.eduplan.eduplan.model.Asignatura;

public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {
    
}