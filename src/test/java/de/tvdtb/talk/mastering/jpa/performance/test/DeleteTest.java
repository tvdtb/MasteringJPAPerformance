package de.tvdtb.talk.mastering.jpa.performance.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.tvdtb.talk.mastering.jpa.performance.PersistenceTest;
import de.tvdtb.talk.mastering.jpa.performance.model.TestEntity;

public class DeleteTest extends PersistenceTest {

	/**
	 * Test entity deletion by finding the entity first - normal transaction
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteNormal() throws Exception {
		EntityManager em = getEntityManager();

		TestEntity entity = em.find(TestEntity.class, 1L);
		em.remove(entity);
		System.out.println("removed, flush ...");
		em.flush();
		System.out.println("flushed");
		assertEquals(1, getStatistics().getEntityDeleteCount(), "should delete 1 entity");
		assertEquals(1, getStatistics().getEntityLoadCount(), "should be 1 load");
	}

	/**
	 * Test entity deletion by getReference -- current implementation still selects
	 * the entity
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteByReference() throws Exception {
		EntityManager em = getEntityManager();

		TestEntity entity = em.getReference(TestEntity.class, 1L);
		System.out.println("reference id=" + entity.getId() + " remove ...");
		em.remove(entity);
		System.out.println("removed, flush ...");
		em.flush();
		System.out.println("flushed");
		assertEquals(1, getStatistics().getEntityDeleteCount(), "should delete 1 entity");
		assertEquals(1, getStatistics().getEntityFetchCount(),
				"should be 0, delete currently requires fetch of reference");
	}

	/**
	 * Deleting a transient entity throws an exception
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteTransientInstance() throws Exception {
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> { 
			EntityManager em = getEntityManager();
	
			TestEntity entity = new TestEntity();
			entity.setId(1L);
			em.remove(entity);
			assertEquals(1, getStatistics().getEntityDeleteCount(), "should delete 1 entity");
			assertEquals(1, getStatistics().getEntityFetchCount(),
					"should be 0, delete currently requires fetch of reference");
		});
	}

	/**
	 * Delete entity by query - optimized version
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteByQuery() throws Exception {
		EntityManager em = getEntityManager();

		em.createQuery("DELETE from TestEntity e WHERE e.id=:id") //
				.setParameter("id", 1L)//
				.executeUpdate();

		assertEquals(0, getStatistics().getEntityDeleteCount(), "dont't know about the query result");
		assertEquals(0, getStatistics().getEntityFetchCount(),
				"no fetches");
	}

}
