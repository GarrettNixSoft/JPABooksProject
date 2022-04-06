package csulb.cecs323.model;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "IndividualAuthor")
@DiscriminatorValue("IndividualAuthor")
public class IndividualAuthor extends Authoring_Entities {
	@ManyToMany(mappedBy = "teamMembers")
	private Set<AdHocTeam> teamMemberships = new HashSet<AdHocTeam>();
	
	// empty constructor
	public IndividualAuthor() {}
	
	/*
	* Adds an ad hoc team to the memberships that an individual author is involved in.
	* @param team - Ad Hoc Team object
	*/
	public void addTeamMemberships(AdHocTeam team)
	{
		this.teamMemberships.add(team);
		team.getTeamMembers().add(this);
	}
	
	// get method for the teamMemberships
	public Set<AdHocTeam> getTeamMemberships() {
		return teamMemberships;
	}
	
	// get method for the individual author email
	public String getIndividual_authors_email()
	{
		return getEmail();
	}
	
	/*
	* set method for changing an individual author email
	* @param email - email of the individual author
	*/	
	public void setIndividual_authors_email(String individual_authors_email)
	{
		setEmail(individual_authors_email);
	}
	
	/*
	* set method for changing a team membership
	* @param teamMemberships - a list of the ad hoc teams an individual author is involved in
	*/
	public void setTeamMemberships(Set<AdHocTeam> teamMemberships) {
		this.teamMemberships = teamMemberships;
	}
}

