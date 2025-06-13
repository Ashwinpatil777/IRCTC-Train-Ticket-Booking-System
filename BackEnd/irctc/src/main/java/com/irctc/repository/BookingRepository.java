package com.irctc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.irctc.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findByPnr(String pnr);
    boolean existsByPnr(String pnr);
    void deleteByPnr(String pnr);
}
