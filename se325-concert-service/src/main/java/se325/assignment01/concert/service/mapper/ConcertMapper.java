package se325.assignment01.concert.service.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Performer;

/**
 * Helper class to convert between domain-model and DTO objects representing
 * Concerts.
 */
public class ConcertMapper {

    static Concert toDomainModel(ConcertDTO concertDto) {
        Concert concert = new Concert(concertDto.getId(), concertDto.getTitle(), concertDto.getImageName(),
                concertDto.getImageName());

        concert.setPerformers(new HashSet<Performer>(performerDtoListToDomain(concertDto.getPerformers())));
        concert.setDates(new HashSet<LocalDateTime>(concertDto.getDates()));

        return concert;
    }

    /**
     * Helper method to convert a list of PerformerDTOs to a list of Performers
     * 
     * @param dtos List of PerformerDTOs
     * @return List of Performers
     */
    private static List<Performer> performerDtoListToDomain(List<PerformerDTO> dtos) {
        List<Performer> performers = new ArrayList<Performer>();

        for (PerformerDTO dto : dtos) {
            performers.add(PerformerMapper.toDomainModel(dto));
        }

        return performers;
    }

    public static ConcertDTO toDto(Concert concert) {
        ConcertDTO dto = new ConcertDTO(concert.getId(), concert.getTitle(), concert.getImageName(),
                concert.getBlurb());

        dto.setDates(new ArrayList<LocalDateTime>(concert.getDates()));
        dto.setPerformers(new ArrayList<PerformerDTO>(performerListToDto(concert.getPerformers())));

        return dto;
    }

    /**
     * Helper method to convert a set of Performers to a set of PerformerDTOs
     * 
     * @param performers
     * @return
     */
    private static Set<PerformerDTO> performerListToDto(Set<Performer> performers) {
        Set<PerformerDTO> dtos = new HashSet<PerformerDTO>();

        for (Performer perf : performers) {
            dtos.add(PerformerMapper.toDto(perf));
        }

        return dtos;
    }

}