package de.tvdtb.talk.mastering.jpa.performance;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import de.tvdtb.talk.mastering.jpa.performance.model.TechnicalId;

public class PersistenceTest {
	
	static EntityManager em;
	static EntityManagerFactory emf;

	@BeforeAll
	public static void setup() {
		emf = Persistence.createEntityManagerFactory("jpa-performance");
	}
	
	@BeforeEach
	public final void beginTransaction() {
		em = emf.createEntityManager();
		// provide a transaction
		em.getTransaction().begin();
		getStatistics().clear();
	}
	
	@AfterEach
	public final void endTransaction() {
		// flush to simulate writing to the database
		em.flush();
		// by default rollback the transaction
		em.getTransaction().rollback();
		em.close();
	}
	
	public final void newTransaction() {
		em.getTransaction().commit();
		em.clear();
		em.getTransaction().begin();
	}
	
	@AfterAll
	public static void tearDown() {
		emf.close();
	}
	
	public boolean isNew(TechnicalId entity) {
		return entity!=null && entity.getId() == 0L;
	}

	
	protected EntityManager getEntityManager() {
		return em;
	}

	protected Statistics getStatistics() {
		Statistics statistics = ((Session) getEntityManager().getDelegate()).getSessionFactory().getStatistics();
		return statistics;
	}	

	protected SessionStatisticListener addListener() {
		SessionStatisticListener statisticListener = new SessionStatisticListener();
		
		Session session = (Session) getEntityManager().getDelegate();
		session.addEventListeners(statisticListener);
		
		return statisticListener;
	}
	
}
