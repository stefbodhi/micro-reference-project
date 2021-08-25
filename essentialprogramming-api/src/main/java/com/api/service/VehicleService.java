package com.api.service;

import com.api.entities.Vehicle;
import com.api.mapper.UserMapper;
import com.api.output.VehicleJSON;
import com.api.repository.VehicleRepository;
import com.internationalization.Messages;
import com.util.enums.HTTPCustomStatus;
import com.util.exceptions.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleService.class);

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public VehicleJSON getVehicle(String plate) {
        Optional<Vehicle> vehicle = vehicleRepository.findByPlate(plate);
        if (!vehicleRepository.findByPlate(plate).isPresent()) {
            throw new ApiException("Vehicle not found", HTTPCustomStatus.BUSINESS_VALIDATION_ERROR);
        } else {
            LOG.info("Car loaded with plate={}", plate);
            return UserMapper.vehicleToJson(vehicle.get());
        }
    }

    public List<VehicleJSON> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles.stream().map(UserMapper::vehicleToJson).collect(Collectors.toList());
    }

    public void addVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }
}
