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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import common.JUnitBase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestCRUDPeerTutor extends JUnitBase{
	private EntityManager em;
	private EntityTransaction et;
	
	private static PeerTutor peer;

	 
	@BeforeAll
	static void setupAllInit() {
//		peer = new PeerTutor();
		
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
		
		long result = getTotalCount(em, PeerTutor.class);
		assertThat(result, is(comparesEqualTo(0L)));	
		}
	
	@Test
	@Order(2)
	void test02_Create() {
		et.begin();
		peer = new PeerTutor();
		peer.setPeerTutor("Winnie", "Pooh", "Honey Pot");
		em.persist(peer);
		et.commit();

		long result = getCountWithId(em, PeerTutor.class, PeerTutor_.id, peer.getId());
		assertThat(result, is(greaterThanOrEqualTo(1L)));
	}

	@Test
	@Order(3)
	void test03_CreateInvalid() {
		et.begin();
		PeerTutor peer2 = new PeerTutor();
//		student2.setFirstName("Mark");
		peer2.setFirstName("King");
		// We expect a failure because first name is part of the student 
		assertThrows(PersistenceException.class, () -> em.persist(peer2));
		et.commit();
	}

	@Test
	@Order(4)
	void test04_Read() {

		List<PeerTutor> peers =getAll(em, PeerTutor.class);

		assertThat(peers, contains(equalTo(peer)));
	}


	@Test
	@Order(5)
	void test05_Update() {
//		
		PeerTutor returnedPeer = getWithId(em, PeerTutor.class, PeerTutor_.id, peer.getId());
		
		String newFirstName = "Kanga";
		String newLastName = "Roo";
		String newProgram= "Honey Cook";

		
		et.begin();
		returnedPeer.setPeerTutor(newFirstName, newLastName, newProgram);
		
		em.merge(returnedPeer);
		et.commit();

		returnedPeer = getWithId(em, PeerTutor.class, PeerTutor_.id, peer.getId());

		assertThat(returnedPeer.getFirstName(), equalTo(newFirstName));
		assertThat(returnedPeer.getLastName(), equalTo(newLastName));
		assertThat(returnedPeer.getProgram(), equalTo(newProgram));

	}

	@Test
	@Order(6)
	void test06_Delete() {
		PeerTutor returnedPeer = getWithId(em, PeerTutor.class, PeerTutor_.id, peer.getId());
		
		et.begin();
		// Add another row to db to make sure only the correct row is deleted
		PeerTutor peer2 = new PeerTutor();
		peer2.setPeerTutor("Jelly", "Fish", "Swiming");
		em.persist(peer2);
		et.commit();

		et.begin();
		em.remove(returnedPeer);
		et.commit();	
		
		long result = getCountWithId(em, PeerTutor.class, PeerTutor_.id, peer.getId());
		assertThat(result, is(equalTo(0L)));

		result = getCountWithId(em, PeerTutor.class, PeerTutor_.id, peer2.getId());
		assertThat(result, is(equalTo(1L)));
	}
}
