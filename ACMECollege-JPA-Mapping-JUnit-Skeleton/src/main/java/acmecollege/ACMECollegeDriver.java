/********************************************************************************************************2*4*w*
 * File:  ACMECollegeDriver.java Course materials CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author Mike Norman
 */
package acmecollege;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.entity.AcademicStudentClub;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.DurationAndStatus;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.NonAcademicStudentClub;
import acmecollege.entity.PeerTutor;
import acmecollege.entity.PeerTutorRegistration;
import acmecollege.entity.PeerTutorRegistrationPK;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

/**
 * Used as starting point of application to simply create the DB on server or refresh it if needs be.
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 */
public class ACMECollegeDriver {

	protected static final Logger LOG = LogManager.getLogger();

	public static final String PERSISTENCE_UNIT = "acmecollege-PU";

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		EntityManager em = emf.createEntityManager();
		// Two methods are just to check we can create and read for all tables.
		// They can be commented out if not needed.
		addSampleData(em);
		printData(em);
		cleanData(em);
		em.close();
		emf.close();
	}
	
	private static void cleanData(EntityManager em) {
		/* Clean up data:  Instead of removing the schema, remove all the entities
		 * 
		 * JPQL has a String-based DELETE query, but it gets complicated when an entity
		 * has a composite primary key, like PeerTutorRegistration with PeerTutorRegistrationPK.  The JPQL look like:
		 *   DELETE from PeerTutorRegistration ptr WHERE ptr.courseId = :x and ptr.studentId = y
		 * If the WHERE clause gets more complicated, ... not nice!
		 * 
		 * We use the CriteriaBuilder API ... it also is complicated, but the benefit
		 * is that once you figure it out, changing the query later doesn't increase
		 * the complexity
		 * 
		 */
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaDelete<PeerTutorRegistration> q1 = cb.createCriteriaDelete(PeerTutorRegistration.class);
		q1.from(PeerTutorRegistration.class);
		CriteriaDelete<Course> q2 = cb.createCriteriaDelete(Course.class);
		q2.from(Course.class);
		CriteriaDelete<PeerTutor> q3 = cb.createCriteriaDelete(PeerTutor.class);
		q3.from(PeerTutor.class);
		CriteriaDelete<MembershipCard> q4 = cb.createCriteriaDelete(MembershipCard.class);
		q4.from(MembershipCard.class);
		CriteriaDelete<ClubMembership> q5 = cb.createCriteriaDelete(ClubMembership.class);
		q5.from(ClubMembership.class);
		CriteriaDelete<StudentClub> q6 = cb.createCriteriaDelete(StudentClub.class);
		q6.from(StudentClub.class);
		CriteriaDelete<Student> q7 = cb.createCriteriaDelete(Student.class);
		q7.from(Student.class);

		EntityTransaction et = em.getTransaction();
		et.begin();
		em.createQuery(q1).executeUpdate();
		em.createQuery(q2).executeUpdate();
		em.createQuery(q3).executeUpdate();
		em.createQuery(q4).executeUpdate();
		em.createQuery(q5).executeUpdate();
		em.createQuery(q6).executeUpdate();
		em.createQuery(q7).executeUpdate();
		et.commit();
		
		em.close();
	}

	private static void printData(EntityManager em) {

		Student s = em.find(Student.class, 1);
		int sizeSPeerTutorRegistrations = s.getPeerTutorRegistrations().size();
		int sizeMembershipCards = s.getMembershipCards().size();

		MembershipCard mc = em.find(MembershipCard.class, 1);
		int ownerId = mc.getOwner().getId();
		int clubMembershipId = mc.getClubMembership().getId();

		ClubMembership cm = em.find(ClubMembership.class, 1);
		int clubId = cm.getStudentClub().getId();
		int cardId = cm.getCard().getId();

		StudentClub sc = em.find(StudentClub.class, 1);
		int sizeClubMemberships = sc.getClubMemberships().size();

		Course c = em.find(Course.class, 1);
		int sizeCPeerTutorRegistrations = c.getPeerTutorRegistrations().size();

		PeerTutor p = em.find(PeerTutor.class, 1);
		int sizePPeerTutorRegistrations = p.getPeerTutorRegistrations().size();

		PeerTutorRegistration ptr = em.find(PeerTutorRegistration.class, new PeerTutorRegistrationPK(1, 1));
		int studentCRId = ptr.getStudent().getId();
		int peerTutorCRId = ptr.getPeerTutor().getId();
		int courseCRId = ptr.getCourse().getId();

		LOG.info("Student.ID: {} has membership cards: {} and peer tutor registrations: {}", s.getId(), sizeMembershipCards, sizeSPeerTutorRegistrations);
		LOG.info("MembershipCard.ID: {} has owner.id: {} and clubMembership.id: {}", mc.getId(), ownerId, clubMembershipId);
		LOG.info("ClubMembership.ID: {} has club.id: {} and card.id: {}", cm.getId(), clubId, cardId);
		LOG.info("StudentClub.ID: {} has memberships: {}", sc.getId(), sizeClubMemberships);
		LOG.info("Course.ID: {} has peerTutorRegistrations: {}", c.getId(), sizeCPeerTutorRegistrations);
		LOG.info("PeerTutor.ID: {} has peerTutorRegistrations: {}", p.getId(), sizePPeerTutorRegistrations);
		LOG.info("CourseRegistration.ID: {} has student.id: {}, professor.id: {}, and course.id: {}", ptr.getId(), studentCRId, peerTutorCRId,
				courseCRId);
	}

	private static void addSampleData(EntityManager em) {
		EntityTransaction et = em.getTransaction();
		et.begin();

		StudentClub clubAcademic = new AcademicStudentClub();
		clubAcademic.setName("Computer Programming Club");
		em.persist(clubAcademic);

		StudentClub clubNonAcademic = new NonAcademicStudentClub();
		clubNonAcademic.setName("Student Hiking Club");
		em.persist(clubNonAcademic);

		Course course1 = new Course();
		course1.setCourse("CST8277", "Enterprise Application Programming", 2022, "AUTUMN", 3, (byte) 0);

		Course course2 = new Course();
		course2.setCourse("CST8284", "Object-Oriented Programming in Java", 2022, "SUMMER", 3, (byte) 1);

		PeerTutor peerTutor = new PeerTutor();
		peerTutor.setPeerTutor("Peter", "Schmidt", "Information and Communications Technology");

		Student s = new Student();
		s.setFullName("John", "Smith");

		DurationAndStatus ds = new DurationAndStatus();
		ds.setDurationAndStatus(LocalDateTime.of(2022, 8, 28, 0, 0), LocalDateTime.of(2023, 8, 27, 0, 0) , "+");

		DurationAndStatus ds2 = new DurationAndStatus();
		ds2.setDurationAndStatus(LocalDateTime.of(2021, 1, 1, 0, 0), LocalDateTime.of(2021, 12, 31, 0, 0) , "-");

		PeerTutorRegistration ptr1 = new PeerTutorRegistration();
		ptr1.setPeerTutor(peerTutor);
		ptr1.setCourse(course1);
		ptr1.setLetterGrade("A+");
		ptr1.setNumericGrade(100);
		ptr1.setStudent(s);
		em.persist(ptr1);

		PeerTutorRegistration ptr2 = new PeerTutorRegistration();
		ptr2.setStudent(s);
		ptr2.setCourse(course2);
		ptr2.setNumericGrade(85);
		ptr2.setStudent(s);
		em.persist(ptr2);

		ClubMembership membership = new ClubMembership();
		membership.setDurationAndStatus(ds);
		membership.setStudentClub(clubNonAcademic);
		em.persist(membership);

		ClubMembership membership2 = new ClubMembership();
		membership2.setDurationAndStatus(ds2);
		membership2.setStudentClub(clubAcademic);
		em.persist(membership2);

		MembershipCard card = new MembershipCard();
		card.setOwner(s);
		card.setSigned(true);
		card.setClubMembership(membership);
		em.persist(card);

		MembershipCard card2 = new MembershipCard();
		card2.setOwner(s);
		card2.setSigned(false);
		card2.setClubMembership(membership);
		em.persist(card2);

		MembershipCard card3 = new MembershipCard();
		card3.setOwner(s);
		card3.setSigned(true);
		card3.setClubMembership(membership2);
		em.persist(card3);

		et.commit();
	}

}
