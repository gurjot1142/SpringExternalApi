package com.spring.hotel.service;


import java.util.*;
import java.util.concurrent.TimeUnit;

import com.spring.hotel.model.Room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.hotel.model.BookingDetails;
import com.spring.hotel.repository.BookingDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;


@Service
public class BookingDetailsService {

	@Autowired
	public BookingDetailsRepository bookingRepo;
	@Autowired
	private EmailSenderService senderService;
	private static final Logger logger = LoggerFactory.getLogger(BookingDetailsService.class);
	public List<BookingDetails> getAllBookingDetails() {
		logger.debug("Fetching all booking details.");
		return new ArrayList<>(bookingRepo.findAll());
	}
	public BookingDetails getBookingDetailsById(Long bookingID){
		logger.debug("Fetching all booking details of "+ bookingID);
		return bookingRepo.findById(bookingID).orElseThrow(() -> new NoSuchElementException("Booking details not found for ID: " + bookingID));

	}
	public List<BookingDetails> getBookingDetailsByCustomerId(Long customerId) {
		List<BookingDetails> bookingDetails = bookingRepo.findByCustCustomerID(customerId);
		if (bookingDetails.isEmpty()) {
			throw new NoSuchElementException("No booking details found for customer ID: " + customerId);
		}
		logger.info("Fetching booking details of  Customer ID : {}", customerId);
		return bookingDetails;
	}

	public void addBookingDetails(BookingDetails bookingDetails) throws MessagingException {
		logger.info("Adding booking details: {}", bookingDetails);
		long durationInMillis = bookingDetails.getEnd_date().getTime() - bookingDetails.getStart_date().getTime();
		int durationInDays = (int) TimeUnit.DAYS.convert(durationInMillis, TimeUnit.MILLISECONDS) + 1;
		bookingDetails.setDuration(durationInDays);

		if (bookingDetails.getCust().stream().anyMatch(c -> c.getAge() < 18)) {
			boolean hasAdult = bookingDetails.getCust().stream().anyMatch(c -> c.getAge() >= 18);
			if (!hasAdult) {
				throw new IllegalArgumentException("Cannot add a booking without an associated adult customer.");
			}
		}
		double netAmt=0;
		double totalAmount = 0;
		for (Room room : bookingDetails.getRoom()) {
			totalAmount += room.getPricePerDay() * bookingDetails.getDuration();
		}
		if(bookingDetails.getDiscountPercentage()>=0){
			netAmt = totalAmount-(totalAmount*bookingDetails.getDiscountPercentage()/100.0);
			bookingDetails.setBill_amount(netAmt);
		}
		else{
			bookingDetails.setBill_amount(totalAmount);
		}
		if (bookingDetails.getRoom().size() > 3) {
			double advanceAmount = netAmt * 0.5;
			bookingDetails.setAdvanceAmount(advanceAmount);
		} else {
			bookingDetails.setAdvanceAmount(0); // No advance payment required
		}
		// Save the booking details
		bookingRepo.save(bookingDetails);
		// Trigger email with the booking ID and dynamic email address after successful addition
		String toEmail = "binaykumartan@gmail.com";
		String subject = "Booking Details Added - Booking ID: " + bookingDetails.getBookingID();
		String body = "Booking details for ID: " + bookingDetails.getBookingID() + " has been added successfully.";
		senderService.sendSimpleEmail(toEmail, subject, body);

	}

	public void updateBookingDetails(Long id, BookingDetails bookingDetails) {
		Optional<BookingDetails> existingBookingOptional = bookingRepo.findById(id);
		if (existingBookingOptional.isPresent()) {
			//calculating the number of days (duration)
			BookingDetails existingBooking = existingBookingOptional.get();
			long durationInMillis = bookingDetails.getEnd_date().getTime() - bookingDetails.getStart_date().getTime();
			int durationInDays = (int) TimeUnit.DAYS.convert(durationInMillis, TimeUnit.MILLISECONDS) + 1;
			bookingDetails.setDuration(durationInDays);

			// Check if there are any minor customers in the updated booking details
			if (bookingDetails.getCust().stream().anyMatch(c -> c.getAge() < 18)) {
				// Check if there is at least one adult associated with a minor customer
				boolean hasAdult = bookingDetails.getCust().stream().anyMatch(c -> c.getAge() >= 18);
				if (!hasAdult) {
					throw new IllegalArgumentException("Cannot update booking without an associated adult customer.");
				}
			}

			//double netAmt=0;
			double totalAmount = 0;
			for (Room room : bookingDetails.getRoom()) {
				totalAmount += room.getPricePerDay() * bookingDetails.getDuration();
			}

			if(bookingDetails.getDiscountPercentage()>=0){
				totalAmount = totalAmount-(totalAmount*bookingDetails.getDiscountPercentage()/100.0);
				bookingDetails.setBill_amount(totalAmount);
			}
			else{
				bookingDetails.setBill_amount(totalAmount);
			}

			if (bookingDetails.getRoom().size() > 3) {
				double advanceAmount = totalAmount * 0.5;
				bookingDetails.setAdvanceAmount(advanceAmount);
			} else {
				bookingDetails.setAdvanceAmount(0); // No advance payment required
			}


			// Update the specific fields of the existing booking details
			existingBooking.setDuration(bookingDetails.getDuration());
			existingBooking.setStart_date(bookingDetails.getStart_date());
			existingBooking.setEnd_date(bookingDetails.getEnd_date());
			existingBooking.setModeOfBooking(bookingDetails.getModeOfBooking());
			existingBooking.setModeOfPayment(bookingDetails.getModeOfPayment());
			existingBooking.setBill_amount(bookingDetails.getBill_amount());
			existingBooking.setCust(bookingDetails.getCust());
			existingBooking.setRoom(bookingDetails.getRoom());
			existingBooking.setAdvanceAmount(bookingDetails.getAdvanceAmount());
			existingBooking.setDiscountPercentage(bookingDetails.getDiscountPercentage());

			// Save the updated booking details
			bookingRepo.save(existingBooking);
			logger.info("Updating booking details of : {}", id);
			//BookingDetails updatedBooking = bookingRepo.save(existingBooking);

		}
		else{
			throw new NoSuchElementException("Cannot update booking as Booking ID does not exist.");
		}

	}
	public void deleteBookingDetails(Long bookingId) {
		if (bookingRepo.existsById(bookingId)) {
			bookingRepo.deleteById(bookingId);
			logger.info("deleting booking details of : {}", bookingId);
		} else {
			throw new NoSuchElementException("Cannot delete booking as Booking ID does not exist.");
		}
	}
}
