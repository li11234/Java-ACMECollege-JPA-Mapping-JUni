package acmecollege.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2024-07-12T14:45:07.610-0400")
@StaticMetamodel(StudentClub.class)
public class StudentClub_ extends PojoBase_ {
	public static volatile SingularAttribute<StudentClub, String> name;
	public static volatile SetAttribute<StudentClub, ClubMembership> clubMemberships;
}
