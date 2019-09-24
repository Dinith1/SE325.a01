package se325.assignment01.concert.service.mapper;

import java.util.ArrayList;
import java.util.List;
import se325.assignment01.concert.common.dto.BookingDTO;
import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Booking;
import se325.assignment01.concert.service.domain.Seat;

public class BookingMapper {
    public static BookingDTO toDto(Booking booking) {
        return new BookingDTO(booking.getConcertId(), booking.getDate(), seatListToDto(booking.getSeats()));
    }

      /**
     * Helper method to convert a list of Seats to a list of SeatDTOs
     * 
     * @param seats List of seats to convert
     * @return List of SeatDTOs
     */
    private static List<SeatDTO> seatListToDto(List<Seat> seats) {
        List<SeatDTO> dtos = new ArrayList<SeatDTO>();

        for (Seat s : seats) {
            dtos.add(SeatMapper.toDto(s));
        }

        return dtos;
    }
}