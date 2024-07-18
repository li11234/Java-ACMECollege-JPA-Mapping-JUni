package acmecollege.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;

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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestCRUDStudent extends JUnitBase{
	 private EntityManager em;
	 private EntityTransaction et;

	 private static Student student;
	 
	@BeforeAll
	static void setupAllInit() {
		student = new Student();
		
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
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		// Select count(ptr) from Student ptr
		Root<Student> root = query.from(Student.class);
		query.select(builder.count(root));
		// Create query and set the parameter
		TypedQuery<Long> tq = em.createQuery(query);
		// Get the result as row count
		long result = tq.getSingleResult();

		assertThat(result, is(comparesEqualTo(0L)));	
		}
	
	@Test
	@Order(2)
	void test02_Create() {
		et.begin();
		student = new Student();
		student.setFirstName("John");
		student.setLastName("Smith");
		em.persist(student);
		et.commit();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		// Select count(ptr) from Student ptr where ptr.id = :id
		Root<Student> root = query.from(Student.class);
		query.select(builder.count(root));
		// Create query and set the parameter
		TypedQuery<Long> tq = em.createQuery(query);
		tq.setParameter("id", student.getId());
		// Get the result as row count
		long result = tq.getSingleResult();

		// There should only be one row in the DB
		assertThat(result, is(greaterThanOrEqualTo(1L)));
//		assertEquals(result, 1);
	}

	@Test
	@Order(3)
	void test03_CreateInvalid() {
		et.begin();
		Student student2 = new Student();
//		student2.setFirstName("Mark");
		student2.setLastName("Sam");
		// We expect a failure because first name is part of
		assertThrows(PersistenceException.class, () -> em.persist(student2));
		et.commit();
	}

	@Test
	@Order(4)
	void test04_Read() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Student
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		// Select ptr from Student ptr
		Root<Student> root = query.from(Student.class);
		query.select(root);
		// Create query and set the parameter
		TypedQuery<Student> tq = em.createQuery(query);
		// Get the result as row count
		List<Student> Students = tq.getResultList();

		assertThat(Students, contains(equalTo(student)));
	}

	@Test
	@Order(5)
	void test05_ReadDependencies() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Student
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		// Select ptr from Student ptr
		Root<Student> root = query.from(Student.class);
		query.select(root);
		// Create query and set the parameter
		TypedQuery<Student> tq = em.createQuery(query);
		tq.setParameter("id", student.getId());
		// Get the result as row count
		Student returnedStudent = tq.getSingleResult();

		assertThat(returnedStudent.getFirstName(), equalTo(""));
		assertThat(returnedStudent.getLastName(), equalTo(""));
		
	}

	@Test
	@Order(6)
	void test06_Update() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Student
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		// Select ptr from Student ptr
		Root<Student> root = query.from(Student.class);
		query.select(root);
		// Create query and set the parameter
		TypedQuery<Student> tq = em.createQuery(query);
		tq.setParameter("id", student.getId());
		// Get the result as row count
		Student returnedStudent = tq.getSingleResult();

		
		et.begin();
		returnedStudent.setFirstName("");
		returnedStudent.setLastName("");
		em.merge(returnedStudent);
		et.commit();

		returnedStudent = tq.getSingleResult();

		assertThat(returnedStudent.getFirstName(), equalTo(""));
		assertThat(returnedStudent.getLastName(), equalTo(""));
	}

	@Test
	@Order(7)
	void test07_UpdateDependencies() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Student
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		// Select ptr from Student ptr where ptr.id = :id
		Root<Student> root = query.from(Student.class);
		query.select(root);
		query.where(builder.equal(root.get(Student_.id), builder.parameter(StudentPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<Student> tq = em.createQuery(query);
		tq.setParameter("id", student.getId());
		// Get the result as row count
		Student returnedStudent = tq.getSingleResult();

		course = returnedStudent.getCourse();
		course.setCourse("CST8116", "Introduction to Computer Programming", 2021, "WINTER", 3, (byte) 0);

		peerTutor = returnedStudent.getPeerTutor();
		peerTutor.setPeerTutor("Charles", "Xavier", "Physics");

		student = returnedStudent.getStudent();
		student.setFullName("Jack", "Jackson");

		et.begin();
		returnedStudent.setPeerTutor(peerTutor);
		returnedStudent.setCourse(course);
		returnedStudent.setStudent(student);
		em.merge(returnedStudent);
		et.commit();

		returnedStudent = tq.getSingleResult();

		assertThat(returnedStudent.getStudent(), equalTo(student));
		assertThat(returnedStudent.getCourse(), equalTo(course));
		assertThat(returnedStudent.getPeerTutor(), equalTo(peerTutor));
	}

	@Test
	@Order(8)
	void test08_DeleteDependecy() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Student
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		// Select ptr from Student ptr
		Root<Student> root = query.from(Student.class);
		query.select(root);
		query.where(builder.equal( root.get(Student_.id), builder.parameter(StudentPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<Student> tq = em.createQuery(query);
		tq.setParameter("id", Student.getId());
		// Get the result as row count
		Student returnedStudent = tq.getSingleResult();

		int peerTutorId = returnedStudent.getPeerTutor().getId();

		et.begin();
		returnedStudent.setPeerTutor(null);
		em.merge(returnedStudent);
		et.commit();

		returnedStudent = tq.getSingleResult();

		assertThat(returnedStudent.getPeerTutor(), is(nullValue()));

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
	@Order(9)
	void test09_Delete() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Student
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		// Select ptr from Student ptr
		Root<Student> root = query.from(Student.class);
		query.select(root);
		query.where(builder.equal(root.get(Student_.id), builder.parameter(StudentPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<Student> tq = em.createQuery(query);
		tq.setParameter("id", Student.getId());
		// Get the result as row count
		Student returnedStudent = tq.getSingleResult();

		et.begin();
		// Add another row to db to make sure only the correct row is deleted
		Student Student2 = new Student();
		Student2.setCourse(new Course().setCourse("CST8288", "Design Patters in Java", 2022, "SPRING", 3, (byte) 0));
		Student2.setNumericGrade(99);
		Student2.setStudent(returnedStudent.getStudent());
		em.persist(Student2);
		et.commit();

		et.begin();
		em.remove(returnedStudent);
		et.commit();

		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query2 = builder.createQuery(Long.class);
		// Select count(pt) from PeerTutor pt where pt.id = :id
		Root<Student> root2 = query2.from(Student.class);
		query2.select(builder.count(root2));
		query2.where(builder.equal(root2.get(Student_.id), builder.parameter(StudentPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq2 = em.createQuery(query2);
		tq2.setParameter("id", returnedStudent.getId());
		// Get the result as row count
		long result = tq2.getSingleResult();
		assertThat(result, is(equalTo(0L)));

		// Create query and set the parameter
		TypedQuery<Long> tq3 = em.createQuery(query2);
		tq3.setParameter("id", Student2.getId());
		// Get the result as row count
		result = tq3.getSingleResult();
		assertThat(result, is(equalTo(1L)));
	}
}


