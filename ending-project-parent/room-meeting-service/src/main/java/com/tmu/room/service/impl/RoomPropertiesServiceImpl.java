package com.tmu.room.service.impl;

import com.tmu.room.model.RoomProperties;
import com.tmu.room.repository.RoomPropertiesRepository;
import com.tmu.room.service.RoomPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomPropertiesServiceImpl implements RoomPropertiesService {

    @Autowired
    private RoomPropertiesRepository roomPropertiesRepository;
    @Override
    public List<RoomProperties> findAll() {
        return roomPropertiesRepository.findAll();
    }

    @Override
    public RoomProperties findById(Long id) {
        return roomPropertiesRepository.findById(id).orElse(null);
    }

    @Override
    public void save(RoomProperties roomProperties) {
        roomPropertiesRepository.save(roomProperties);
    }

    @Override
    public void remove(Long id) {
        roomPropertiesRepository.deleteById(id);
    }

    @Override
    public RoomProperties update(RoomProperties roomProperties) {
        RoomProperties currentRoomProperties = roomPropertiesRepository.findById(roomProperties.getId()).orElse(null);
        if(currentRoomProperties == null ){
            return null;
        }
        currentRoomProperties.setStatus(roomProperties.getStatus());
        return currentRoomProperties;
    }
}
