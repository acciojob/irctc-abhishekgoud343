package com.driver.repository;

import com.driver.model.Ticket;
import com.driver.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TrainRepository extends JpaRepository<Train,Integer> {

    void addTicketToTrain(Ticket ticket, Train train);
}
