package csulb.cecs323.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "AUTHORING_ENTITY_TYPE")
@NamedNativeQuery(
		name="ReturnAuthor",
		query=	"SELECT * " +
				"FROM AUTHORING_ENTITIES " +
				"WHERE NAME = ? ",
		resultClass = Authoring_Entities.class
)
@NamedNativeQuery(
		name="ReturnAllAuthors",
		query = "SELECT * " +
				"FROM   AUTHORING_ENTITIES ",
		resultClass = Authoring_Entities.class
)
@NamedNativeQuery(
		name="ReturnAllWritingGroups",
		query = "SELECT * " +
				"FROM   AUTHORING_ENTITIES " +
				"WHERE  AUTHORING_ENTITY_TYPE = 'WritingGroup'",
		resultClass = Writing_Groups.class
)
@NamedNativeQuery(
		name="ReturnAllIndividualAuthors",
		query = "SELECT * " +
				"FROM   AUTHORING_ENTITIES " +
				"WHERE  AUTHORING_ENTITY_TYPE = 'IndividualAuthor'",
		resultClass = Writing_Groups.class
)
@NamedNativeQuery(
		name="ReturnAllAdHocTeams",
		query = "SELECT * " +
				"FROM   AUTHORING_ENTITIES " +
				"WHERE  AUTHORING_ENTITY_TYPE = 'AdHocTeam'",
		resultClass = Writing_Groups.class
)
public abstract class Authoring_Entities {

	@Column(nullable = false, length = 80)
	private String name;

	@Id
	@Column(nullable = false, length = 30)
	private String email;

	@OneToMany
	@JoinColumn(name = "AUTHORING_ENTITY_NAME")
	private Set<Books> works;

	public Authoring_Entities() {
		this.name = "";
		this.email = "";
	}

	public Authoring_Entities(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public Set<Books> getWorks() {
		return works;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setWorks(Set<Books> works) {
		this.works = works;
	}
}
