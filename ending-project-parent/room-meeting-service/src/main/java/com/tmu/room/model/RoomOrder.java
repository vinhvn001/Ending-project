package com.tmu.room.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "room_order")
public class RoomOrder implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name ="username")
    private String username;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "start_time")
    private Date start;

    @Column(name = "end_time")
    private Date end;

}
