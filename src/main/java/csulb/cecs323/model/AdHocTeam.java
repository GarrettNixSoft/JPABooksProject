package csulb.cecs323.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "AdHocTeam")
@DiscriminatorValue("AdHocTeam")
public class AdHocTeam extends Authoring_Entities {

	@ManyToMany
	@JoinTable(
			name = "AD_HOC_TEAMS_MEMBER",
			joinColumns = @JoinColumn(name = "AD_HOC_TEAMS_EMAIL"),
			inverseJoinColumns = @JoinColumn(name = "INDIVIDUAL_AUTHORS_EMAIL")
	)
	private Set<IndividualAuthor> teamMembers = new HashSet<IndividualAuthor>();

	public AdHocTeam() { }

	public String getAd_hoc_teams_email()
	{
		return getEmail();
	}

	public void setAd_hoc_teams_email(String ad_hoc_teams_email)
	{
		setEmail(ad_hoc_teams_email);

	}

	public Set<IndividualAuthor> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(Set<IndividualAuthor> authors)
	{
		teamMembers = authors;
	}

	public void addTeamMembers(IndividualAuthor authors)
	{
		this.teamMembers.add(authors);
		authors.getTeamMemberships().add(this);
	}

}
