package com.eduplan.eduplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.eduplan.eduplan.model.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {

}
