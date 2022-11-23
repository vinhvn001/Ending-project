package com.tmu.room.repository;


import com.tmu.room.model.RoomProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomPropertiesRepository extends JpaRepository<RoomProperties, Long> {
}
