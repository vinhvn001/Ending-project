package com.tmu.room.service;

import com.tmu.room.model.RoomProperties;

import java.util.List;

public interface RoomPropertiesService {

    List<RoomProperties> findAll();

    RoomProperties findById(Long id);

    void save(RoomProperties roomProperties);

    void remove(Long id);

    RoomProperties update(RoomProperties roomProperties);
}
