**Parking Management Application**

This repository contains a small Spring Boot web application for managing parking spots of a apartment. It supports two repository modes:

- In-memory repositories (intended for development and quick demos)
- JDBC-backed repositories (for production with an external database; Oracle used in examples)

The README below explains how to prepare the environment, install the Oracle JDBC driver (if needed), build, run and configure the app.

----

## Prerequisites

- Java 17+ (JDK installed and `JAVA_HOME` configured)
- Maven 3.6+
- Optional: Oracle Database if you want to run with the `db` profile

## Quick setup

1. Open a terminal and change to the project module:

```bash
cd /workspaces/OOP_with_java/ParkingManagementApp
```

2. Build the project (skip tests for faster builds during development):

```bash
mvn -DskipTests clean package
```

The runnable jar is created at `target/parking-management-1.0.0.jar`.

## Oracle JDBC driver (ojdbc11)

Oracle's JDBC driver is not available on Maven Central. If you have `ojdbc11.jar` locally (this project expects it at `ParkingManagementApp/lib/ojdbc11.jar` or you can download another version from [Oracle](https://www.oracle.com/database/technologies/appdev/jdbc.html) ), install it into your local Maven repository and add a dependency to the project's `pom.xml`.

And this is how to install it into your maven:

From the repository root run:

```bash
mvn install:install-file \
	-Dfile=ParkingManagementApp/lib/ojdbc11.jar \
	-DgroupId=com.oracle.database.jdbc \
	-DartifactId=ojdbc11 \
	-Dversion=11.2.0-oracle \
	-Dpackaging=jar
```

Then add this dependency to `ParkingManagementApp/pom.xml`:

```xml
<dependency>
	<groupId>com.oracle.database.jdbc</groupId>
	<artifactId>ojdbc11</artifactId>
	<version>11.2.0-oracle</version>
</dependency>
```

Notes:

- Do **not** commit `ojdbc11.jar` into version control. Keep a copy in `lib/` locally and install it to your local Maven repository as shown above.
- If your organization has an internal Maven repository (Nexus/Artifactory) that hosts Oracle drivers, prefer deploying the driver there and referencing that repository in the `pom.xml`.
- Check Oracle's license terms for redistribution before publishing artifacts that include their driver.

----

## Configuration

- Database configuration is located in `src/main/resources/application-db.properties`. Edit the following properties for an Oracle DB:

```properties
spring.datasource.url=jdbc:oracle:thin:@<HOST>:<PORT>:<SID-or-SERVICE>
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
# Hikari tuning recommended for production
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.connection-test-query=SELECT 1 FROM DUAL
```

- The application supports selecting between in-memory and DB-backed repositories using these options:

	- Run with the `db` profile to enable JDBC-backed repositories (requires a configured DataSource):

	```bash
	java -jar target/parking-management-1.0.0.jar --spring.profiles.active=db
	```

	- Run in in-memory mode for development (no DB required):

	```bash
	java -jar target/parking-management-1.0.0.jar --app.use-inmemory=true
	```

----

## Run the application

From the module directory:

```bash
cd cd ./ParkingManagementApp/target/
java -jar target/parking-management-1.0.0.jar --spring.profiles.active=db
```

When the application starts you can open a browser to:

- Dashboard: `http://localhost:8080/parking/dashboard`
- Manage spots: `http://localhost:8080/parking/spots`

Use the UI to add spots and residents or assign/unassign spots.

## Clean shutdown

To stop the application cleanly and ensure the DataSource and JDBC drivers are deregistered, press `Ctrl+C` in the terminal where the app is running. The app logs will include shutdown messages that confirm Hikari and the DataSource closed.

----

## Important operational notes and troubleshooting

- Sessions / Pool sizing (Oracle): if you see `ORA-02391 sessions-per-user exceeded` or connection failures when starting multiple instances, reduce the Hikari pool size and/or increase DB user session limits.

- Connection testing: the property `spring.datasource.hikari.connection-test-query=SELECT 1 FROM DUAL` helps validate connections for Oracle.

----

## Developer notes

- Repositories:
	- In-memory implementations are intended for development and quick demos; they use thread-safe maps but do not enforce DB-level constraints.
	- JDBC implementations use `JdbcTemplate` and depend on the configured DataSource.

- Service layer:
	- Multi-repository operations are annotated with `@Transactional` to ensure atomicity with JDBC-based repositories.
	- `ResidentRepository.save(...)` and `ParkingSpotRepository.save(...)` return the saved entity with generated id populated.
