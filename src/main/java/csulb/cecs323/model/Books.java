package csulb.cecs323.model;

import javax.persistence.*;

@Entity(name = "Books")
@NamedNativeQuery(
		name="ReturnBooks",
		query=	"SELECT * " +
				"FROM BOOKS " +
				"WHERE ISBN = ? ",
		resultClass = Books.class
)
@NamedNativeQuery(
		name="ReturnAllBooks",
		query = "SELECT * " +
				"FROM   BOOKS ",
		resultClass = Books.class
)
public class Books {

	@Id
	@Column(nullable = false, length = 17)
	private String ISBN;

	@Column(nullable = false, length = 80)
	private String title;

	@Column(name = "YEAR_PUBLISHED", nullable = false)
	private int yearPublished;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AUTHORING_ENTITY_NAME", nullable = false)
	private Authoring_Entities author;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PUBLISHER_NAME", nullable = false)
	private Publishers publisher;
	
	// Empty book constructor
	public Books() {}
	
	// get method for the ISBN
	public String getISBN() {
		return ISBN;
	}
	
	// get method for the title
	public String getTitle() {
		return title;
	}
	
	// get method for year published
	public int getYearPublished() {
		return yearPublished;
	}
	
	// get method for an author of an authoring entity
	public Authoring_Entities getAuthor() {
		return author;
	}
	
	// get method for a publisher
	public Publishers getPublisher() {
		return publisher;
	}
	
	/*
	* set method for changing an ISBN
	* @param ISBN - book serial number
	*/
	public void setISBN(String isbn) {
		this.ISBN = isbn;
	}
	
	/*
	* set method for changing an title
	* @param title - title of a book
	*/
	public void setTitle(String title) {
		this.title = title;
	}	
	
	/*
	* set method for changing the year published
	* @param yearPublished - publish  year
	*/
	public void setYearPublished(int yearPublished) {
		this.yearPublished = yearPublished;
	}
	
	/*
	* set method for changing an author
	* @param author - authoring entity object
	*/
	public void setAuthor(Authoring_Entities author) {
		this.author = author;
	}
	
	/*
	* set method for changing a publisher
	* @param publisher - book publisher
	*/
	public void setPublisher(Publishers publisher) {
		this.publisher = publisher;
	}
}
