package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto) throws Exception {
        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        //In case the there are insufficient tickets
        //throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //In case the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
        //And the end return the ticketId that has come from db

        List<Ticket> bookedTicketsList = ticketRepository.findAll();

        Optional<Train> optionalTrain = trainRepository.findById(bookTicketEntryDto.getTrainId());
        if (!optionalTrain.isPresent())
            throw new Exception("Invalid train Id");
        Train train = optionalTrain.get();

        int avail = train.getNoOfSeats() - bookedTicketsList.size();
        if (avail < bookTicketEntryDto.getNoOfSeats())
            throw new Exception("Less tickets are available");

        String route = train.getRoute();
        int from = route.indexOf(bookTicketEntryDto.getFromStation().name()), to = route.indexOf(bookTicketEntryDto.getToStation().name());
        if (from == -1 || to == -1 || from >= to)
            throw new Exception("Invalid stations");

        int fare = (route.split(",").length - 1) * bookTicketEntryDto.getNoOfSeats() * 300;

        List<Passenger> passengerList = passengerRepository.findAllById(bookTicketEntryDto.getPassengerIds());

        Ticket ticket = new Ticket();
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        ticket.setTrain(train);
        ticket.setTotalFare(fare);
        ticket.setPassengersList(passengerList);

        ticket = ticketRepository.save(ticket);

        train.getBookedTickets().add(ticket);

        for (Passenger passenger : passengerList)
            passenger.getBookedTickets().add(ticket);

       return ticket.getTicketId();
    }
}
