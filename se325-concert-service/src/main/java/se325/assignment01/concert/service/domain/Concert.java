package se325.assignment01.concert.service.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
public class Concert implements Comparable<Concert> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String imageName;
    private String blurb;
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Performer> performers = new ArrayList<Performer>();
    @Enumerated
    private Set<LocalDateTime> dates = new HashSet<>();

    public Concert() {
    }

    public Concert(Long id, String title, String imageName, String blurb, List<Performer> performers, Set<LocalDateTime> dates) {
        this.id = id;
        this.title = title;
        this.imageName = imageName;
        this.blurb = blurb;
        this.performers = (performers != null) ? performers : this.performers;
        this.dates = (dates != null) ? dates : this.dates;
    }

    public Concert(Long id, String title, String imageName, String blurb) {
        this(id, title, imageName, blurb, null, null);
    }

    public Concert(String title, String imageName, String blurb) {
        this(null, title, imageName, blurb);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public List<Performer> getPerformers() {
        return performers;
    }

    public void addPerformer(Performer performer) {
        performers.add(performer);
    }

    public Set<LocalDateTime> getDates() {
        return dates;
    }

    public void addDate(LocalDateTime date) {
        dates.add(date);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Concert, id: ");
        buffer.append(id);
        buffer.append(", title: ");
        buffer.append(title);
        buffer.append(", imageName: ");
        buffer.append(imageName);
        buffer.append(", blurb: {");
        buffer.append(blurb);
        buffer.append("}, dates: {");
        for (LocalDateTime d : dates) {
            buffer.append(d.toString());
            buffer.append(", ");
        }
        buffer.append("}, featuring: {");
        for (Performer p : performers) {
            buffer.append(p.getName());
            buffer.append(", ");
        }
        buffer.append("}");
        return buffer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        // Implement value-equality based on a Concert's title alone.
        if (!(obj instanceof Concert))
            return false;

        if (obj == this)
            return true;

        Concert con = (Concert) obj;
        return new EqualsBuilder().append(title, con.title).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(title).hashCode();
    }

    @Override
    public int compareTo(Concert concert) {
        return title.compareTo(concert.getTitle());
    }

}
