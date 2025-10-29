package com.eduplan.eduplan.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eduplan.eduplan.model.Evaluacion;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    
    // Buscar evaluaciones por curso y fecha (para validar máximo 2 por día)
    @Query("SELECT e FROM Evaluacion e WHERE e.cursoAsignaturaDocente.curso.id = :cursoId AND e.fecha = :fecha")
    List<Evaluacion> findByCursoAndFecha(@Param("cursoId") Long cursoId, @Param("fecha") LocalDate fecha);
    
    // Listar evaluaciones de un curso
    List<Evaluacion> findByCursoAsignaturaDocente_Curso_Id(Long cursoId);
    
    // Listar evaluaciones de un docente
    List<Evaluacion> findByCursoAsignaturaDocente_Docente_Id(Integer docenteId);
    
    // Listar evaluaciones que ve un alumno
    @Query("SELECT e FROM Evaluacion e WHERE e.cursoAsignaturaDocente.curso.id IN " +
           "(SELECT ca.curso.id FROM CursoAlumno ca WHERE ca.alumno.id = :alumnoId)")
    List<Evaluacion> findByAlumnoId(@Param("alumnoId") Integer alumnoId);
}