package com.irctc.controller;

import com.irctc.DTO.BookingRequest;
import com.irctc.DTO.BookingResponse;
import com.irctc.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    
    @PostMapping
    public ResponseEntity<?> bookTickets(@Valid @RequestBody BookingRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            logger.warn("Unauthorized booking attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Unauthorized booking attempt"));
        }

        // Trust the authenticated user instead of request.getUser()
        String loggedInUser = auth.getName();
        request.setUser(loggedInUser);

        try {
            logger.info("Booking tickets for user: {}", loggedInUser);
            BookingResponse response = bookingService.bookTicket(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Booking failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/status/{pnr}")
    public ResponseEntity<?> getBookingStatus(@PathVariable String pnr) {
        try {
            BookingResponse response = bookingService.getBookingByPnr(pnr);
            if (response != null) {
                return ResponseEntity.ok(response);
            }
            logger.warn("Booking not found for PNR: {}", pnr);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "PNR not found"));
        } catch (Exception e) {
            logger.error("Failed to fetch booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/cancel/{pnr}")
    public ResponseEntity<?> cancelBooking(@PathVariable String pnr) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            logger.warn("Unauthorized cancellation attempt for PNR: {}", pnr);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Not authenticated"));
        }
        try {
            boolean cancelled = bookingService.cancelBookingByPnr(pnr, auth.getName());
            if (cancelled) {
                logger.info("Booking cancelled for PNR: {}", pnr);
                return ResponseEntity.ok(Map.of("message", "Booking cancelled successfully"));
            }
            logger.warn("Booking not found or unauthorized for PNR: {}", pnr);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "PNR not found or unauthorized"));
        } catch (Exception e) {
            logger.error("Cancellation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }
}