package de.tvdtb.talk.mastering.jpa.performance.test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.jupiter.api.Test;

import de.tvdtb.talk.mastering.jpa.performance.PersistenceTest;
import de.tvdtb.talk.mastering.jpa.performance.SessionStatisticListener;
import de.tvdtb.talk.mastering.jpa.performance.model.City;
import de.tvdtb.talk.mastering.jpa.performance.model.PostalCode;
import de.tvdtb.talk.mastering.jpa.performance.model.State;

public class CreateTest extends PersistenceTest {

	/**
	 * Demonstrates write performance, in order to optimize let those "T_O_D_O
	 * Optimization" comments guide you through (Toggle all lines except the last
	 * one with two dashes)
	 * 
	 * You might want to change your database connection in persistence.xml, SQL
	 * files are in src/test/resources/META-INF/sql
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWritePerformance() throws Exception {
		EntityManager em = getEntityManager();
		em.createNamedQuery("PostalCode.deleteAll").executeUpdate();
		em.createNamedQuery("City.deleteAll").executeUpdate();
		em.createNamedQuery("State.deleteAll").executeUpdate();
		System.out.println("---------------------------------------------");

		SessionStatisticListener listener = addListener();

		boolean lorem = true;
		Map<String, State> states = new TreeMap<>();
		Map<String, City> cities = new TreeMap<>();

		long startTime = System.currentTimeMillis();

		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("testdata.csv");

		try (Reader fr = new InputStreamReader(in, "utf-8")) {

			CSVParser parsedCsv = CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader().parse(fr);
// TODO Optimization Step 2
//			List<ImmutableTriple<State, City, PostalCode>> triples = //
//--
			StreamSupport.stream(parsedCsv.spliterator(), false) //
					.map(record -> {
						State state = getOrCreate(states, record, "bundesland", r -> {
							State newState = new State();
							newState.setName(r.get("bundesland"));
							if (lorem)
								newState.setLorem(record.get("lorem"));
							return newState;
						});
						City city = getOrCreate(cities, record, "ort", r -> {
							City newCity = new City(r.get("ort"));
							newCity.setState(state);
							if (lorem)
								newCity.setLorem(record.get("lorem"));
							return newCity;
						});
						PostalCode postalCode = new PostalCode(record.get("plz"));
						postalCode.setCity(city);
						if (lorem)
							postalCode.setLorem(record.get("lorem"));
						
						return new ImmutableTriple<>(state, city, postalCode);
					})//
// TODO Optimization Step 2
					.forEach((ImmutableTriple<State, City, PostalCode> triple) -> {
						if (isNew(triple.getLeft()))
							em.persist(triple.getLeft());
						if (isNew(triple.getMiddle()))
							em.persist(triple.getMiddle());
						em.persist(triple.getRight());
					});
//					.collect(Collectors.toList());
//			triples.stream().filter(triple -> isNew(triple.getLeft()))//
//							.forEach(triple -> em.persist(triple.getLeft()));
//			triples.stream().filter(triple -> isNew(triple.getMiddle()))//
//			                .forEach(triple -> em.persist(triple.getMiddle()));
//			triples.stream().forEach(triple -> em.persist(triple.getRight()));
// --
		}

		long flushTime = System.currentTimeMillis();

		em.flush();

		long endTime = System.currentTimeMillis();

		int jdbcExecuteStatementCount = listener.getJdbcExecuteStatementCount();
		int batchCount = listener.getJdbcExecuteBatchCount();
		System.out.println("\n\n" //
				+ "total=" + (endTime - startTime) + "ms " //
				+ "(write = " + (flushTime - startTime) + "ms "//
				+ ", flush = " + (endTime - flushTime) + "ms)\n" //
				+ "statements = " + jdbcExecuteStatementCount + " batches = " + batchCount + " lorem="+lorem);

		System.out.println("STATEMENTS=" + jdbcExecuteStatementCount);
		assertFalse(jdbcExecuteStatementCount > 37087, "Optimize me! this is worse than my default");
		assertFalse(jdbcExecuteStatementCount == 37087,
				"Optimize me! This is the first result, toggle lines below TODO \"Optimization 1\"");
		assertFalse(jdbcExecuteStatementCount > 35135,
				"Optimize me! now go ahead and change increment in Constants.java");
		assertFalse(jdbcExecuteStatementCount > 23893,
				"Optimize me! There is still optimization possible ... up to 1000");
		assertFalse(jdbcExecuteStatementCount > 22658,
				"Optimize me! There is still optimization possible ... up to 1000");
		assertFalse(batchCount > 146, "Optimize me! a jdbc batch size of up to 100 makes sense");
		assertFalse(jdbcExecuteStatementCount > 8202,
				"Optimize me! There is still optimization possible ... up to 1000");
	}

	private <V> V getOrCreate(Map<String, V> map, CSVRecord record, String keyColumn, Function<CSVRecord, V> function) {
		String key = record.get(keyColumn);
		V existingValue = map.get(key);
		if (existingValue == null) {
			existingValue = function.apply(record);
			map.put(key, existingValue);
		}
		return existingValue;
	}
}
