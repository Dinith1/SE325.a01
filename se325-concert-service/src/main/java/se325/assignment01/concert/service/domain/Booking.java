package se325.assignment01.concert.service.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import se325.assignment01.concert.common.jackson.LocalDateTimeDeserializer;
import se325.assignment01.concert.common.jackson.LocalDateTimeSerializer;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "CONCERT_ID")
    private Long concertId;

    private LocalDateTime date;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private List<Seat> seats = new ArrayList<>();

    Long userId;

    public Booking() {
    }

    public Booking(Long concertId, LocalDateTime date, List<Seat> seats, Long userId) {
        this.concertId = concertId;
        this.date = date;
        this.setSeats(seats);
        this.userId = userId;
    }

    public Long getConcertId() {
        return concertId;
    }

    public void setConcertId(Long concertId) {
        this.concertId = concertId;
    }

    @JsonSerialize(contentUsing = LocalDateTimeSerializer.class)
    @JsonDeserialize(contentUsing = LocalDateTimeDeserializer.class)
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        for (Seat s : seats) {
            s.setBooked(true);
        }

        this.seats = seats;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Booking, concertId: ");
        buffer.append(concertId);
        buffer.append(", date: ");
        buffer.append(date.toString());
        buffer.append(", seats: {");
        for (Seat s : seats) {
            buffer.append(s.toString());
            buffer.append(", ");
        }
        buffer.append("}");
        buffer.append(", userId: ");
        buffer.append(userId);
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(concertId).append(date).append(seats).hashCode();
    }
}