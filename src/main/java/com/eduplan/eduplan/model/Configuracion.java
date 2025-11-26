package com.eduplan.eduplan.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "configuracion")
public class Configuracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String clave;
    private String valor;
    private String descripcion;

    // Constructores
    public Configuracion() {
    }

    public Configuracion(String clave, String valor, String descripcion) {
        this.clave = clave;
        this.valor = valor;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Configuracion{" +
                "id=" + id +
                ", clave='" + clave + '\'' +
                ", valor='" + valor + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}