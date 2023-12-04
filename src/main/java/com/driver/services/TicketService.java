package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    @Transactional
    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto) throws Exception {
        Train train = trainRepository.findById(bookTicketEntryDto.getTrainId()).orElseThrow(() -> new Exception("Invalid train Id"));

        int fare = getFare(bookTicketEntryDto, train);

        Ticket ticket = new Ticket();
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        ticket.setTrain(train);
        ticket.setTotalFare(fare);
        ticket.setPassengersList(passengerRepository.findAllById(bookTicketEntryDto.getPassengerIds()));

        trainRepository.addTicketToTrain(ticket, train);
        passengerRepository.addTicketToPassenger(ticket, bookTicketEntryDto.getBookingPersonId());

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

        return (to - from) * bookTicketEntryDto.getNoOfSeats() * 300; //fare
    }
}