package com.tmu.room.repository;

import com.tmu.room.model.RoomOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomOrderRepository extends CrudRepository<RoomOrder, Long> {



    @Query("SELECT  a.start, a.end from RoomOrder a WHERE a.roomName =:roomName and a.end > CURRENT_DATE ORDER BY a.start ASC ")
    List<Object[]> findByRoomName( String roomName);

    @Query("SELECT a.roomName, a.start, a.end from RoomOrder a ")
    List<Object[]> findAllTime();

}
