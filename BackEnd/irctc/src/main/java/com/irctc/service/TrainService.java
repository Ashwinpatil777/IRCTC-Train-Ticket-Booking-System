package com.irctc.service;

import com.irctc.model.Train;
import java.time.LocalDate;
import java.util.List;

public interface TrainService {
    List<Train> findTrains(String source, String destination, LocalDate date);
    void saveTrain(Train train);
}
