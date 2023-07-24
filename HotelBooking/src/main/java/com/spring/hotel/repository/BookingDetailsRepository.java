package com.spring.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.hotel.model.BookingDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BookingDetailsRepository extends JpaRepository<BookingDetails, Long> {
    List<BookingDetails> findByCustCustomerID(Long customerID);


}

