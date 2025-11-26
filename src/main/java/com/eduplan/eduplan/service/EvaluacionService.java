package com.eduplan.eduplan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduplan.eduplan.model.CursoAsignaturaDocente;
import com.eduplan.eduplan.model.Evaluacion;
import com.eduplan.eduplan.repository.CursoAsignaturaDocenteRepository;
import com.eduplan.eduplan.repository.EvaluacionRepository;

@Service
public class EvaluacionService {

    @Autowired
    private EvaluacionRepository evaluacionRepo;
    
    @Autowired
    private CursoAsignaturaDocenteRepository cadRepo;

    @Autowired
    private ConfiguracionService configuracionService;

    // Listar evaluaciones de un curso
    public List<Evaluacion> listarPorCurso(Long cursoId) {
        return evaluacionRepo.findByCursoAsignaturaDocente_Curso_Id(cursoId);
    }
    
    // Listar evaluaciones de un docente
    public List<Evaluacion> listarPorDocente(Integer docenteId) {
        return evaluacionRepo.findByCursoAsignaturaDocente_Docente_Id(docenteId);
    }
    
    // Listar evaluaciones que ve un alumno
    public List<Evaluacion> listarPorAlumno(Integer alumnoId) {
        return evaluacionRepo.findByAlumnoId(alumnoId);
    }

    // Crear evaluación (validar docente y límite configurable)
    public Evaluacion crear(Evaluacion eval, Integer docenteId) {
        // 1. Validar que el docente tiene asignado ese curso-asignatura
        CursoAsignaturaDocente cad = cadRepo.findById(eval.getCursoAsignaturaDocente().getId())
            .orElseThrow(() -> new RuntimeException("Asignación curso-asignatura-docente no encontrada"));
        
        if (!cad.getDocente().getId().equals(docenteId)) {
            throw new RuntimeException("No tienes permiso para agendar evaluaciones en este curso/asignatura");
        }
        
        // 2. Obtener el límite de evaluaciones desde la configuración
        int maxEvaluaciones = configuracionService.getMaxEvaluacionesPorDia();
        
        // 3. Validar límite de evaluaciones por día en el curso
        Long cursoId = cad.getCurso().getId();
        List<Evaluacion> existentes = evaluacionRepo.findByCursoAndFecha(cursoId, eval.getFecha());
        
        if (existentes.size() >= maxEvaluaciones) {
            throw new IllegalStateException(
                "Ya hay " + maxEvaluaciones + " evaluación(es) agendada(s) para el " + eval.getFecha() + 
                " en " + cad.getCurso().getNombre() + ". Límite máximo alcanzado."
            );
        }
        
        return evaluacionRepo.save(eval);
    }

    // Editar (solo el docente dueño)
    public Evaluacion editar(Evaluacion eval, Integer docenteId) {
        Evaluacion existente = evaluacionRepo.findById(eval.getId())
            .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));
        
        if (!existente.getCursoAsignaturaDocente().getDocente().getId().equals(docenteId)) {
            throw new RuntimeException("Solo puedes editar tus propias evaluaciones");
        }
        
        // Si se cambia la fecha, validar el nuevo límite
        if (!existente.getFecha().equals(eval.getFecha())) {
            int maxEvaluaciones = configuracionService.getMaxEvaluacionesPorDia();
            Long cursoId = existente.getCursoAsignaturaDocente().getCurso().getId();
            List<Evaluacion> existentesEnNuevaFecha = evaluacionRepo.findByCursoAndFecha(cursoId, eval.getFecha());
            
            if (existentesEnNuevaFecha.size() >= maxEvaluaciones) {
                throw new IllegalStateException(
                    "Ya hay " + maxEvaluaciones + " evaluación(es) agendada(s) para el " + eval.getFecha() + 
                    ". No se puede cambiar la fecha a este día."
                );
            }
        }
        
        // Actualizar campos
        existente.setFecha(eval.getFecha());
        existente.setHora(eval.getHora());
        existente.setDescripcion(eval.getDescripcion());
        
        return evaluacionRepo.save(existente);
    }

    // Eliminar (solo el docente dueño)
    public void eliminar(Long id, Integer docenteId) {
        Evaluacion eval = evaluacionRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));
        
        if (!eval.getCursoAsignaturaDocente().getDocente().getId().equals(docenteId)) {
            throw new RuntimeException("Solo puedes eliminar tus propias evaluaciones");
        }
        
        evaluacionRepo.deleteById(id);
    }
}