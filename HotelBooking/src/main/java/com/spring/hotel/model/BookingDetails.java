package com.spring.hotel.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;

import java.sql.Date;
import java.util.List;


import javax.persistence.*;

@Entity
@Data
@Table(name="booking_details")
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class BookingDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_seq")
	@SequenceGenerator(name = "booking_seq", sequenceName = "booking_sequence", initialValue = 1001, allocationSize = 1)

	@Column(name = "bookingID")
    private Long bookingID;

    @Column(name = "duration")
    private int duration;

    @Column(name = "start_date")
    private Date start_date;

    @Column(name = "end_date")
    private Date end_date;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "bookingID", referencedColumnName = "bookingID")
    private List<Customer> cust;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "bookingID", referencedColumnName = "bookingID")
    private List<Room> room;

    @Column(name = "modeOfBooking")
    private String modeOfBooking;

    @Column(name = "bill_amount")
    private double bill_amount;

    @Column(name = "modeOfPayment")
    private String modeOfPayment;

	@Column(name="advanceAmount")
	private double advanceAmount;

	@Column(name = "discountPercentage")
	private Double discountPercentage;

}
