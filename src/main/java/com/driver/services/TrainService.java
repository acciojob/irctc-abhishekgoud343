package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;

    public Integer addTrain(AddTrainEntryDto trainEntryDto) {
        //Add the train to the trainRepository
        //and route String logic to be taken from the Problem statement.
        //Save the train and return the trainId that is generated from the database.
        //Avoid using the lombok library

        Train train = new Train();
        train.setNoOfSeats(trainEntryDto.getNoOfSeats());
        train.setDepartureTime(trainEntryDto.getDepartureTime());

        StringBuilder route = new StringBuilder();
        List<Station> stationRoute = trainEntryDto.getStationRoute();
        for (int i = 0; i < stationRoute.size(); ++i) {
            route.append(stationRoute.get(i));
            if (i != stationRoute.size() - 1)
                route.append(",");
        }

        train.setRoute(route.toString());

        train = trainRepository.save(train);

        return train.getTrainId();
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto) {
        //Calculate the total seats available
        //Suppose the route is A B C D
        //And there are 2 seats available in total in the train
        //and 2 tickets are booked from A to C and B to D.
        //The seat is available only between A to C and A to B. If a seat is empty between 2 station it will be counted to our final ans
        //even if that seat is booked post the destStation or before the boardingStation
        //In short : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.

        Optional<Train> trainOptional = trainRepository.findById(seatAvailabilityEntryDto.getTrainId());
        if (!trainOptional.isPresent())
            return 0;
        Train train = trainOptional.get();

        String[] route = train.getRoute().split(",");
        int from = -1, to = -1;
        for (int i = 0; i < route.length; ++i) {
            if (route[i].equals(seatAvailabilityEntryDto.getFromStation().name()))
                from = i;

            if (route[i].equals(seatAvailabilityEntryDto.getToStation().name()))
                to = i;
        }
        if (from == - 1 || to == - 1 || from >= to)
            return 0;

        int res = train.getNoOfSeats();

        List<Ticket> bookedTicketList = train.getBookedTickets();
        for (int i = from; i <= to; ++i) {
            String station = route[i];
            int seats = 0;

            for (Ticket ticket : bookedTicketList) {
                int indFrom = Arrays.binarySearch(route, ticket.getFromStation().name());
                int indTo = Arrays.binarySearch(route, ticket.getToStation().name());

                if (i >= indFrom && i <= indTo)
                    ++seats;
            }

            res = Math.min(res, train.getNoOfSeats() - seats);
        }

       return res;
    }

    public Integer calculatePeopleBoardingAtAStation(Integer trainId, Station station) throws Exception {
        //We need to find out the number of people who will be boarding a train from a particular station
        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        //in a happy case we need to find out the number of such people.

        Optional<Train> trainOptional = trainRepository.findById(trainId);
        if (!trainOptional.isPresent())
            throw new Exception("Invalid train Id");
        Train train = trainOptional.get();

        if (!train.getRoute().contains(station.name()))
            throw new Exception("Train is not passing from this station");

        int res = 0;

        for (Ticket ticket : train.getBookedTickets())
            if (ticket.getFromStation().equals(station))
                res += ticket.getPassengersList().size();

        return res;
    }

    public Integer calculateOldestPersonTravelling(Integer trainId) {
        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0

        Optional<Train> trainOptional = trainRepository.findById(trainId);
        if (!trainOptional.isPresent())
            return 0;
        Train train = trainOptional.get();

        int res = 0;

        for (Ticket ticket : train.getBookedTickets())
            for (Passenger passenger : ticket.getPassengersList())
                res = Math.max(res, passenger.getAge());

        return res;
    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime){

        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame both start time and end time included.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date (More details
        //in problem statement)
        //You can also assume the seconds and milli seconds value will be 0 in a LocalTime format.

        return null;
    }

}
