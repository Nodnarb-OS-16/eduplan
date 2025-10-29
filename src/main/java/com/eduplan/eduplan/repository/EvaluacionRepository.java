package com.eduplan.eduplan.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eduplan.eduplan.model.Evaluacion;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    List<Evaluacion> findByCursoIdAndFecha(Long cursoId, LocalDate fecha);
    List<Evaluacion> findByCursoId(Long cursoId);
    
}