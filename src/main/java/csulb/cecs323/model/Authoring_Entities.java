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
		resultClass = Individual_Author.class
)
@NamedNativeQuery(
		name="ReturnAllAdHocTeams",
		query = "SELECT * " +
				"FROM   AUTHORING_ENTITIES " +
				"WHERE  AUTHORING_ENTITY_TYPE = 'AdHocTeam'",
		resultClass = AdHocTeam.class
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
	
	// Empty Authoring Entity constructor that initializes a name and email.
	public Authoring_Entities() {
		this.name = "";
		this.email = "";
	}
	
	/*
	* Overloaded Authoring Entity constructor that initializes a name and email from the parameter.
	* @param name name of the Authoring Entity
	* @param email email of the Authoring Entity
	*/
	public Authoring_Entities(String name, String email) {
		this.name = name;
		this.email = email;
	}
	
	// Get method for the name
	public String getName() {
		return name;
	}
	
	// Get method for the email.
	public String getEmail() {
		return email;
	}
	
	// Get method for the work list
	public Set<Books> getWorks() {
		return works;
	}
	
	/*
	* Set method that initializes a name with the parameter
	* @param name  name of the authoring entity.
	*/
	public void setName(String name) {
		this.name = name;
	}
	
	/*
	* Set method that initializes an email with the parameter
	* @param email  email of the authoring entity.
	*/
	public void setEmail(String email) {
		this.email = email;
	}

	/*
	* Set method that initializes a work List with the parameter
	* @param works  List of books of the authoring entity.
	*/
	public void setWorks(Set<Books> works) {
		this.works = works;
	}
}
