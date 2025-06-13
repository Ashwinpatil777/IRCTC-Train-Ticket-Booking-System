package com.irctc.service;

import com.irctc.DTO.BookingRequest;
import com.irctc.DTO.BookingResponse;
import com.irctc.Exception.InvalidBookingRequestException;
import com.irctc.Exception.SeatsNotAvailableException;
import com.irctc.Exception.TrainNotFoundException;
import com.irctc.model.Booking;
import com.irctc.model.Passenger;
import com.irctc.model.SeatClass;
import com.irctc.model.Train;
import com.irctc.repository.BookingRepository;
import com.irctc.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final TrainRepository trainRepository;

    @Transactional
    public BookingResponse bookTicket(BookingRequest request) {
        if (request.getPassengers().size() != request.getNumberOfTickets()) {
            logger.warn("Invalid booking: passenger count ({}) does not match number of tickets ({})",
                request.getPassengers().size(), request.getNumberOfTickets());
            throw new InvalidBookingRequestException("Number of tickets must match passenger count");
        }

        Train train = trainRepository.findById(request.getTrainId())
            .orElseThrow(() -> new TrainNotFoundException("Train not found with ID: " + request.getTrainId()));

        SeatClass seatClass;
        try {
            seatClass = SeatClass.valueOf(request.getSeatClass().toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid seat class: {}", request.getSeatClass());
            throw new InvalidBookingRequestException("Invalid seat class: " + request.getSeatClass());
        }

        boolean seatsAvailable = false;
        int requestedTickets = request.getNumberOfTickets();
        switch (seatClass) {
            case SLEEPER:
                seatsAvailable = train.isSleeper() && train.getSleeperSeatsAvailable() >= requestedTickets;
                if (seatsAvailable) {
                    train.setSleeperSeatsAvailable(train.getSleeperSeatsAvailable() - requestedTickets);
                }
                break;
            case AC3TIER:
                seatsAvailable = train.isAc3Tier() && train.getAc3TierSeatsAvailable() >= requestedTickets;
                if (seatsAvailable) {
                    train.setAc3TierSeatsAvailable(train.getAc3TierSeatsAvailable() - requestedTickets);
                }
                break;
            case AC2TIER:
                seatsAvailable = train.isAc2Tier() && train.getAc2TierSeatsAvailable() >= requestedTickets;
                if (seatsAvailable) {
                    train.setAc2TierSeatsAvailable(train.getAc2TierSeatsAvailable() - requestedTickets);
                }
                break;
            case ACFIRSTCLASS:
                seatsAvailable = train.isAcFirstClass() && train.getAcFirstClassSeatsAvailable() >= requestedTickets;
                if (seatsAvailable) {
                    train.setAcFirstClassSeatsAvailable(train.getAcFirstClassSeatsAvailable() - requestedTickets);
                }
                break;
        }

        if (!seatsAvailable) {
            logger.warn("No seats available in {} for train ID: {}", seatClass, train.getId());
            throw new SeatsNotAvailableException("No seats available in " + seatClass + " for requested " + requestedTickets + " tickets");
        }

        trainRepository.save(train);

        String pnr;
        do {
            pnr = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (bookingRepository.existsByPnr(pnr));

        Booking booking = new Booking();
        booking.setTrain(train);
        booking.setSeatClass(seatClass.name());
        booking.setPnr(pnr);
        booking.setBookingTime(LocalDateTime.now());
       
        Set<Integer> allocatedSeats = new HashSet<>();
        Random random = new Random();
        List<Passenger> passengerList = request.getPassengers().stream().map(info -> {
            Passenger p = new Passenger();
            p.setName(info.getName());
            p.setAge(info.getAge());
            p.setAadhaar(info.getAadhaar());
            p.setBooking(booking);
            int seat;
            do {
                seat = random.nextInt(100) + 1;
            } while (allocatedSeats.contains(seat));
            allocatedSeats.add(seat);
            p.setSeatNumber(seat);
            return p;
        }).collect(Collectors.toList());

        booking.setPassengers(passengerList);

        bookingRepository.save(booking);
        logger.info("Booking created with PNR: {} for user: {}", pnr, request.getUser());

        List<BookingResponse.PassengerInfo> passengerInfos = passengerList.stream()
        	    .map(p -> new BookingResponse.PassengerInfo(
        	        p.getName(),
        	        p.getAge(),
        	        maskAadhaar(p.getAadhaar()),
        	        p.getSeatNumber()))
        	    .collect(Collectors.toList());


        return new BookingResponse(
            pnr,
            seatClass.name(),
            booking.getBookingTime(),
            new BookingResponse.TrainDetails(train.getName(), train.getFromStation(), train.getToStation()),
            passengerInfos,
            "SUCCESS",
            "Booking confirmed successfully!",
            getRemainingSeats(train, seatClass)
        );
    }

    private int getRemainingSeats(Train train, SeatClass seatClass) {
        switch (seatClass) {
            case SLEEPER: return train.getSleeperSeatsAvailable();
            case AC3TIER: return train.getAc3TierSeatsAvailable();
            case AC2TIER: return train.getAc2TierSeatsAvailable();
            case ACFIRSTCLASS: return train.getAcFirstClassSeatsAvailable();
            default: return -1;
        }
    }

    private String maskAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.length() < 4) return aadhaar;
        return "XXXXXXXX" + aadhaar.substring(aadhaar.length() - 4);
    }

    @Transactional
    public boolean cancelBookingByPnr(String pnr, String authenticatedUser) {
        Booking booking = bookingRepository.findByPnr(pnr);
        if (booking == null || !booking.getUserEmail().equals(authenticatedUser)) {
            logger.warn("Booking not found or unauthorized for PNR: {} by user: {}", pnr, authenticatedUser);
            return false;
        }
        Train train = booking.getTrain();
        SeatClass seatClass = SeatClass.valueOf(booking.getSeatClass());
        int passengerCount = booking.getPassengers().size();

        switch (seatClass) {
            case SLEEPER:
                train.setSleeperSeatsAvailable(train.getSleeperSeatsAvailable() + passengerCount);
                break;
            case AC3TIER:
                train.setAc3TierSeatsAvailable(train.getAc3TierSeatsAvailable() + passengerCount);
                break;
            case AC2TIER:
                train.setAc2TierSeatsAvailable(train.getAc2TierSeatsAvailable() + passengerCount);
                break;
            case ACFIRSTCLASS:
                train.setAcFirstClassSeatsAvailable(train.getAcFirstClassSeatsAvailable() + passengerCount);
                break;
        }

        trainRepository.save(train);
        bookingRepository.delete(booking);
        logger.info("Booking cancelled for PNR: {}", pnr);
        return true;
    }

    public BookingResponse getBookingByPnr(String pnr) {
        Booking booking = bookingRepository.findByPnr(pnr);
        if (booking == null) {
            logger.warn("Booking not found for PNR: {}", pnr);
            return null;
        }

        Train train = booking.getTrain();
        SeatClass seatClass = SeatClass.valueOf(booking.getSeatClass());

        List<BookingResponse.PassengerInfo> passengerInfos = booking.getPassengers().stream()
        	    .map(p -> new BookingResponse.PassengerInfo(
        	        p.getName(),
        	        p.getAge(),
        	        maskAadhaar(p.getAadhaar()),
        	        p.getSeatNumber()   // <-- Include seat number here
        	    ))
        	    .collect(Collectors.toList());


        logger.info("Fetched booking for PNR: {}", pnr);
        return new BookingResponse(
            booking.getPnr(),
            booking.getSeatClass(),
            booking.getBookingTime(),
            new BookingResponse.TrainDetails(train.getName(), train.getFromStation(), train.getToStation()),
            passengerInfos,
            "SUCCESS",
            "Booking fetched successfully!",
            getRemainingSeats(train, seatClass)
        );
    }
}
