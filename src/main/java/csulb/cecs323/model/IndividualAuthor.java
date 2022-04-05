package csulb.cecs323.model;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "IndividualAuthor")
@DiscriminatorValue("IndividualAuthor")
public class IndividualAuthor extends Authoring_Entities {
	@ManyToMany(mappedBy = "teamMembers")
	private Set<AdHocTeam> teamMemberships = new HashSet<AdHocTeam>();

	public IndividualAuthor() {

	}

	public void addTeamMemberships(AdHocTeam team)
	{
		this.teamMemberships.add(team);
		team.getTeamMembers().add(this);
	}

	public Set<AdHocTeam> getTeamMemberships() {
		return teamMemberships;
	}

	public String getIndividual_authors_email()
	{
		return getEmail();
	}

	public void setIndividual_authors_email(String individual_authors_email)
	{
		setEmail(individual_authors_email);
	}

	public void setTeamMemberships(Set<AdHocTeam> teamMemberships) {
		this.teamMemberships = teamMemberships;
	}
}

