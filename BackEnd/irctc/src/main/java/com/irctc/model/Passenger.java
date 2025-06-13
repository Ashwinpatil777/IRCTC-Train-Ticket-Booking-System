package com.irctc.model;

import org.hibernate.annotations.ColumnTransformer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Passenger name is required")
    private String name;

    @Min(value = 1, message = "Age must be at least 1")
    private int age;

    @NotBlank(message = "Aadhaar is required")
    private String aadhaar;  // Add this field
    
    @Column(name = "seat_number")
    private Integer  seatNumber;


    public String getAadhaar() {
        return this.aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }

    @ManyToOne
    @JoinColumn(name = "booking_id")
    @JsonIgnore
    private Booking booking;
}
