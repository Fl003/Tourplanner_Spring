package com.example.tourplanner.repo;

import com.example.tourplanner.model.Log;
import org.springframework.data.repository.CrudRepository;

public interface LogRepository extends CrudRepository<Log, Long> {
    Integer removeLogById(Long id);
}
