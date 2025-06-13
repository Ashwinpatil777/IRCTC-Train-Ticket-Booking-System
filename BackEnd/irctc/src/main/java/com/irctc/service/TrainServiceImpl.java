package com.irctc.service;

import com.irctc.Exception.TrainNotFoundException;
import com.irctc.model.Train;
import com.irctc.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {

    private static final Logger logger = LoggerFactory.getLogger(TrainServiceImpl.class);

    private final TrainRepository trainRepository;

    @Override
    public List<Train> findTrains(String source, String destination, LocalDate date) {
        if (source == null || destination == null || date == null) {
            logger.warn("Invalid search parameters: source={}, destination={}, date={}", source, destination, date);
            throw new IllegalArgumentException("Source, destination, and date are required");
        }
        String day = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        List<Train> trains = trainRepository.findTrainsByDay(source, destination, day);
        if (trains.isEmpty()) {
            logger.info("No trains found from {} to {} on {}", source, destination, date);
            throw new TrainNotFoundException("No trains found from " + source + " to " + destination + " on " + date);
        }
        logger.info("Found {} trains from {} to {} on {}", trains.size(), source, destination, date);
        return trains;
    }

    @Override
    public void saveTrain(Train train) {
        logger.info("Saving train: {}", train.getName());
        trainRepository.save(train);
    }
}