# Backend for main analytics dashboard

> **_NOTE:_**  The solution is pretty straightforward and simple. It is non-production project.
> Due to time restrictions performance is somehow poor. 

##  Requirements
- JDK 21
- Docker
- Gradle

## Startup
1. Clone the repo
```bash
mkdir analytics_dashboard
cd analytics_dashboard
git clone https://github.com/PureTeamLead/kotlin-jetbrains-backend-analytics-dashboard.git
```

2. Building .jar
```bash
export GRADLE_OPTS="--enable-native-access=ALL-UNNAMED"
./gradlew clean build
```

3. Building docker image (do not forget to start docker daemon)
```bash
docker build -t backend:1.0.0 .
```

4. Run docker container
```bash
docker run -it -p 8089:8089 backend:1.0.0
```

## Testing API with Postman
> Do not share the link!

[Postman Collection](https://api.postman.com/collections/40502373-9a3be043-726f-4080-be5b-6a02ad1adda8?access_key=PMAT-01K6BQF2KY9NDH93N2XS5CVJ08)