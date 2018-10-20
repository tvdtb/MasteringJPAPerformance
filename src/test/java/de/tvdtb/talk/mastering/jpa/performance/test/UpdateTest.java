package de.tvdtb.talk.mastering.jpa.performance.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import de.tvdtb.talk.mastering.jpa.performance.PersistenceTest;
import de.tvdtb.talk.mastering.jpa.performance.model.City;
import de.tvdtb.talk.mastering.jpa.performance.model.NamedEntity;
import de.tvdtb.talk.mastering.jpa.performance.model.State;
import de.tvdtb.talk.mastering.jpa.performance.model.TestEntity;

public class UpdateTest extends PersistenceTest {

	/**
	 * This test uses an entity with Property-access to show Hibernate accessing the
	 * objects'data
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdate() throws Exception {
		EntityManager em = getEntityManager();

		NamedEntity entity = em.find(NamedEntity.class, 1L);
		assertEquals(0, NamedEntity.getNameCounter.getAndSet(0), "no invocations by hibernate");
		assertEquals(1, getStatistics().getEntityLoadCount(), "one load up to here");
		System.out.println("Found " + entity.getName() + " ---------------------------");
		assertEquals(1, NamedEntity.getNameCounter.getAndSet(0), "1 invocation by sysout");

		System.out.println("Find ... ----------------------");
		em.find(TestEntity.class, 2L);
		assertEquals(0, NamedEntity.getNameCounter.getAndSet(0), "no changes by em.find");
		assertEquals(2, getStatistics().getEntityLoadCount(), "one more load");

		System.out.println("Query ... ----------------------");
		em.createQuery("SELECT e FROM TestEntity e WHERE e.id=3").getResultList();
		assertEquals(2, NamedEntity.getNameCounter.getAndSet(0), "query causes flush, 2 invocations");

		entity.setName("Test 1");
		System.out.println("Query ... ----------------------");
		em.createQuery("SELECT e FROM TestEntity e WHERE e.id=3").getResultList();
		assertEquals(2, NamedEntity.getNameCounter.getAndSet(0), "query causes flush, 2 invocations");

		System.out.println("setName ... ----------------------");
		entity.setName("Test 2");
		System.out.println("Flush ... ----------------------");
		em.flush();
		assertEquals(2, NamedEntity.getNameCounter.getAndSet(0), "explicit flush, 2 invocations");
	}

	/**
	 * Update a  @ManyToOne key with a reference
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateByReference() throws Exception {
		EntityManager em = getEntityManager();

		State newState = em.getReference(State.class, 1005L);
		assertEquals(0, getStatistics().getEntityLoadCount(), "no load yet");
		assertEquals(0, getStatistics().getEntityLoadCount(), "no fetch yet");

		City city = em.find(City.class, 1001L);
		city.setState(newState); // the new state is set without being fetched from the database

		em.flush(); // be sure the update statement happened

		assertEquals(0, getStatistics().getEntityFetchCount(), "no fetch");
		assertEquals(2, getStatistics().getEntityLoadCount(), "only city & state loaded");
	}
}
