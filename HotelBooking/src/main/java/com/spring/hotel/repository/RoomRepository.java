package com.spring.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.hotel.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}

