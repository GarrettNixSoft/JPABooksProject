import javax.persistence.*;

import java.util.Set;

@Entity(name = "IndividualAuthor")
@DiscriminatorValue("IndividualAuthor")
public class IndividualAuthor extends Authoring_Entities {
	@ManyToMany(mappedBy = "teamMembers")
	private Set<AdHocTeam> teamMemberships;

	private String individual_authors_email;

	public IndividualAuthor() { }

	public Set<AdHocTeam> getTeamMemberships() {
		return teamMemberships;
	}

	public String getIndividual_authors_email()
	{
		return individual_authors_email;
	}

	public void setIndividual_authors_email(String individual_authors_email)
	{
		this.individual_authors_email = individual_authors_email;
	}

	public void setTeamMemberships(Set<AdHocTeam> teamMemberships) {
		this.teamMemberships = teamMemberships;
	}
}

