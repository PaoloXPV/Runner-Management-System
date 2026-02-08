package com.ElVacio.runner.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data // Generates getters, setters, equals, hashCode and toString
@Entity // JPA entity
@Table(name = "sesiones")
public class SesionRunning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;       // Session date
    private Double distancia;      // Distance in kilometers
    private String tiempo;         // Duration (e.g. "40 min")

    @Column(length = 500)
    private String descripcion;    // Session notes

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}