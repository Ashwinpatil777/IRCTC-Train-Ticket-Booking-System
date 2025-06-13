package com.irctc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "PNR is required")
    @Column(name = "pnr", nullable = false, unique = true)
    private String pnr;

    @NotBlank(message = "Seat class is required")
    @Column(name = "seat_class", nullable = false)
    private String seatClass;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;

    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    @JsonManagedReference
    private Train train;

    @NotBlank(message = "User email is required")
    @Column(name = "user_email", nullable = false)
    private String userEmail;
    
    @JsonManagedReference
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Passenger> passengers = new ArrayList<>();
    
    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

}