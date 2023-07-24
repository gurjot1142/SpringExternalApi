package com.spring.hotel.service;

import com.spring.hotel.model.BookingDetails;
import com.spring.hotel.model.Customer;
import com.spring.hotel.repository.BookingDetailsRepository;
import com.spring.hotel.repository.CustomerRepository;
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
import static org.mockito.Mockito.*;

public class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private BookingDetailsRepository bookingDetailsRepo;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    public void setup() {
        System.out.println("Set up called");
        MockitoAnnotations.openMocks(this);
    }

    private Customer createCustomer(String fullName, String address, String contactNumber, int age) {
        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setAddress(address);
        customer.setContactNumber(contactNumber);
        customer.setAge(age);
        return customer;
    }

    @Test
    public void testGetAllCustomer() {
        List<Customer> expectedCustomers = new ArrayList<>();
        expectedCustomers.add(createCustomer("John Doe", "123 Main St", "1234567890", 25));
        expectedCustomers.add(createCustomer("Jane Smith", "456 Elm St", "9876543210", 30));

        when(customerRepo.findAll()).thenReturn(expectedCustomers);

        List<Customer> actualCustomers = customerService.getAllCustomer();

        assertEquals(expectedCustomers.size(), actualCustomers.size());
        assertEquals(expectedCustomers, actualCustomers);

        verify(customerRepo, times(1)).findAll();
    }

    @Test
    public void testAddCustomer() {
        Long bookingID = 1000L;
        Customer customer = createCustomer("John Doe", "123 Main St", "1234567890", 25);

        BookingDetails bookingDetails = new BookingDetails();
        bookingDetails.setCust(new ArrayList<>());
        when(bookingDetailsRepo.findById(bookingID)).thenReturn(Optional.of(bookingDetails));
        when(customerRepo.save(customer)).thenReturn(customer);

        assertDoesNotThrow(() -> customerService.addCustomer(bookingID, customer));

        verify(customerRepo, times(1)).save(customer);
    }

    @Test
    public void testUpdateCustomer() {
        Long customerID = 100L;
        Long bookingID = 2000L;
        Customer existingCustomer = createCustomer("John Doe", "123 Main St", "1234567890", 25);
        Customer updatedCustomer = createCustomer("John Smith", "456 Elm St", "9876543210", 30);

        Optional<Customer> existingCustomerOptional = Optional.of(existingCustomer);
        BookingDetails bookingDetails = new BookingDetails();
        bookingDetails.setBookingID(bookingID); // Set the ID of the booking details
        bookingDetails.setCust(new ArrayList<>());

        // Configure mock behavior
        when(customerRepo.findById(customerID)).thenReturn(existingCustomerOptional);
        when(customerRepo.save(existingCustomer)).thenReturn(existingCustomer);
        when(bookingDetailsRepo.findById(bookingID)).thenReturn(Optional.of(bookingDetails));

        // Invoke the method and assert the results
        assertDoesNotThrow(() -> customerService.updateCustomer(customerID, bookingID, updatedCustomer));
        assertEquals(updatedCustomer.getFullName(), existingCustomer.getFullName());
        assertEquals(updatedCustomer.getAddress(), existingCustomer.getAddress());
        assertEquals(updatedCustomer.getContactNumber(), existingCustomer.getContactNumber());
        assertEquals(updatedCustomer.getAge(), existingCustomer.getAge());

        // Verify the mock interactions
        verify(customerRepo, times(1)).findById(customerID);
        verify(customerRepo, times(1)).save(existingCustomer);
        verify(bookingDetailsRepo, times(1)).findById(bookingID);
    }


    @Test
    public void testDeleteCustomer() {
        Long customerID = 100L;

        when(customerRepo.existsById(customerID)).thenReturn(true);
        doNothing().when(customerRepo).deleteById(customerID);

        assertDoesNotThrow(() -> customerService.deleteCustomer(customerID));

        verify(customerRepo, times(1)).existsById(customerID);
        verify(customerRepo, times(1)).deleteById(customerID);
    }
}
