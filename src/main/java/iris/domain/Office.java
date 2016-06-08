package iris.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Geometry;

import iris.domain.util.GeomToJsonSerializer;
import iris.domain.util.JsonToGeomDeserializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Office.
 */
@Entity
@Table(name = "office")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "office")
public class Office implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

	@Column(name = "geom", columnDefinition="geometry", nullable=true)
	@JsonSerialize(using=GeomToJsonSerializer.class)
	@JsonDeserialize(using=JsonToGeomDeserializer.class)
	private Geometry location;

    @Column(name = "type")
    private String type;

    @Column(name = "parent_office")
    private String parentOffice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Geometry getLocation() {
        return location;
    }

    public void setLocation(Geometry location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentOffice() {
        return parentOffice;
    }

    public void setParentOffice(String parentOffice) {
        this.parentOffice = parentOffice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Office office = (Office) o;
        if(office.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, office.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Office{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", location='" + location + "'" +
            ", type='" + type + "'" +
            ", parentOffice='" + parentOffice + "'" +
            '}';
    }
}
