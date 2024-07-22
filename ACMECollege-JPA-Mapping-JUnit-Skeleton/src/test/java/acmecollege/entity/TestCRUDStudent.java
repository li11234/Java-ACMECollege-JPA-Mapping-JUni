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
//	 private static final String FIRST_NAME = "John";
//	 private static final String LAST_NAME = "Smith";
	 

	 
	@BeforeAll
	static void setupAllInit() {
//		student = new Student();
		
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
		
		long result = getTotalCount(em, Student.class);
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

		long result = getCountWithId(em, Student.class, Student_.id, student.getId());
		assertThat(result, is(greaterThanOrEqualTo(1L)));
	}

	@Test
	@Order(3)
	void test03_CreateInvalid() {
		et.begin();
		Student student2 = new Student();
//		student2.setFirstName("Mark");
		student2.setLastName("King");
		// We expect a failure because first name is part of the student 
		assertThrows(PersistenceException.class, () -> em.persist(student2));
		et.commit();
	}

	@Test
	@Order(4)
	void test04_Read() {

		List<Student> students =getAll(em, Student.class);

		assertThat(students, contains(equalTo(student)));
	}


	@Test
	@Order(5)
	void test05_Update() {
//		
		Student returnedStudent = getWithId(em, Student.class, Student_.id, student.getId());
		
		String newFirstName = "Hello";
		String newLastName = "Kitty";
		
		et.begin();
		returnedStudent.setFirstName(newFirstName);
		returnedStudent.setLastName(newLastName);
		em.merge(returnedStudent);
		et.commit();

		returnedStudent = getWithId(em, Student.class, Student_.id, student.getId());

		assertThat(returnedStudent.getFirstName(), equalTo(newFirstName));
		assertThat(returnedStudent.getLastName(), equalTo(newLastName));
	}

	@Test
	@Order(6)
	void test06_Delete() {
		Student returnedStudent = getWithId(em, Student.class, Student_.id, student.getId());
		
		et.begin();
		// Add another row to db to make sure only the correct row is deleted
		Student student2 = new Student();
		student2.setFirstName("Jude");
		student2.setLastName("Wooden");
		em.persist(student2);
		et.commit();

		et.begin();
		em.remove(returnedStudent);
		et.commit();	
		
		long result = getCountWithId(em, Student.class, Student_.id, student.getId());
		assertThat(result, is(equalTo(0L)));

		result = getCountWithId(em, Student.class, Student_.id, student2.getId());
		assertThat(result, is(equalTo(1L)));
	}
}


