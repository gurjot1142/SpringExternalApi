package com.spring.hotel.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "customer")
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
	@SequenceGenerator(name = "customer_seq", sequenceName = "customer_sequence", initialValue = 101, allocationSize = 1)
	@Column(name = "customerID")
	private Long customerID;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "address")
	private String address;

	@Column(name = "contact_number")
	private String contactNumber;

	@Column(name = "age")
	private int age;

	@ManyToOne
	@JoinColumn(name = "bookingID")
	@JsonIgnore
	private BookingDetails bookingDetails;

}
