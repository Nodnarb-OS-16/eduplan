package com.eduplan.eduplan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduplan.eduplan.model.Evaluacion;
import com.eduplan.eduplan.repository.EvaluacionRepository;

@Service
public class EvaluacionService {

    @Autowired
    private EvaluacionRepository repo;

    public List<Evaluacion> listarPorCurso(Long cursoId) {
        return repo.findByCursoId(cursoId);
    }

    public Evaluacion crear(Evaluacion eval) {
        List<Evaluacion> existentes = repo.findByCursoIdAndFecha(eval.getCurso().getId(), eval.getFecha());
        if (existentes.size() >= 2) {
            throw new IllegalStateException("Ya hay 2 evaluaciones agendadas para este día.");
        }
        return repo.save(eval);
    }

    public Evaluacion editar(Evaluacion eval) {
        return repo.save(eval);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}