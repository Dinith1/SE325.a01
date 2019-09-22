// package se325.assignment01.concert.service.mapper;

// import se325.assignment01.concert.common.dto.ConcertDTO;
// import se325.assignment01.concert.service.domain.Concert;

// /**
//  * Helper class to convert between domain-model and DTO objects representing
//  * Concerts.
//  */
// public class ConcertMapper {

//     static Concert toDomainModel(ConcertDTO concertDto) {
//         return new Concert(concertDto.getId(), concertDto.getTitle(), concertDto.getImageName(),
//                 concertDto.getImageName(), concertDto.getDates(), concertDto.getPerformers());
//     }
// }