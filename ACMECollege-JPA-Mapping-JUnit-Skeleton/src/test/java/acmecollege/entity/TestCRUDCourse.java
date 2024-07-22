package acmecollege.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
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
class TestCRUDCourse extends JUnitBase {
	private EntityManager em;
	private EntityTransaction et;
	
	private static Course course;
//	private static final String COURSE_CODE = "CST8277";
//	private static final String COURSE_TITLE = "JAVA LEARNING";
//	private static final int YEAR = 2024;
//	private static final String SEMESTER = "LEVEL 4";
//	private static final int CREDIT_UNITS = 1;
//	private static final Byte ONLINE = 1;
	
	 
	@BeforeAll
	static void setupAllInit() {
//		course = new Course();
		
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
		
		long result = getTotalCount(em, Course.class);
		assertThat(result, is(comparesEqualTo(0L)));	
		}
	
	@Test
	@Order(2)
	void test02_Create() {
		et.begin();
		course = new Course();
		course.setCourseCode("CST8277");
		course.setCourseTitle("JAVA LEARNING");
		course.setYear(2024);
		course.setSemester("L4");
		course.setCreditUnits(1);
		course.setOnline((byte)1);
		em.persist(course);
		et.commit();

		long result = getCountWithId(em, Course.class, Course_.id, course.getId());
		assertThat(result, is(greaterThanOrEqualTo(1L)));
	}

	@Test
	@Order(3)
	void test03_CreateInvalid() {
		et.begin();
		Course course2 = new Course();
//		student2.setFirstName("Mark");
		course2.setCourseTitle("King");
		// We expect a failure because first name is part of the student 
		assertThrows(PersistenceException.class, () -> em.persist(course2));
		et.commit();
	}

	@Test
	@Order(4)
	void test04_Read() {

		List<Course> courses =getAll(em, Course.class);

		assertThat(courses, contains(equalTo(course)));
	}


	@Test
	@Order(5)
	void test05_Update() {
//		
		Course returnedCourse = getWithId(em, Course.class, Course_.id, course.getId());
		
		String newCourseCode = "CST8002";
		String newCourseTitle = "JAVA002";
		
		et.begin();
		returnedCourse.setCourseCode(newCourseCode);
		returnedCourse.setCourseTitle(newCourseTitle);
		em.merge(returnedCourse);
		et.commit();

		returnedCourse = getWithId(em, Course.class, Course_.id, course.getId());

		assertThat(returnedCourse.getCourseCode(), equalTo(newCourseCode));
		assertThat(returnedCourse.getCourseTitle(), equalTo(newCourseTitle));
	}

	@Test
	@Order(6)
	void test06_Delete() {
		Course returnedStudent = getWithId(em, Course.class, Course_.id, course.getId());
		
		et.begin();
		// Add another row to db to make sure only the correct row is deleted
		Course course2 = new Course();
		course2.setCourseCode("CST1234");
		course2.setCourseTitle("TESTING");
		course2.setYear(2024);
		course2.setSemester("L4");
		course2.setCreditUnits(1);
		course2.setOnline((byte)1);
		em.persist(course2);
		et.commit();

		et.begin();
		em.remove(returnedStudent);
		et.commit();	
		
		long result = getCountWithId(em, Course.class, Course_.id, course.getId());
		assertThat(result, is(equalTo(0L)));

		result = getCountWithId(em, Course.class, Course_.id, course2.getId());
		assertThat(result, is(equalTo(1L)));
	}
}


