package com.spring.hotel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spring.hotel.model.Room;
import com.spring.hotel.service.RoomService;


@RestController
public class RoomController {
	@Autowired
	private RoomService roomServ;

	@GetMapping("/roomDetails")
	public List<Room> getAllRooms(){
		return roomServ.getAllRooms();
	}

	@PostMapping("/roomDetails/{bookingID}")
	public ResponseEntity<String> addRoom(@PathVariable Long bookingID,@RequestBody Room roomDetails) {
		roomServ.addRoom(bookingID,roomDetails);
		return ResponseEntity.ok("Data added successfully");
	}

	@PutMapping("/roomDetails/{roomID}/{bookingID}")
	public ResponseEntity<String> updateRoom(@PathVariable Long roomID, @PathVariable Long bookingID, @RequestBody Room roomDetails) {
		roomServ.updateRoom(roomID,bookingID, roomDetails);
		return ResponseEntity.ok("Data updated successfully");
	}

	@DeleteMapping("/roomDetails/{roomID}")
	public ResponseEntity<String> deleteRoom(@PathVariable Long roomID) {
		roomServ.deleteRoom(roomID);
		return ResponseEntity.ok("Data deleted successfully");
	}

}
