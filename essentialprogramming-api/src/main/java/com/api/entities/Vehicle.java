package com.api.entities;

import javax.persistence.*;

@Entity
@Table(name = "vehicles", schema = "avangarde_internship")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private int id;

    @Column(name = "vehicle_model", nullable = false)
    private String model;

    @Column(name = "vehicle_plate", nullable = false, unique = true)
    private String plate;

    public Vehicle() {
    }

    public Vehicle(String model, String plate) {
        this.model = model;
        this.plate = plate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }
}
