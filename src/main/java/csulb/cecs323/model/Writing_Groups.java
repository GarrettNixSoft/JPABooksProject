package csulb.cecs323.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "WritingGroup")
@DiscriminatorValue("WritingGroup")
public class Writing_Groups extends Authoring_Entities {

	@Column(length = 80)
	private String headWriter;

	@Column
	private int yearFormed;
	
	// get method for the head writer of a writing group
	public String getHeadWriter() {
		return headWriter;
	}
	
	// get method for the year formed
	public int getYearFormed() {
		return yearFormed;
	}
	
	/*
	* set method for changing a head writer
	* @param headWriter - head writer of a group
	*/
	public void setHeadWriter(String headWriter) {
		this.headWriter = headWriter;
	}
	
	/*
	* set method for changing the year formed
	* @param yearFormed - year formed of the publisher.
	*/
	public void setYearFormed(int yearFormed) {
		this.yearFormed = yearFormed;
	}
}
