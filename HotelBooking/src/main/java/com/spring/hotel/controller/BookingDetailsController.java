package com.spring.hotel.controller;

import java.util.List;
import java.util.NoSuchElementException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spring.hotel.model.BookingDetails;
import com.spring.hotel.service.BookingDetailsService;

import javax.mail.MessagingException;

@RestController
@Api(tags = "Booking Details API")
public class BookingDetailsController {

	@Autowired
	private BookingDetailsService bookServ;

	/**
	 * Retrieves all booking details.
	 *
	 * @return List of BookingDetails representing all the booking details.
	 */
	@GetMapping("/bookingDetails")
	@ApiOperation(value = "Get all booking details", notes = "Retrieve a list of all booking details")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved booking details"),
			@ApiResponse(code = 500, message = "Internal server error")
	})
	public List<BookingDetails> getAllBookingDetails(){

		return bookServ.getAllBookingDetails();
	}

	/**
	 * Retrieves booking details by ID.
	 *
	 * @param bookingID The ID of the booking details to retrieve.
	 * @return ResponseEntity containing the retrieved BookingDetails if found, or appropriate error response.
	 */
	@GetMapping("/bookingDetails/{bookingID}")
	@ApiOperation(value = "Get booking details by ID", notes = "Retrieve the booking details for a specific booking ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved booking details"),
			@ApiResponse(code = 404, message = "Booking ID Not Found"),
			@ApiResponse(code = 500, message = "Internal server error")
	})
	public ResponseEntity<BookingDetails> getBookingDetailsById(@PathVariable Long bookingID){
		BookingDetails bd = bookServ.getBookingDetailsById(bookingID);
		return ResponseEntity.ok(bd);

	}
	/**
	 * Retrieves booking details by customer ID.
	 *
	 * @param customerId The ID of the customer to retrieve booking details for.
	 * @return ResponseEntity containing the retrieved BookingDetails list if found, or appropriate error response.
	 */
	@GetMapping("/bookingDetails/customer/{customerID}")
	@ApiOperation(value = "Get booking details by customer ID", notes = "Retrieve the booking details for a specific customer ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved booking details"),
			@ApiResponse(code = 404, message = "Customer ID not found"),
			@ApiResponse(code = 500, message = "Internal server error")
	})
	public ResponseEntity<List<BookingDetails>> getBookingDetailsByCustomerId(@PathVariable("customerID") Long customerId) {
		List<BookingDetails> bookingDetails = bookServ.getBookingDetailsByCustomerId(customerId);
		return ResponseEntity.ok(bookingDetails);
	}

	/**
	 * Adds a new booking details.
	 *
	 * @param bookingDetails The BookingDetails object to be added.
	 * @return ResponseEntity with success message if the data is added successfully.
	 */
	@PostMapping("/bookingDetails")
	@ApiOperation(value = "Add booking details", notes = "Create a new booking details entry")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Booking details added successfully"),
			@ApiResponse(code = 500, message = "Internal server error")
	})
	public ResponseEntity<String> addBookingDetails(@RequestBody BookingDetails bookingDetails) throws MessagingException {
		bookServ.addBookingDetails(bookingDetails);
		return ResponseEntity.ok("Data added successfully");
	}

	/**
	 * Updates booking details by ID.
	 *
	 * @param bookingID The ID of the booking details to be updated.
	 * @param bookingDetails The updated BookingDetails object.
	 * @return ResponseEntity with success message if the data is updated successfully, or appropriate error response.
	 */
	@RequestMapping(method= RequestMethod.PUT, value ="/bookingDetails/{bookingID}")
	@ApiOperation(value = "Update booking details", notes = "Update the booking details for a specific booking ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Booking details updated successfully"),
			@ApiResponse(code = 404, message = "booking ID not found"),
			@ApiResponse(code = 500, message = "Internal server error")
	})
	public ResponseEntity<String> updateBookingDetails(@PathVariable Long bookingID,@RequestBody BookingDetails bookingDetails) {
		try {
			bookServ.updateBookingDetails(bookingID, bookingDetails);
			return ResponseEntity.ok("Data of Booking ID: " + bookingID + "\nUpdated successfully");
		}catch(NoSuchElementException e){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Booking id is not found.");
		}
	}

	/**
	 * Deletes booking details by ID.
	 *
	 * @param bookingID The ID of the booking details to be deleted.
	 * @return ResponseEntity with success message if the data is deleted successfully.
	 */
	@RequestMapping(method=RequestMethod.DELETE , value ="/bookingDetails/{bookingID}")
	@ApiOperation(value = "Delete booking details", notes = "Delete the booking details for a specific booking ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Booking details deleted successfully"),
			@ApiResponse(code = 404, message = "booking ID not found"),
			@ApiResponse(code = 500, message = "Internal server error")
	})
	public ResponseEntity<String> deleteBookingDetails(@PathVariable Long bookingID) {
		bookServ.deleteBookingDetails(bookingID);
		return ResponseEntity.ok("Data of Booking ID: "+bookingID+"\nDeleted successfully");
	}
}
