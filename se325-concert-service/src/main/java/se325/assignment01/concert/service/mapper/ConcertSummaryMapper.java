package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.service.domain.Concert;

/**
 * Helper class to create a ConcerSummaryDTO object from a Concert object.
 */
public class ConcertSummaryMapper {

    public static ConcertSummaryDTO toConcertSummary(Concert concert) {
        return new ConcertSummaryDTO(concert.getId(), concert.getTitle(), concert.getImageName());
    }
    
}