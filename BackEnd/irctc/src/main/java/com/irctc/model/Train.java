package com.irctc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "trains")
@Getter
@Setter
@NoArgsConstructor
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Train name is required")
    private String name;

    @Min(value = 10000, message = "Train number should be at least 5 digits")
    private int number;

    @NotBlank(message = "Source station is required")
    @Column(name = "from_station", nullable = false)
    private String fromStation;

    @NotBlank(message = "Destination station is required")
    @Column(name = "to_station", nullable = false)
    private String toStation;

    @Column(name = "departure_time")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime departureTime;

    @Column(name = "arrival_time")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime arrivalTime;

    @NotBlank(message = "Running days are required")
    @Pattern(regexp = "[MTWTFSS]+", message = "Invalid running days format")
    @Column(name = "running_days")
    private String runningDays;

    @Min(value = 0, message = "Delay days cannot be negative")
    @Column(name = "delay_days")
    private Integer delayDays;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "actual_running_date")
    private LocalDate actualRunningDate;
    
    @OneToMany(mappedBy = "train")
    @JsonBackReference  // back part, prevents infinite recursion
    private List<Booking> bookings;

    private boolean sleeper;
    private boolean ac3Tier;
    private boolean ac2Tier;
    private boolean acFirstClass;

    @Min(value = 0, message = "Sleeper seats cannot be negative")
    @Column(name = "sleeper_seats_available")
    private int sleeperSeatsAvailable;

    @Min(value = 0, message = "AC 3-tier seats cannot be negative")
    @Column(name = "ac3tier_seats_available")
    private int ac3TierSeatsAvailable;

    @Min(value = 0, message = "AC 2-tier seats cannot be negative")
    @Column(name = "ac2tier_seats_available")
    private int ac2TierSeatsAvailable;

    @Min(value = 0, message = "AC first class seats cannot be negative")
    @Column(name = "acfirstclass_seats_available")
    private int acFirstClassSeatsAvailable;
}