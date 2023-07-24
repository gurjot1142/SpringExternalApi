package com.spring.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.hotel.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

