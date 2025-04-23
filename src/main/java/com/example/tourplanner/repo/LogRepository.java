package com.example.tourplanner.repo;

import com.example.tourplanner.model.Log;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LogRepository extends CrudRepository<Log, Long> {
    Integer removeLogById(Long id);

    List<Log> findByTourId(Long tourId);
}
