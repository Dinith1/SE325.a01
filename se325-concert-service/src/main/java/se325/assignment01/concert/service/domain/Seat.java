package se325.assignment01.concert.service.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import se325.assignment01.concert.common.jackson.LocalDateTimeDeserializer;
import se325.assignment01.concert.common.jackson.LocalDateTimeSerializer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Seat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String label;
	private boolean isBooked;
	private LocalDateTime date;
	private BigDecimal price;

	public Seat() {
	}

	public Seat(Long id, String label, boolean isBooked, LocalDateTime date, BigDecimal price) {
		this.id = id;
		this.label = label;
		this.isBooked = isBooked;
		this.date = date;
		this.price = price;
	}

	public Seat(String label, boolean isBooked, LocalDateTime date, BigDecimal price) {
		this(null, label, isBooked, date, price);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isBooked() {
		return isBooked;
	}

	public void setBooked(boolean isBooked) {
		this.isBooked = isBooked;
	}

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Seat, id: ");
		buffer.append(id);
		buffer.append(", label: ");
		buffer.append(label);
		buffer.append(", isBooked: ");
		buffer.append(isBooked);
		buffer.append(", date: ");
		buffer.append(date.toString());
		buffer.append(", price: ");
		buffer.append(price.toString());
		return buffer.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Seat))
			return false;

		if (obj == this)
			return true;

		Seat seat = (Seat) obj;
		return new EqualsBuilder().append(label, seat.getLabel()).append(date, seat.getDate())
				.append(price, seat.getPrice()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(label).append(date).append(price).hashCode();
	}

}
