package csulb.cecs323.model;
import javax.persistence.*;

@Entity(name = "Publishers")
@NamedNativeQuery(
	name="ReturnPublisher",
	query=	"SELECT * " +
			"FROM PUBLISHERS " +
			"WHERE NAME = ? ",
	resultClass = Publishers.class
)
@NamedNativeQuery(
		name="ReturnAllPublishers",
		query = "SELECT * " +
				"FROM   PUBLISHERS ",
		resultClass = Publishers.class
)
public class Publishers {

	@Id
	@Column(nullable = false, length = 80)
	private String name;

	@Column(nullable = false, length = 80, unique = true)
	private String email;

	@Column(nullable = false, length = 24, unique = true)
	private String phone;
	
	// Empty constructor
	public Publishers() {}
	
	/* 
	* Overloaded constructor to initialize a publisher object
	* @param name - name of the publisher
	* @param email - email of the publisher
	* @param phone - phone number of the publisher
	*
	*/
	public Publishers(String name, String email, String phone) {
		this.name = name;
		this.email = email;
		this.phone = phone;
	}
	
	// get method for the name
	public String getName()
	{
		return this.name;
	}
	
	/*
	* set method for changing a publisher name
	* @param name - name of the publisher
	*/
	public void setName(String name)
	{
		this.name = name;
	}
	
	// get method for the email
	public String getEmail()
	{
		return this.email;
	}
	
	/*
	* set method for changing an email of a publisher 
	* @param ISBN - book serial number
	*/
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	// get method for the phone
	public String getPhone()
	{
		return this.phone;
	}
	
	/*
	* set method for changing a phone number
	* @param phone - phone number of the Publisher
	*/
	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "Publishers - Name: " + this.name + " Email: " + this.email + " Phone: " + this.phone;
	}



}
