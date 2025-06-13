package com.irctc.DTO;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookingRequest {

    @NotNull(message = "Train ID is required")
    private Long trainId;

    @NotEmpty(message = "At least one passenger is required")
    @Size(max = 6, message = "Maximum 6 passengers allowed")
    private List<PassengerInfo> passengers;

    @NotBlank(message = "Seat class is required")
    private String seatClass;

    @Min(value = 1, message = "At least one ticket is required")
    private int numberOfTickets;

    @NotBlank(message = "User is required")
    private String user;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PassengerInfo {
        @NotBlank(message = "Passenger name is required")
        private String name;

        @Min(value = 1, message = "Age must be at least 1")
        private int age;

        @NotBlank(message = "Aadhaar is required")
        @Size(min = 12, max = 12, message = "Aadhaar must be 12 digits")
        private String aadhaar;
    }
}