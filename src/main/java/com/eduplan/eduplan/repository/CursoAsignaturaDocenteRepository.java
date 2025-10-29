package com.eduplan.eduplan.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.eduplan.eduplan.model.CursoAsignaturaDocente;

public interface CursoAsignaturaDocenteRepository extends JpaRepository<CursoAsignaturaDocente, Long> {
    
    List<CursoAsignaturaDocente> findByDocenteId(Integer docenteId);
    
    List<CursoAsignaturaDocente> findByCursoId(Long cursoId);
}