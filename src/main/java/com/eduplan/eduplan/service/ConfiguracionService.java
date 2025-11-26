package com.eduplan.eduplan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduplan.eduplan.model.Configuracion;
import com.eduplan.eduplan.repository.ConfiguracionRepository;

@Service
public class ConfiguracionService {

    @Autowired
    private ConfiguracionRepository configuracionRepo;

    // Constantes para las claves de configuración
    private static final String CLAVE_MAX_EVALUACIONES = "max_evaluaciones_por_dia";
    private static final int VALOR_DEFECTO_MAX_EVALUACIONES = 2;
    private static final int MIN_EVALUACIONES = 1;
    private static final int MAX_EVALUACIONES = 10;

    /**
     * Obtiene el límite máximo de evaluaciones por día configurado
     * @return El número máximo de evaluaciones permitidas por día
     */
    public int getMaxEvaluacionesPorDia() {
        return configuracionRepo.findByClave(CLAVE_MAX_EVALUACIONES)
            .map(config -> {
                try {
                    return Integer.parseInt(config.getValor());
                } catch (NumberFormatException e) {
                    // Si hay un error en el formato, devolver valor por defecto
                    return VALOR_DEFECTO_MAX_EVALUACIONES;
                }
            })
            .orElse(VALOR_DEFECTO_MAX_EVALUACIONES); // Valor por defecto si no existe
    }

    /**
     * Actualiza el límite máximo de evaluaciones por día (solo Director)
     * @param nuevoValor El nuevo límite (debe estar entre 1 y 10)
     * @param directorId ID del director que realiza el cambio (para auditoría)
     * @return La configuración actualizada
     * @throws IllegalArgumentException Si el valor está fuera del rango permitido
     * @throws RuntimeException Si la configuración no existe en la base de datos
     */
    @Transactional
    public Configuracion actualizarMaxEvaluaciones(int nuevoValor, Integer directorId) {
        // Validar rango
        if (nuevoValor < MIN_EVALUACIONES || nuevoValor > MAX_EVALUACIONES) {
            throw new IllegalArgumentException(
                "El valor debe estar entre " + MIN_EVALUACIONES + " y " + MAX_EVALUACIONES
            );
        }

        // Buscar la configuración existente
        Configuracion config = configuracionRepo.findByClave(CLAVE_MAX_EVALUACIONES)
            .orElseThrow(() -> new RuntimeException(
                "Configuración '" + CLAVE_MAX_EVALUACIONES + "' no encontrada en la base de datos. " +
                "Por favor, ejecuta el script de inicialización de la base de datos."
            ));

        // Actualizar el valor
        config.setValor(String.valueOf(nuevoValor));
        
        return configuracionRepo.save(config);
    }

    /**
     * Obtiene todas las configuraciones del sistema
     * @return Lista de todas las configuraciones
     */
    public List<Configuracion> obtenerTodas() {
        return configuracionRepo.findAll();
    }

    /**
     * Obtiene una configuración específica por su clave
     * @param clave La clave de la configuración
     * @return La configuración si existe
     * @throws RuntimeException Si la configuración no existe
     */
    public Configuracion obtenerPorClave(String clave) {
        return configuracionRepo.findByClave(clave)
            .orElseThrow(() -> new RuntimeException("Configuración con clave '" + clave + "' no encontrada"));
    }

    /**
     * Crea o actualiza una configuración genérica
     * @param clave La clave de la configuración
     * @param valor El valor a establecer
     * @param descripcion Descripción de la configuración
     * @return La configuración guardada
     */
    @Transactional
    public Configuracion guardarConfiguracion(String clave, String valor, String descripcion) {
        Configuracion config = configuracionRepo.findByClave(clave)
            .orElse(new Configuracion());
        
        config.setClave(clave);
        config.setValor(valor);
        config.setDescripcion(descripcion);
        
        return configuracionRepo.save(config);
    }

    /**
     * Verifica si existe una configuración con la clave dada
     * @param clave La clave a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeConfiguracion(String clave) {
        return configuracionRepo.existsByClave(clave);
    }

    /**
     * Inicializa las configuraciones por defecto si no existen
     * Útil para ejecutar al inicio de la aplicación
     */
    @Transactional
    public void inicializarConfiguracionesPorDefecto() {
        if (!existeConfiguracion(CLAVE_MAX_EVALUACIONES)) {
            guardarConfiguracion(
                CLAVE_MAX_EVALUACIONES,
                String.valueOf(VALOR_DEFECTO_MAX_EVALUACIONES),
                "Número máximo de evaluaciones permitidas por día en un curso"
            );
        }
    }
}