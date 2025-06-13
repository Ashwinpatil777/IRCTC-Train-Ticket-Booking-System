package com.irctc.controller;

import com.irctc.DTO.BookingResponse;
import com.irctc.model.Booking;
import com.irctc.model.Train;
import com.irctc.repository.BookingRepository;
import com.irctc.repository.TrainRepository;
import com.irctc.service.TrainService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/trains")
@RequiredArgsConstructor
public class TrainController {

    private static final Logger logger = LoggerFactory.getLogger(TrainController.class);

    private final TrainRepository trainRepository;
    private final BookingRepository bookingRepository;
    private final TrainService trainService;

    // Add a new train
    @PostMapping
    public ResponseEntity<Map<String, String>> addTrain(@Valid @RequestBody Train train) {
        try {
            logger.info("Adding new train: {}", train.getName());
            trainService.saveTrain(train);
            return ResponseEntity.ok(Map.of("message", "Train saved successfully"));
        } catch (Exception e) {
            logger.error("Failed to add train: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add train: " + e.getMessage()));
        }
    }

    // Get all bookings
    @GetMapping("/all-bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        try {
            List<Booking> bookings = bookingRepository.findAll();
            System.out.println("Bookings count: " + bookings.size());
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }


    @PutMapping("/update-booking/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody Booking updatedBooking) {
        logger.info("Updating booking ID: {}", id);

        Optional<Booking> optBooking = bookingRepository.findById(id);
        if (optBooking.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }

        Booking booking = optBooking.get();

        // Check if updatedBooking's scheduledDate is non-null before updating
        if (updatedBooking.getScheduledDate() != null) {
            booking.setScheduledDate(updatedBooking.getScheduledDate());
        }

        Train existingTrain = booking.getTrain();
        Train updatedTrain = updatedBooking.getTrain();

        if (updatedTrain != null && updatedTrain.getId() != null) {
            if (existingTrain == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Existing booking has no train to update.");
            }
            if (!existingTrain.getId().equals(updatedTrain.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Train ID mismatch in update.");
            }
            // Update only allowed fields
            existingTrain.setFromStation(updatedTrain.getFromStation());
            existingTrain.setToStation(updatedTrain.getToStation());

            trainRepository.save(existingTrain);
        }

        bookingRepository.save(booking);

        return ResponseEntity.ok(booking);
    }

    // Search trains by source, destination, and date
    @GetMapping("/search")
    public ResponseEntity<?> searchTrains(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam String date) {
        try {
            LocalDate travelDate = LocalDate.parse(date);
            logger.info("Searching trains from {} to {} on {}", source, destination, travelDate);
            List<Train> trains = trainService.findTrains(source, destination, travelDate);

            if (trains.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No trains found for the given criteria.");
            }

            return ResponseEntity.ok(trains);
        } catch (DateTimeException e) {
            logger.error("Invalid date format: {}", date, e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid date format. Expected yyyy-MM-dd"));
        } catch (Exception e) {
            logger.error("Search failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search trains"));
        }
    }
}
