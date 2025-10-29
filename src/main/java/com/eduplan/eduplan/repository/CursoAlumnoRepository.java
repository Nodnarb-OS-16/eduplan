package com.eduplan.eduplan.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.eduplan.eduplan.model.CursoAlumno;

public interface CursoAlumnoRepository extends JpaRepository<CursoAlumno, Long> {
    
    List<CursoAlumno> findByAlumnoId(Integer alumnoId);
    
    List<CursoAlumno> findByCursoId(Long cursoId);
}