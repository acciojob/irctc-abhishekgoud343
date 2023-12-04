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
        Optional<Train> optionalTrain = trainRepository.findById(bookTicketEntryDto.getTrainId());
        if (!optionalTrain.isPresent())
            throw new Exception("Invalid train Id");
        Train train = optionalTrain.get();

        int fare = getFare(bookTicketEntryDto, train);

        List<Passenger> passengerList = passengerRepository.findAllById(bookTicketEntryDto.getPassengerIds());

        Ticket ticket = new Ticket();
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        ticket.setTrain(train);
        ticket.setTotalFare(fare);
        ticket.setPassengersList(passengerList);

        train.getBookedTickets().add(ticket);

        Passenger bookingPerson = passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).orElseThrow(() -> new Exception("Invalid booking person Id"));
        bookingPerson.getBookedTickets().add(ticket);

        return ticketRepository.saveAndFlush(ticket).getTicketId();
    }

    private static int getFare(BookTicketEntryDto bookTicketEntryDto, Train train) throws Exception {
        int avail = train.getNoOfSeats() - train.getBookedTickets().size();
        if (avail < bookTicketEntryDto.getNoOfSeats())
            throw new Exception("Less tickets are available");

        String route = train.getRoute();
        int from = route.indexOf(bookTicketEntryDto.getFromStation().name()), to = route.indexOf(bookTicketEntryDto.getToStation().name());
        if (from == -1 || to == -1 || from >= to)
            throw new Exception("Invalid stations");

        int fare = (to - from) * bookTicketEntryDto.getNoOfSeats() * 300;
        return fare;
    }
}