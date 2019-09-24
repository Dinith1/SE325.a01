package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Seat;

public class SeatMapper {
    // public static Seat toDomainModel(SeatDTO dto) {
    //     return new Seat
    // }

    public static SeatDTO toDto(Seat seat) {
        return new SeatDTO(seat.getLabel(), seat.getPrice());
    }
}