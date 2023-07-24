package com.spring.hotel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "room")
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Room {

    @Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_seq")
	@SequenceGenerator(name = "room_seq", sequenceName = "room_sequence", initialValue = 1601, allocationSize = 1)

	@Column(name = "roomID")
	private Long roomID;

    @Column(name = "roomNumber")
    private Long roomNumber;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "occupancy")
    private int occupancy;

    @Column(name = "availability")
    private boolean availability;

    @Column(name = "price_per_day")
    private double pricePerDay;

    @Column(name = "is_checked_in")
    private boolean isCheckedIn;

    @Column(name = "is_checked_out")
    private boolean isCheckedOut;

    @ManyToOne
    @JoinColumn(name = "bookingID")
	@JsonIgnore
    private BookingDetails bookingDetails;

	public boolean getAvailability() {
		return availability;
	}


}
