# Mastering JPA Performance

Demo code for my "Mastering JPA Performance" talk at Oracle Code on 25-10-2018.

All code is written in JUnit 5 and runs by default using a H2 in-memory database.

It's organized following the **CRUD** scheme
- **C**reate [CreateTest.java](src/test/java/de/tvdtb/talk/mastering/jpa/performance/test/CreateTest.java "CreateTest.java")
- **R**ead [ReadTest.java](src/test/java/de/tvdtb/talk/mastering/jpa/performance/test/ReadTest.java "ReadTest.java") including Caching
- **U**pdate [UpdateTest.java](src/test/java/de/tvdtb/talk/mastering/jpa/performance/test/UpdateTest.java "UpdateTest.java")
- **D**elete [DeleteTest.java](src/test/java/de/tvdtb/talk/mastering/jpa/performance/test/DeleteTest.java "DeleteTest.java")

All Tests run successfully in the default configuration, except [CreateTest.java](src/test/java/de/tvdtb/talk/mastering/jpa/performance/test/CreateTest.java "CreateTest.java") which needs to be optimized. Changing the mapping of entities may affect other tests as well.

You can
- turn off SQL output, gathering statistics or switch databases in [src/test/resources/persistence.xml](src/test/resources/persistence.xml "persistence.xml" )
- change Hibernate dependencies in [pom.xml](./pom.xml "pom.xml" )
