package com.ridehailing.driver.repository;

import com.ridehailing.driver.domain.DriverLocation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverLocationRepository extends CrudRepository<DriverLocation, Long> {
}
