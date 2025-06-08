package com.evaluacion.mapper;

import com.evaluacion.dto.PhoneDTO;
import com.evaluacion.entity.Phone;
import com.evaluacion.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class PhoneMapper {

    public static List<Phone> mapearDesdeDTOs(List<PhoneDTO> dtoList, User user) {
        return dtoList.stream().map(dto -> {
            Phone phone = new Phone();
            phone.setNumber(dto.getNumber());
            phone.setCityCode(dto.getCityCode());
            phone.setCountryCode(dto.getCountryCode());
            phone.setUser(user);
            return phone;
        }).collect(Collectors.toList());
    }
}
