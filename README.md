**Project Overview**
- Nội dung (Ai rảnh thì viết nha :> )

**Prerequisites**
- Java 17+ (JDK installed)
- Maven 3.6+
- Optional: Oracle DB if you want to run with `db` profile

**Build**
- From the repository root (or inside `ParkingManagementApp`):
```bash
cd /workspaces/OOP_with_java/ParkingManagementApp
mvn -DskipTests clean package
```
- The runnable jar will be created at `target/parking-management-1.0.0.jar`.

**Run (development quick-run)**
- Using Maven (dev with devtools reload — not recommended for shutdown testing):
```bash
cd /workspaces/OOP_with_java/ParkingManagementApp
mvn -DskipTests spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=db"
```

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
