package acmecollege.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import common.JUnitBase;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestCRUDPeerTutorRegistrationOriginal extends JUnitBase {

	private EntityManager em;
	private EntityTransaction et;

	private static Course course;
	private static PeerTutor peerTutor;
	private static Student student;
	private static PeerTutorRegistration peerTutorRegistration;
	private static final String LETTER_GRADE = "A+";
	private static final int NUMERIC_GRADE = 100;

	
	@BeforeAll
	static void setupAllInit() {
		course = new Course();
		course.setCourse("CST8277", "Enterprise Application Programming", 2022, "AUTUMN", 3, (byte) 0);

		peerTutor = new PeerTutor();
		peerTutor.setPeerTutor("Peter", "Schmidt", "Information and Communications Technology");

		student = new Student();
		student.setFullName("John", "Smith");
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
	void test01_Empty() {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		// Select count(ptr) from PeerTutorRegistration ptr
		Root<PeerTutorRegistration> root = query.from(PeerTutorRegistration.class);
		query.select(builder.count(root));
		// Create query and set the parameter
		TypedQuery<Long> tq = em.createQuery(query);
		// Get the result as row count
		long result = tq.getSingleResult();

		assertThat(result, is(comparesEqualTo(0L)));

	}

	@Test
	void test02_Create() {
		et.begin();
		peerTutorRegistration = new PeerTutorRegistration();
		peerTutorRegistration.setPeerTutor(peerTutor);
		peerTutorRegistration.setCourse(course);
		peerTutorRegistration.setStudent(student);
		peerTutorRegistration.setLetterGrade("A+");
		peerTutorRegistration.setNumericGrade(100);
		em.persist(peerTutorRegistration);
		et.commit();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		// Select count(ptr) from PeerTutorRegistration ptr where ptr.id = :id
		Root<PeerTutorRegistration> root = query.from(PeerTutorRegistration.class);
		query.select(builder.count(root));
		query.where(builder.equal(root.get(PeerTutorRegistration_.id), builder.parameter(PeerTutorRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq = em.createQuery(query);
		tq.setParameter("id", peerTutorRegistration.getId());
		// Get the result as row count
		long result = tq.getSingleResult();

		// There should only be one row in the DB
		assertThat(result, is(greaterThanOrEqualTo(1L)));
//		assertEquals(result, 1);
	}

	@Test
	void test03_CreateInvalid() {
		et.begin();
		PeerTutorRegistration peerTutorRegistration2 = new PeerTutorRegistration();
		peerTutorRegistration2.setPeerTutor(peerTutor);
//		peerTutorRegistration2.setCourse(course);
		peerTutorRegistration2.setStudent(student);
		peerTutorRegistration2.setNumericGrade(85);
		peerTutorRegistration2.setLetterGrade("A");
		// We expect a failure because course is part of the composite key
		assertThrows(PersistenceException.class, () -> em.persist(peerTutorRegistration2));
		et.commit();
	}

	@Test
	void test04_Read() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for PeerTutorRegistration
		CriteriaQuery<PeerTutorRegistration> query = builder.createQuery(PeerTutorRegistration.class);
		// Select ptr from PeerTutorRegistration ptr
		Root<PeerTutorRegistration> root = query.from(PeerTutorRegistration.class);
		query.select(root);
		// Create query and set the parameter
		TypedQuery<PeerTutorRegistration> tq = em.createQuery(query);
		// Get the result as row count
		List<PeerTutorRegistration> peerTutorRegistrations = tq.getResultList();

		assertThat(peerTutorRegistrations, contains(equalTo(peerTutorRegistration)));
	}

	@Test
	void test05_ReadDependencies() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for PeerTutorRegistration
		CriteriaQuery<PeerTutorRegistration> query = builder.createQuery(PeerTutorRegistration.class);
		// Select ptr from PeerTutorRegistration ptr
		Root<PeerTutorRegistration> root = query.from(PeerTutorRegistration.class);
		query.select(root);
		query.where(builder.equal(root.get(PeerTutorRegistration_.id), builder.parameter(PeerTutorRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<PeerTutorRegistration> tq = em.createQuery(query);
		tq.setParameter("id", peerTutorRegistration.getId());
		// Get the result as row count
		PeerTutorRegistration returnedPeerTutorRegistration = tq.getSingleResult();

		assertThat(returnedPeerTutorRegistration.getStudent(), equalTo(student));
		assertThat(returnedPeerTutorRegistration.getLetterGrade(), equalTo(LETTER_GRADE));
		assertThat(returnedPeerTutorRegistration.getNumericGrade(), equalTo(NUMERIC_GRADE));
		assertThat(returnedPeerTutorRegistration.getCourse(), equalTo(course));
		assertThat(returnedPeerTutorRegistration.getPeerTutor(), equalTo(peerTutor));
	}

	@Test
	void test06_Update() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for PeerTutorRegistration
		CriteriaQuery<PeerTutorRegistration> query = builder.createQuery(PeerTutorRegistration.class);
		// Select ptr from PeerTutorRegistration ptr
		Root<PeerTutorRegistration> root = query.from(PeerTutorRegistration.class);
		query.select(root);
		query.where(builder.equal(root.get(PeerTutorRegistration_.id), builder.parameter(PeerTutorRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<PeerTutorRegistration> tq = em.createQuery(query);
		tq.setParameter("id", peerTutorRegistration.getId());
		// Get the result as row count
		PeerTutorRegistration returnedPeerTutorRegistration = tq.getSingleResult();

		String newLetterGrade = "A";
		int newNumericGrade = 85;

		et.begin();
		returnedPeerTutorRegistration.setLetterGrade(newLetterGrade);
		returnedPeerTutorRegistration.setNumericGrade(newNumericGrade);
		em.merge(returnedPeerTutorRegistration);
		et.commit();

		returnedPeerTutorRegistration = tq.getSingleResult();

		assertThat(returnedPeerTutorRegistration.getLetterGrade(), equalTo(newLetterGrade));
		assertThat(returnedPeerTutorRegistration.getNumericGrade(), equalTo(newNumericGrade));
	}

	@Test
	void test07_UpdateDependencies() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for PeerTutorRegistration
		CriteriaQuery<PeerTutorRegistration> query = builder.createQuery(PeerTutorRegistration.class);
		// Select ptr from PeerTutorRegistration ptr where ptr.id = :id
		Root<PeerTutorRegistration> root = query.from(PeerTutorRegistration.class);
		query.select(root);
		query.where(builder.equal(root.get(PeerTutorRegistration_.id), builder.parameter(PeerTutorRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<PeerTutorRegistration> tq = em.createQuery(query);
		tq.setParameter("id", peerTutorRegistration.getId());
		// Get the result as row count
		PeerTutorRegistration returnedPeerTutorRegistration = tq.getSingleResult();

		course = returnedPeerTutorRegistration.getCourse();
		course.setCourse("CST8116", "Introduction to Computer Programming", 2021, "WINTER", 3, (byte) 0);

		peerTutor = returnedPeerTutorRegistration.getPeerTutor();
		peerTutor.setPeerTutor("Charles", "Xavier", "Physics");

		student = returnedPeerTutorRegistration.getStudent();
		student.setFullName("Jack", "Jackson");

		et.begin();
		returnedPeerTutorRegistration.setPeerTutor(peerTutor);
		returnedPeerTutorRegistration.setCourse(course);
		returnedPeerTutorRegistration.setStudent(student);
		em.merge(returnedPeerTutorRegistration);
		et.commit();

		returnedPeerTutorRegistration = tq.getSingleResult();

		assertThat(returnedPeerTutorRegistration.getStudent(), equalTo(student));
		assertThat(returnedPeerTutorRegistration.getCourse(), equalTo(course));
		assertThat(returnedPeerTutorRegistration.getPeerTutor(), equalTo(peerTutor));
	}

	@Test
	void test08_DeleteDependecy() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for PeerTutorRegistration
		CriteriaQuery<PeerTutorRegistration> query = builder.createQuery(PeerTutorRegistration.class);
		// Select ptr from PeerTutorRegistration ptr
		Root<PeerTutorRegistration> root = query.from(PeerTutorRegistration.class);
		query.select(root);
		query.where(builder.equal( root.get(PeerTutorRegistration_.id), builder.parameter(PeerTutorRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<PeerTutorRegistration> tq = em.createQuery(query);
		tq.setParameter("id", peerTutorRegistration.getId());
		// Get the result as row count
		PeerTutorRegistration returnedPeerTutorRegistration = tq.getSingleResult();

		int peerTutorId = returnedPeerTutorRegistration.getPeerTutor().getId();

		et.begin();
		returnedPeerTutorRegistration.setPeerTutor(null);
		em.merge(returnedPeerTutorRegistration);
		et.commit();

		returnedPeerTutorRegistration = tq.getSingleResult();

		assertThat(returnedPeerTutorRegistration.getPeerTutor(), is(nullValue()));

		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query2 = builder.createQuery(Long.class);
		// Select count(pt) from PeerTutor pt where pt.id = :id
		Root<PeerTutor> root2 = query2.from(PeerTutor.class);
		query2.select(builder.count(root2));
		query2.where(builder.equal(root2.get(PeerTutor_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq2 = em.createQuery(query2);
		tq2.setParameter("id", peerTutorId);
		// Get the result as row count
		long result = tq2.getSingleResult();
		// Because it can be null so it is not removed
		assertThat(result, is(equalTo(1L)));
	}

	@Test
	void test09_Delete() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for PeerTutorRegistration
		CriteriaQuery<PeerTutorRegistration> query = builder.createQuery(PeerTutorRegistration.class);
		// Select ptr from PeerTutorRegistration ptr
		Root<PeerTutorRegistration> root = query.from(PeerTutorRegistration.class);
		query.select(root);
		query.where(builder.equal(root.get(PeerTutorRegistration_.id), builder.parameter(PeerTutorRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<PeerTutorRegistration> tq = em.createQuery(query);
		tq.setParameter("id", peerTutorRegistration.getId());
		// Get the result as row count
		PeerTutorRegistration returnedPeerTutorRegistration = tq.getSingleResult();

		et.begin();
		// Add another row to db to make sure only the correct row is deleted
		PeerTutorRegistration peerTutorRegistration2 = new PeerTutorRegistration();
		peerTutorRegistration2.setCourse(new Course().setCourse("CST8288", "Design Patters in Java", 2022, "SPRING", 3, (byte) 0));
		peerTutorRegistration2.setNumericGrade(99);
		peerTutorRegistration2.setStudent(returnedPeerTutorRegistration.getStudent());
		em.persist(peerTutorRegistration2);
		et.commit();

		et.begin();
		em.remove(returnedPeerTutorRegistration);
		et.commit();

		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query2 = builder.createQuery(Long.class);
		// Select count(pt) from PeerTutor pt where pt.id = :id
		Root<PeerTutorRegistration> root2 = query2.from(PeerTutorRegistration.class);
		query2.select(builder.count(root2));
		query2.where(builder.equal(root2.get(PeerTutorRegistration_.id), builder.parameter(PeerTutorRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq2 = em.createQuery(query2);
		tq2.setParameter("id", returnedPeerTutorRegistration.getId());
		// Get the result as row count
		long result = tq2.getSingleResult();
		assertThat(result, is(equalTo(0L)));

		// Create query and set the parameter
		TypedQuery<Long> tq3 = em.createQuery(query2);
		tq3.setParameter("id", peerTutorRegistration2.getId());
		// Get the result as row count
		result = tq3.getSingleResult();
		assertThat(result, is(equalTo(1L)));
	}
}

