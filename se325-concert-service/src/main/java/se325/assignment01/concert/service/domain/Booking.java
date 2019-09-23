package se325.assignment01.concert.service.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Booking {

    private LocalDateTime date;
    @OneToMany
    private List<Seat> seats = new ArrayList<>();
}