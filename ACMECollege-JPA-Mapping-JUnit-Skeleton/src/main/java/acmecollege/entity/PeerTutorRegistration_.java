package acmecollege.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2024-01-01T18:37:11.941-0500")
@StaticMetamodel(PeerTutorRegistration.class)
public class PeerTutorRegistration_ {
	public static volatile SingularAttribute<PeerTutorRegistration, PeerTutorRegistrationPK> id;
	public static volatile SingularAttribute<PeerTutorRegistration, Student> student;
	public static volatile SingularAttribute<PeerTutorRegistration, Course> course;
	public static volatile SingularAttribute<PeerTutorRegistration, PeerTutor> peerTutor;
	public static volatile SingularAttribute<PeerTutorRegistration, Integer> numericGrade;
	public static volatile SingularAttribute<PeerTutorRegistration, String> letterGrade;
}
