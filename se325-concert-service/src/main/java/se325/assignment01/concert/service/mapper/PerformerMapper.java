package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Performer;

/**
 * Helper class to convert between domain-model and DTO objects representing
 * Performers.
 */
public class PerformerMapper {

    public static Performer toDomainModel(PerformerDTO dto) {
        return new Performer(dto.getId(), dto.getName(), dto.getImageName(), dto.getGenre(), dto.getBlurb());
    };

    public static PerformerDTO toDto(Performer performer) {
        return new PerformerDTO(performer.getId(), performer.getName(), performer.getImageName(), performer.getGenre(),
                performer.getBlurb());
    }
    
}