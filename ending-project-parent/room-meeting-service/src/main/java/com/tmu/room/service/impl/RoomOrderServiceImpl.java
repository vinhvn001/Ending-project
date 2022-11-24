package com.tmu.room.service.impl;

import com.tmu.room.model.RoomOrder;
import com.tmu.room.repository.RoomOrderRepository;
import com.tmu.room.service.RoomOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomOrderServiceImpl implements RoomOrderService {

    @Autowired
    private RoomOrderRepository roomOrderRepository;

    @Override
    public List<RoomOrder> findAll() {
        return (List<RoomOrder>) roomOrderRepository.findAll();
    }

    @Override
    public RoomOrder findById(Long id) {
        return roomOrderRepository.findById(id).orElse(null);
    }

    @Override
    public void save(RoomOrder roomOrder) {
        roomOrderRepository.save(roomOrder);
    }

    @Override
    public void remove(Long id) {
        roomOrderRepository.deleteById(id);
    }

    @Override
    public List<Object[]> findByRoomName(String roomName) {
        return roomOrderRepository.findByRoomName(roomName);
    }

    @Override
    public List<Object[]> findAllTime() {
        return roomOrderRepository.findAllTime();
    }
}
