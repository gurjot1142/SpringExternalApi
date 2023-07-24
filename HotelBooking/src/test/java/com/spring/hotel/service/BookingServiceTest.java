package com.spring.hotel.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.spring.hotel.model.BookingDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.spring.hotel.model.Customer;
import com.spring.hotel.model.Room;
import com.spring.hotel.repository.BookingDetailsRepository;

import javax.mail.MessagingException;

class BookingServiceTest {

    @Mock
    private BookingDetailsRepository bookingRepo;

    @InjectMocks
    private BookingDetailsService bookingService;

    @BeforeEach
    public void setup() {
        System.out.println("Set up called");
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBookingDetails() throws MessagingException {
        BookingDetails bookingDetails = createBookingDetails();
        bookingService.addBookingDetails(bookingDetails);
        verify(bookingRepo, times(1)).save(bookingDetails);
    }

    @Test
    public void testUpdateBookingDetails() {
        BookingDetails bookingDetails = createBookingDetails();
        when(bookingRepo.save(any(BookingDetails.class))).thenReturn(bookingDetails);
        Long id = bookingDetails.getBookingID();

        when(bookingRepo.findById(id)).thenReturn(Optional.of(createBookingDetails()));
        when(bookingRepo.save(any(BookingDetails.class))).thenReturn(bookingDetails);
        bookingService.updateBookingDetails(id, bookingDetails);
        System.out.println("No.of rooms:"+bookingDetails.getRoom().size()+"\nAdvance amount:" + bookingDetails.getAdvanceAmount());


        // Add additional rooms to exceed the limit and trigger the advance payment constraint
        bookingDetails.getRoom().add(createRoom(205L, "Single", 1, true, 500.0, false, false));
        bookingDetails.getRoom().add(createRoom(206L, "Double", 2, true, 1000.0, false, false));
        bookingDetails.getRoom().add(createRoom(207L, "Double", 2, true, 1000.0, false, false));

        // Set the age of customers to trigger the adult constraint
        bookingDetails.getCust().get(0).setAge(14);
        bookingDetails.getCust().get(1).setAge(12);

        // Mock the behavior of findById to return a non-empty Optional with a valid booking object
        when(bookingRepo.findById(id)).thenReturn(Optional.of(createBookingDetails()));
        when(bookingRepo.save(any(BookingDetails.class))).thenReturn(bookingDetails);

        // Test the adult constraint
        try {
            bookingService.updateBookingDetails(id, bookingDetails);
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("NoSuchElementException: of age " + e.getMessage());
        }

        // Set the age of one customer to satisfy the adult constraint
        bookingDetails.getCust().get(1).setAge(18);

        try {
            bookingService.updateBookingDetails(id, bookingDetails);
            System.out.println("Updated with an adult");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("NoSuchElementException: " + e.getMessage());
        }
        //Printing the no.of rooms after adding extra rooms
        System.out.println("No.of rooms:"+bookingDetails.getRoom().size()+"\nAdvance amount:" + bookingDetails.getAdvanceAmount());


    }


    private Room createRoom(Long roomNumber, String roomType, int occupancy, boolean availability,
                            double pricePerDay, boolean checkedIn, boolean checkedOut) {
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setRoomType(roomType);
        room.setOccupancy(occupancy);
        room.setAvailability(availability);
        room.setPricePerDay(pricePerDay);
        room.setCheckedIn(checkedIn);
        room.setCheckedOut(checkedOut);
        return room;
    }

    @Test
    void testUpdateBookingDetails_NonExistentId() {
        long nonExistentId = 1001L;
        BookingDetails updatedBookingDetails = new BookingDetails();
        updatedBookingDetails.setBookingID(nonExistentId);
        when(bookingRepo.existsById(nonExistentId)).thenReturn(false);
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            bookingService.updateBookingDetails(nonExistentId, updatedBookingDetails);
        });
        assertEquals("Cannot update booking as Booking ID does not exist.", exception.getMessage());
        System.out.println(exception.getMessage());
    }


    @Test
    public void testDeleteBookingDetails() {
        Long id = 1000L;
        when(bookingRepo.existsById(id)).thenReturn(true);
        bookingService.deleteBookingDetails(id);
        verify(bookingRepo, times(1)).deleteById(id);
    }


    @Test
    public void testGetAllBookingDetails() {
        List<BookingDetails> bookingDetailsList = new ArrayList<>();
        bookingDetailsList.add(createBookingDetails());
        when(bookingRepo.findAll()).thenReturn(bookingDetailsList);
        List<BookingDetails> result = bookingService.getAllBookingDetails();
        assertEquals(bookingDetailsList.size(), result.size());
    }

    @Test
    public void testGetBookingDetailsById() {
        Long id = 1000L;
        BookingDetails bookingDetails = createBookingDetails();
        when(bookingRepo.findById(id)).thenReturn(Optional.of(bookingDetails));
        BookingDetails result = bookingService.getBookingDetailsById(id);
        assertEquals(bookingDetails, result);
    }
    @Test
    public void testGetBookingDetailsByCustomerId() {
        Long customerId = 101L;
        // Empty list to simulate no booking details found for the customer
        when(bookingRepo.findByCustCustomerID(customerId)).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            bookingService.getBookingDetailsByCustomerId(customerId);
        });
        assertEquals("No booking details found for customer ID: " + customerId, exception.getMessage());
    }

    private BookingDetails createBookingDetails() {
        BookingDetails bookingDetails = new BookingDetails();
        bookingDetails.setBookingID(1000L);
        bookingDetails.setStart_date(Date.valueOf("2023-05-18"));
        bookingDetails.setEnd_date(Date.valueOf("2023-05-20"));
        bookingDetails.setModeOfBooking("Online");
        bookingDetails.setModeOfPayment("Credit Card");
        bookingDetails.setDiscountPercentage(25.0);

        List<Customer> customers = new ArrayList<>();
        Customer customer1 = new Customer();
        customer1.setFullName("Binay Kumar");
        customer1.setAddress("123 Main St");
        customer1.setContactNumber("9876543210");
        customer1.setAge(13);
        customers.add(customer1);

        Customer customer2 = new Customer();
        customer2.setFullName("Gurjot Parmar");
        customer2.setAddress("456 Elm St");
        customer2.setContactNumber("1234567890");
        customer2.setAge(24);
        customers.add(customer2);

        Customer customer3 = new Customer();
        customer3.setFullName("Radhika Narang");
        customer3.setAddress("789 Oak St");
        customer3.setContactNumber("5551234567");
        customer3.setAge(15);
        customers.add(customer3);

        Customer customer4 = new Customer();
        customer4.setFullName("Shaurya Dev");
        customer4.setAddress("321 Pine St");
        customer4.setContactNumber("9876543210");
        customer4.setAge(15);
        customers.add(customer4);

        bookingDetails.setCust(customers);

        List<Room> rooms = new ArrayList<>();
        Room room1 = new Room();
        room1.setRoomNumber(201L);
        room1.setRoomType("Double");
        room1.setOccupancy(2);
        room1.setAvailability(true);
        room1.setPricePerDay(1000.0);
        room1.setCheckedIn(false);
        room1.setCheckedOut(false);
        rooms.add(room1);

        Room room2 = new Room();
        room2.setRoomNumber(202L);
        room2.setRoomType("Single");
        room2.setOccupancy(1);
        room2.setAvailability(true);
        room2.setPricePerDay(500.0);
        room2.setCheckedIn(false);
        room2.setCheckedOut(false);
        rooms.add(room2);

        Room room3 = new Room();
        room3.setRoomNumber(203L);
        room3.setRoomType("Double");
        room3.setOccupancy(2);
        room3.setAvailability(true);
        room3.setPricePerDay(1000.0);
        room3.setCheckedIn(false);
        room3.setCheckedOut(false);
        rooms.add(room3);

        bookingDetails.setRoom(rooms);

        return bookingDetails;
    }
}
