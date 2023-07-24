package com.spring.hotel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.spring.hotel.model.BookingDetails;
import com.spring.hotel.repository.BookingDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.hotel.model.Room;
import com.spring.hotel.repository.RoomRepository;

@Service
public class RoomService {
	
	@Autowired
	public RoomRepository roomRepo;
	@Autowired
	public BookingDetailsRepository bookingDetailsRepo;
	
	public List<Room> getAllRooms(){
		List<Room> roomDetail = new ArrayList<>();
		roomRepo.findAll().forEach(roomDetail::add);
		return roomDetail;
	}
	public BookingDetails checkIfBdExist(Long bookingID){
		return bookingDetailsRepo.findById(bookingID)
				.orElseThrow(() -> new NoSuchElementException("Booking ID does not exist."));
	}
	public void updateBookingDetailsRecords(BookingDetails bookingDetails) {
		if (bookingDetails.getRoom() == null) {
			bookingDetails.setRoom(new ArrayList<>());
		}

		Double discountPercentage = bookingDetails.getDiscountPercentage();
		double percentage = discountPercentage != null ? discountPercentage.doubleValue() : 0.0;

		// Calculate total price
		double totalPrice = 0;
		for (Room room : bookingDetails.getRoom()) {
			totalPrice += room.getPricePerDay() * bookingDetails.getDuration();
		}
		bookingDetails.setBill_amount(totalPrice);

		if (bookingDetails.getDiscountPercentage() != null && bookingDetails.getDiscountPercentage() >= 0) {
			totalPrice = totalPrice - (totalPrice * bookingDetails.getDiscountPercentage() / 100.0);
			bookingDetails.setBill_amount(totalPrice);
		} else {
			bookingDetails.setBill_amount(totalPrice);
		}

		// Update advance amount based on the number of rooms
		if (bookingDetails.getRoom().size() > 3) {
			double advanceAmount = totalPrice * 0.5;
			bookingDetails.setAdvanceAmount(advanceAmount);
		} else {
			bookingDetails.setAdvanceAmount(0); // No advance payment required
		}

		bookingDetailsRepo.save(bookingDetails);
	}


	public void addRoom(Long bookingID,Room roomDetails) {
		BookingDetails bookingDetails = checkIfBdExist(bookingID);
		roomDetails.setBookingDetails(bookingDetails);
		roomRepo.save(roomDetails);
		updateBookingDetailsRecords(bookingDetails);
		
	}
	public void updateRoom(Long id, Long bookingID, Room roomDetails) {
		BookingDetails bookingDetails = checkIfBdExist(bookingID);
		Room existingRoom = roomRepo.findById(id).orElse(null);
		if (existingRoom != null) {
			existingRoom.setRoomType(roomDetails.getRoomType());
			existingRoom.setOccupancy(roomDetails.getOccupancy());
			existingRoom.setAvailability(roomDetails.getAvailability());
			existingRoom.setPricePerDay(roomDetails.getPricePerDay());
			existingRoom.setCheckedIn(roomDetails.isCheckedIn());
			existingRoom.setCheckedOut(roomDetails.isCheckedOut());

			// Restore the existing IDs
			existingRoom.setBookingDetails(bookingDetails);
			roomRepo.save(existingRoom);
			updateBookingDetailsRecords(bookingDetails);
		}
		
	}

	public void deleteRoom(Long id) {
		Room rooms = roomRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Room ID :"+id+" does not exist."));
		roomRepo.deleteById(id);
		BookingDetails bookingDetails = rooms.getBookingDetails();
		updateBookingDetailsRecords(bookingDetails);

	}	
}
