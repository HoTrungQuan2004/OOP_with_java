**Project Overview**
- Nội dung (Ai rảnh thì viết nha :> )

**Prerequisites**
- Java 17+ (JDK installed)
- Maven 3.6+
- Optional: Oracle DB if you want to run with `db` profile

**Installing Oracle JDBC driver (ojdbc11) for Maven builds**

The Oracle JDBC driver is not available on Maven Central by default. If you already have
the `ojdbc11.jar` file in `lib/` (path: `/workspaces/OOP_with_java/ParkingManagementApp/lib/ojdbc11.jar`),
install it into your local Maven repository and then add it as a dependency in the `pom.xml`.

- Install the jar into your local Maven repo (run this from the project root):
```bash
mvn install:install-file \
	-Dfile=ParkingManagementApp/lib/ojdbc11.jar \
	-DgroupId=com.oracle.database.jdbc \
	-DartifactId=ojdbc11 \
	-Dversion=11.2.0-oracle \
	-Dpackaging=jar
```

- Add this dependency to `ParkingManagementApp/pom.xml` (or the module's `pom.xml`):
```xml
<dependency>
	<groupId>com.oracle.database.jdbc</groupId>
	<artifactId>ojdbc11</artifactId>
	<version>11.2.0-oracle</version>
</dependency>
```

- Notes and alternatives:
	- Do **not** commit the `ojdbc11.jar` binary into source control. Keep it in `lib/` locally
		and install it into your local Maven repository as shown above.
	- If you have access to an internal Maven repository (Nexus/Artifactory) that hosts Oracle
		drivers, consider deploying the jar there and referencing that repository from your `pom.xml`.
	- Oracle JDBC is distributed under Oracle's license — check redistribution rules before
		publishing any artifacts that include the driver.

**Build**
- From the repository root (or inside `ParkingManagementApp`):
```bash
cd /workspaces/OOP_with_java/ParkingManagementApp
mvn -DskipTests clean package
```
- The runnable jar will be created at `target/parking-management-1.0.0.jar`.


**Run (recommended — packaged jar)**
- Run the packaged application (preferred for reliable shutdown behavior):
```bash
cd /workspaces/OOP_with_java/ParkingManagementApp/target
java -jar parking-management-1.0.0.jar --spring.profiles.active=db
```

**Profiles**
- `db` — use real JDBC DataSource (configured in `src/main/resources/application-db.properties`).
- In-memory mode (dev) — set `app.use-inmemory=true` (default for the in-memory repository). If you want in-memory mode run with:
```bash
java -jar target/parking-management-1.0.0.jar --spring.profiles.active=default --app.use-inmemory=true
```

**Database setup (Oracle example)**
- Edit `src/main/resources/application-db.properties` and fill your `spring.datasource.url`, `username`, `password`.
- We used SID in this below example, you need to change the URL depend on your type
```bash
spring.datasource.url=jdbc:oracle:thin:@<HOST>:<PORT>:<SID>
spring.datasource.username=<YOUR_USERNAME>
spring.datasource.password=<YOUR_PASSWORD>
```


**Verify clean shutdown & DB disconnect (Ctrl+C)**
- Recommended: run the jar and redirect logs to a file so you can inspect shutdown messages:
```bash
cd /workspaces/OOP_with_java/ParkingManagementApp/target
java -jar parking-management-1.0.0.jar --spring.profiles.active=db > ../app.log 2>&1
# Press Ctrl+C in that terminal to stop the app
```

