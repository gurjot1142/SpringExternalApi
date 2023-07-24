package com.spring.hotel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.spring.hotel.repository.BookingDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.hotel.model.BookingDetails;
import com.spring.hotel.model.Customer;
import com.spring.hotel.repository.CustomerRepository;

@Service
public class CustomerService {
	@Autowired
	public CustomerRepository customerRepo;
	@Autowired
	public BookingDetailsRepository bookingDetailsRepo;


	public List<Customer> getAllCustomer() {
		return new ArrayList<>(customerRepo.findAll());
	}

	// checks if Booking Id exists or not
	public BookingDetails checkIfBdExist(Long bookingID){
		return bookingDetailsRepo.findById(bookingID)
				.orElseThrow(() -> new NoSuchElementException("Booking ID does not exist."));
	}

	public void addCustomer(Long bookingID, Customer custDetails) {
		BookingDetails bookingDetails = checkIfBdExist(bookingID);
		if (custDetails.getAge() < 18) {
			boolean hasAdult = bookingDetails.getCust().stream().anyMatch(c -> c.getAge() >= 18);
			if (!hasAdult) {
				throw new IllegalArgumentException("Cannot add a booking without an associated adult customer.");
			}
		}

		custDetails.setBookingDetails(bookingDetails);
		customerRepo.save(custDetails);
	}


	public void updateCustomer(Long customerID, Long bookingID, Customer custDetails) {
		BookingDetails bookingDetails = checkIfBdExist(bookingID);
		Optional<Customer> existingCustomerOptional = customerRepo.findById(customerID);
		if (existingCustomerOptional.isPresent()) {
			Customer existingCustomer = existingCustomerOptional.get();
			bookingDetails = existingCustomer.getBookingDetails();

			// Check if the customer is a minor
			if (custDetails.getAge() < 18) {
				boolean hasAdult = bookingDetails.getCust().stream().anyMatch(c -> c.getAge() >= 18 && !c.getCustomerID().equals(customerID));
				if (!hasAdult) {
					throw new IllegalArgumentException("Cannot update a minor customer without an associated adult.");
				}
			}

			// Update the specific fields of the existing customer
			existingCustomer.setFullName(custDetails.getFullName());
			existingCustomer.setAddress(custDetails.getAddress());
			existingCustomer.setContactNumber(custDetails.getContactNumber());
			existingCustomer.setAge(custDetails.getAge());

			// Save the updated customer
			customerRepo.save(existingCustomer);
		} else {
			throw new IllegalArgumentException("Customer ID not found: " + customerID);
		}
	}

	public void deleteCustomer(Long id) {
		if (customerRepo.existsById(id)) {
			customerRepo.deleteById(id);
		} else {
			throw new NoSuchElementException("Cannot delete booking as Booking ID does not exist.");
		}
	}
}
