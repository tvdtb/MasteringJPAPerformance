package de.tvdtb.talk.mastering.jpa.performance.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import de.tvdtb.talk.mastering.jpa.performance.PersistenceTest;
import de.tvdtb.talk.mastering.jpa.performance.model.CachedEntity;
import de.tvdtb.talk.mastering.jpa.performance.model.City;
import de.tvdtb.talk.mastering.jpa.performance.model.PostalCode;
import de.tvdtb.talk.mastering.jpa.performance.model.State;
import de.tvdtb.talk.mastering.jpa.performance.model.TestEntity;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class ReadTest extends PersistenceTest {

	static List<String> codes = Arrays.asList( //
			"1067", "1945", "4600", "6108", "10115" //
			, "17033", "19273", "20095", "21465", "27568" //
			, "32049", "34117", "51598", "63739", "64754"//
			, "66111");

	@Test
	public void testReadSimple() throws Exception {
		EntityManager em = getEntityManager();
		City c = em.find(City.class, 1001L);
	}
	
	@Test
	public void testReadPostalCodes() throws Exception {
		EntityManager em = getEntityManager();

		List<PostalCode> resultList = em//
				.createNamedQuery("PostalCode.byCodes", PostalCode.class) //
				.setParameter("codes", codes) //
				.getResultList();

		System.out.println("codes is " + codes.size() + " items, query result is " + resultList.size());
		// Normal version: too many queries (16 fetches)
		assertEquals(1, getStatistics().getQueryExecutionCount(), getStatistics().toString());
		assertEquals(48, getStatistics().getEntityLoadCount(), getStatistics().toString());
		assertEquals(16, getStatistics().getEntityFetchCount(), getStatistics().toString());
	}

	@Test
	public void testReadPostalCodesOptimized() throws Exception {
		EntityManager em = getEntityManager();

		em.createNamedQuery("PostalCode.byCodesOptimized.prefetch", State.class) //
				.setParameter("codes", codes) //
				.getResultList();
		List<PostalCode> resultList = em//
				.createNamedQuery("PostalCode.byCodesOptimized", PostalCode.class) //
				.setParameter("codes", codes) //
				.getResultList();

		System.out.println("codes is " + codes.size() + " items, query result is " + resultList.size());
		// Optimized version: 2 queries, 48 entities, no fetches!
		assertEquals(2, getStatistics().getQueryExecutionCount(), getStatistics().toString());
		assertEquals(48, getStatistics().getEntityLoadCount(), getStatistics().toString());
		assertEquals(0, getStatistics().getEntityFetchCount(), getStatistics().toString());
	}

	@Test
	public void testReadReference() throws Exception {
		EntityManager em = getEntityManager();

		System.out.println(">getReference");
		TestEntity testEntity = em.getReference(TestEntity.class, 1L);
		System.out.println("----");
		// no SQL until here
		assertEquals(0, getStatistics().getEntityFetchCount());

		// no SQL until here
		System.out.println("id=" + testEntity.getId());
		assertEquals(0, getStatistics().getEntityFetchCount());

		// any other method than getId() causes the fetch SQL
		System.out.println("value=" + testEntity.doSomething());
		assertEquals(1, getStatistics().getEntityFetchCount() //
				, "TODO fails if bytecode instrumentation active and enableLazyInitialization=true in pom.xml\"");
	}

	@Test
	public void testReadLazyAttribute() throws Exception {
		EntityManager em = getEntityManager();

		TestEntity testEntity = em.find(TestEntity.class, 1L);
		System.out.println("id=" + testEntity.getId());
		assertEquals(1, getStatistics().getEntityLoadCount());
		assertEquals(1, getStatistics().getPrepareStatementCount());

		testEntity.getValue();
		assertEquals(1, getStatistics().getPrepareStatementCount()//
				, "should be 2, activate bytecode instrumentation and enableLazyInitialization=true in pom.xml");
	}

	@Test
	public void testCacheRead() {
		EntityManager em = getEntityManager();
		CacheManager cacheManager = CacheManager.ALL_CACHE_MANAGERS.get(0);
		Cache cache = cacheManager.getCache(CachedEntity.class.getName());
		cache.removeAll(); // clear cache, especially when running all tests in one run

		CachedEntity entity = em.find(CachedEntity.class, 1L);
		assertEquals(1, getStatistics().getEntityLoadCount(), "should have loaded once");
		assertEquals(0, getStatistics().getSecondLevelCacheHitCount(), "should have had no hit");

		newTransaction();
		CachedEntity entity2 = em.find(CachedEntity.class, 1L);
		assertEquals(1, getStatistics().getEntityLoadCount(), "should have loaded once");
		assertEquals(1, getStatistics().getSecondLevelCacheHitCount(), "should have had one hit");

		assertTrue(cache.getSize() > 0, "should have entries in die cache");
		assertEquals(1, getStatistics().getEntityLoadCount(), "should have loaded once");
		assertEquals(1, getStatistics().getSecondLevelCacheHitCount(), "should have had one hit");

		newTransaction();
		cache.removeAll();
		assertTrue(cache.getSize() == 0, "should be empty");

		CachedEntity entity3 = em.find(CachedEntity.class, 1L);
		assertEquals(2, getStatistics().getEntityLoadCount(), "should have loaded twice");
		assertEquals(1, getStatistics().getSecondLevelCacheHitCount(), "should have had one hit");
	}

	@Test
	public void testWriteThrough() {
		EntityManager em = getEntityManager();
		CacheManager cacheManager = CacheManager.ALL_CACHE_MANAGERS.get(0);
		String newName = "changed name";

		CachedEntity entity = em.find(CachedEntity.class, 1L);
		assertEquals(1, getStatistics().getEntityLoadCount(), "should have loaded once");
		assertEquals(0, getStatistics().getSecondLevelCacheHitCount(), "should have had no hit");

		newTransaction();
		CachedEntity entity2 = em.find(CachedEntity.class, 1L);
		assertEquals(1, getStatistics().getEntityLoadCount(), "should have loaded once");
		assertEquals(1, getStatistics().getSecondLevelCacheHitCount(), "should have had one hit");
		entity2.setName(newName);
		em.flush();
		assertEquals(1, getStatistics().getEntityUpdateCount(), "should have one updated");

		newTransaction();

		CachedEntity entity3 = em.find(CachedEntity.class, 1L);
		assertEquals(1, getStatistics().getEntityLoadCount(), "should have loaded twice");
		assertEquals(2, getStatistics().getSecondLevelCacheHitCount(), "should have had one hit");
		assertEquals(newName, entity3.getName(), "name should still be changed");
	}

	@Test
	public void testDeleteThrough() {
		EntityManager em = getEntityManager();
		CacheManager cacheManager = CacheManager.ALL_CACHE_MANAGERS.get(0);
		Cache cache = cacheManager.getCache(CachedEntity.class.getName());
		cache.removeAll(); // clear cache, especially when running all tests in one run
		
		em.find(CachedEntity.class, 1L);
		em.find(CachedEntity.class, 2L);
		em.find(CachedEntity.class, 3L);
		assertEquals(3, getStatistics().getEntityLoadCount(), "should have loaded two");
		assertEquals(0, getStatistics().getSecondLevelCacheHitCount(), "should have had no hit");
		assertEquals(3, cache.getSize());

		newTransaction();

		CachedEntity entity = em.find(CachedEntity.class, 1L);
		em.remove(entity);
		em.flush();
		assertEquals(3, cache.getSize());
		em.createQuery("DELETE from CachedEntity e WHERE e.id=:id") //
				.setParameter("id", 2L)//
				.executeUpdate();
		assertEquals(0, cache.getSize());

		em.createNativeQuery("DELETE from CachedEntity e WHERE e.id=:id") //
				.setParameter("id", 3L)//
				.executeUpdate();
		assertEquals(0, cache.getSize());

		em.flush();
		assertEquals(1, getStatistics().getEntityDeleteCount(), "should have deleted one");

		newTransaction();

		CachedEntity entity1 = em.find(CachedEntity.class, 1L);
		CachedEntity entity2 = em.find(CachedEntity.class, 2L);
		CachedEntity entity3 = em.find(CachedEntity.class, 3L);
		assertTrue(entity1 == null, "should be null");
		assertTrue(entity2 == null, "should be null, deleted by query");
		assertTrue(entity3 == null, "should be null, deleted by native query");
	}

	@Test
	public void testQueryCache() {
		EntityManager em = getEntityManager();
		CacheManager cacheManager = CacheManager.ALL_CACHE_MANAGERS.get(0);

		for (int i = 0; i < 6; i++) {
			long id = (long) (1 + i / 3);
			System.out.println("----query loop=" + i + " id=" + id);
			List<CachedEntity> cached = em
					.createQuery("SELECT e FROM CachedEntity e WHERE id <= :id", CachedEntity.class) //
					.setHint("org.hibernate.cacheable", true) //
					.setParameter("id", id) //
					.getResultList();

			newTransaction();

		}

		System.out.println("Caches:");
		Arrays.stream(cacheManager.getCacheNames()).forEach(name -> {
			System.out.println(name + "-------------------------");
			Cache cache = cacheManager.getCache(name);
			cache.getKeys().stream().forEach(key -> {
				Element value = cache.get(key);
				System.out.println("key=" + key + "\n = " + value.getObjectValue());
			});
		});
		System.out.println();
	}

}
