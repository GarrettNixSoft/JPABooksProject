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
	
	// Empty Constructor
	public AdHocTeam() { }
	
	// Get method for ad hoc team email.
	public String getAd_hoc_teams_email()
	{
		return getEmail();
	}
	
	// Set method for ad hoc team email
	public void setAd_hoc_teams_email(String ad_hoc_teams_email)
	{
		setEmail(ad_hoc_teams_email);

	}
	
	// Get method for teamMembers list.
	public Set<IndividualAuthor> getTeamMembers() {
		return teamMembers;
	}
	
	// Set method for teamMembers list.
	public void setTeamMembers(Set<IndividualAuthor> authors)
	{
		teamMembers = authors;
	}
	
	/*
	* Adds authors to the teamMembers list and adds the Ad Hoc Team object itself to the author's list.
	* @param authors an Individual Author object.
	*/
	public void addTeamMembers(IndividualAuthor authors)
	{
		this.teamMembers.add(authors);
		authors.getTeamMemberships().add(this);
	}

}
