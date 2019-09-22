package se325.assignment01.concert.service.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String imageName;
    private String blurb;
    private List<Performer> performers;
    @Enumerated
    private Set<LocalDateTime> dates = new HashSet<>();

    public Concert() {
    }

    public Concert(Long id, String title, String imageName, String blurb) {
        this.id = id;
        this.title = title;
        this.imageName = imageName;
        this.blurb = blurb;
    }

    public Concert(String title, String imageName, String blurb) {
        this(null, title, imageName, blurb);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageName() {
        return imageName;
    }

    public String getBlurb() {
        return blurb;
    }

    public List<Performer> getPerformers() {
        return performers;
    }

    public Set<LocalDateTime> getDates() {
        return dates;
    }
}
