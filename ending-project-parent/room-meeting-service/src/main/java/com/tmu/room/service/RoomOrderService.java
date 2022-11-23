package com.tmu.room.service;

import com.tmu.room.model.RoomOrder;

import java.util.List;

public interface RoomOrderService {

    List<RoomOrder> findAll();

    RoomOrder findById(Long id);

    void save(RoomOrder roomOrder);

    void remove(Long id);

    List<RoomOrder> findByRoomName(String roomName);
}
