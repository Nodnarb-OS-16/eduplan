package com.eduplan.eduplan.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.eduplan.eduplan.model.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    
    List<Tarea> findByAlumnoId(Integer alumnoId);
    
    List<Tarea> findByCursoId(Long cursoId);
}