package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Seat;

/**
 * Helper class to convert from domain-model to DTO objects representing Seats.
 */
public class SeatMapper {

    public static SeatDTO toDto(Seat seat) {
        return new SeatDTO(seat.getLabel(), seat.getPrice());
    }
    
}