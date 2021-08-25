package com.api.mapper;

import com.api.entities.User;
import com.api.entities.Vehicle;
import com.api.model.UserInput;
import com.api.output.UserJSON;
import com.api.output.VehicleJSON;


public class UserMapper {

    public static User inputToUser(UserInput input) {
        return User.builder()
                .username(input.getUserName())
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .email(input.getEmail())
                .phone(input.getPhone())
                .build();
    }

    public static UserJSON userToJson(User user) {
        return UserJSON.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .userKey(user.getUserKey())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .build();
    }

    public static VehicleJSON vehicleToJson(Vehicle vehicle) {
        return VehicleJSON.builder()
                .model(vehicle.getModel())
                .plate(vehicle.getPlate())
                .build();
    }


}
