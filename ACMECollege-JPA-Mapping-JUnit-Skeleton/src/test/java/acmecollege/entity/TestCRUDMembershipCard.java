package acmecollege.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import common.JUnitBase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestCRUDMembershipCard extends JUnitBase{

	private EntityManager em;
	private EntityTransaction et;
	
	private static MembershipCard card;
	private static Student student;
//	private static ClubMembership clubMembership;
//	private static StudentClub studentClub;
//	private static DurationAndStatus duration;
	 
	@BeforeAll
	static void setupAllInit() {
		
		student = new Student();
		student.setFullName("John", "Smith");
		
//		StudentClub	studentClub = new AcademicStudentClub();
//		studentClub.setName("Java");
		
//		clubMembership = new ClubMembership();
//		clubMembership.setStudentClub(studentClub);
//		
//		DurationAndStatus duration = new DurationAndStatus();
//		duration.setDurationAndStatus(LocalDateTime.now(), LocalDateTime.now().plusDays(30), "1");
//		clubMembership.setDurationAndStatus(duration);
//		
		
	}

	@BeforeEach
	void setup() {
		em = getEntityManager();
		et = em.getTransaction();
	}
	
	@AfterEach
	void tearDown() {
		em.close();
	}



	@Test
	@Order(1)
	void test01_Empty() {
		
		long result = getTotalCount(em, MembershipCard.class);
		assertThat(result, is(comparesEqualTo(0L)));	
		}
	
	@Test
	@Order(2)
	void test02_Create() {
		et.begin();
		
//		student = new Student();
//	    student.setFullName("John", "Smith");
	    em.persist(student);
		card = new MembershipCard();
//		card.setClubMembership(clubMembership);
		card.setOwner(student);
		card.setSigned((byte)1);
		em.persist(card);
		
		et.commit();

		long result = getCountWithId(em, MembershipCard.class, MembershipCard_.id, card.getId());
		assertThat(result, is(greaterThanOrEqualTo(1L)));
	}

	@Test
	@Order(3)
	void test03_CreateInvalid() {
		et.begin();
		MembershipCard card2 = new MembershipCard();
//		student2.setFirstName("Mark");
//		card2.setSigned();
		// We expect a failure because first name is part of the student 
		assertThrows(PersistenceException.class, () -> em.persist(card2));
		et.commit();
	}

	@Test
	@Order(4)
	void test04_Read() {

		List<MembershipCard> cards =getAll(em, MembershipCard.class);

		assertThat(cards, contains(equalTo(card)));
	}


	@Test
	@Order(5)
	void test05_Update() {
//		
		MembershipCard returnedCard = getWithId(em, MembershipCard.class, MembershipCard_.id, card.getId());
		
		byte newSigned = (byte) 0;
		
		
		et.begin();
		returnedCard.setSigned(newSigned);
		em.merge(returnedCard);
		et.commit();

		returnedCard = getWithId(em, MembershipCard.class, MembershipCard_.id, card.getId());

		assertThat(returnedCard.getSigned(), equalTo(newSigned));
	}

	@Test
	@Order(6)
	void test06_Delete() {
		MembershipCard returnedCard = getWithId(em, MembershipCard.class, MembershipCard_.id, card.getId());
		
		et.begin();
		// Add another row to db to make sure only the correct row is deleted
		MembershipCard card2 = new MembershipCard();
		Student newStudent = new Student();
		newStudent.setFullName("Hello", "Kitty");
		em.persist(newStudent); 
		card2.setOwner(newStudent);
		card2.setClubMembership(returnedCard.getClubMembership());
		card2.setSigned((byte) 0 );
		
		em.persist(card2);
		et.commit();

		et.begin();
		em.remove(returnedCard);
		et.commit();	
		
		long result = getCountWithId(em, MembershipCard.class, MembershipCard_.id, card.getId());
		assertThat(result, is(equalTo(0L)));

		result = getCountWithId(em, MembershipCard.class, MembershipCard_.id, card2.getId());
		assertThat(result, is(equalTo(1L)));
	}
}
