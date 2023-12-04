package com.driver.repository;

import com.driver.model.Passenger;
import com.driver.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger,Integer> {

    void addTicketToPassenger(Ticket ticket, Integer bookingPersonId);
}
