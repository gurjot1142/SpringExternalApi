package com.spring.hotel.service;

import com.spring.hotel.model.BookingDetails;
import com.spring.hotel.model.Room;
import com.spring.hotel.repository.BookingDetailsRepository;
import com.spring.hotel.repository.RoomRepository;
import com.spring.hotel.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RoomServiceTest{

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingDetailsRepository bookingDetailsRepository;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllRooms() {
        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(new Room());
        when(roomRepository.findAll()).thenReturn(expectedRooms);

        List<Room> actualRooms = roomService.getAllRooms();

        assertEquals(expectedRooms, actualRooms);
        verify(roomRepository, times(1)).findAll();
    }



    @Test
    public void testAddRoom() {
        Long bookingId = 1L;
        Room room = new Room();
        BookingDetails bookingDetails = new BookingDetails();
        bookingDetails.setBookingID(bookingId);
        bookingDetails.setRoom(new ArrayList<>()); // Initialize the list of rooms

        when(bookingDetailsRepository.findById(bookingId)).thenReturn(Optional.of(bookingDetails));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        assertDoesNotThrow(() -> roomService.addRoom(bookingId, room));

        verify(bookingDetailsRepository, times(1)).findById(bookingId);
        verify(roomRepository, times(1)).save(room);
        verify(bookingDetailsRepository, times(1)).save(bookingDetails);
    }

    @Test
    public void testUpdateRoom_existingRoom() {
        Long roomId = 1L;
        Long bookingId = 1L;
        Room existingRoom = new Room();
        BookingDetails bookingDetails = new BookingDetails();
        bookingDetails.setBookingID(bookingId);

        when(bookingDetailsRepository.findById(bookingId)).thenReturn(Optional.of(bookingDetails));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(existingRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(existingRoom);

        Room updatedRoom = new Room();
        updatedRoom.setRoomType("Updated Room Type");

        assertDoesNotThrow(() -> roomService.updateRoom(roomId, bookingId, updatedRoom));

        assertEquals(updatedRoom.getRoomType(), existingRoom.getRoomType());

        verify(bookingDetailsRepository, times(1)).findById(bookingId);
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, times(1)).save(existingRoom);
        verify(bookingDetailsRepository, times(1)).save(bookingDetails);
    }

    @Test
    public void testUpdateRoom_nonExistingRoom() {
        Long roomId = 1L;
        Long bookingId = 1L;

        when(bookingDetailsRepository.findById(bookingId)).thenReturn(Optional.of(new BookingDetails()));
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        Room updatedRoom = new Room();
        updatedRoom.setRoomType("Updated Room Type");

        assertDoesNotThrow(() -> roomService.updateRoom(roomId, bookingId, updatedRoom));

        verify(bookingDetailsRepository, times(1)).findById(bookingId);
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, never()).save(any(Room.class));
        verify(bookingDetailsRepository, never()).save(any(BookingDetails.class));
    }

    @Test
    public void testDeleteRoom() {
        Long roomId = 1L;
        Room room = new Room();
        room.setBookingDetails(new BookingDetails());

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        assertDoesNotThrow(() -> roomService.deleteRoom(roomId));

        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, times(1)).deleteById(roomId);
        verify(bookingDetailsRepository, times(1)).save(any(BookingDetails.class));
    }

    @Test
    public void testDeleteRoom_nonExistingRoom() {
        Long roomId = 1L;

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> roomService.deleteRoom(roomId));

        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, never()).deleteById(anyLong());
        verify(bookingDetailsRepository, never()).save(any(BookingDetails.class));
    }
}
