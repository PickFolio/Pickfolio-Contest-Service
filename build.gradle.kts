plugins {
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.5"
    id("java")
}

group = "com.pickfolio"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot core
    implementation("org.springframework.boot:spring-boot-starter-web:3.5.3")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.5.3")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.5.3")
    implementation("org.springframework.boot:spring-boot-starter-security:3.5.3")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:3.5.3")
    implementation("org.springframework.boot:spring-boot-starter-websocket:3.5.3")

    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // PostgreSQL driver
    runtimeOnly("org.postgresql:postgresql:42.7.3")

    // Java UUID support
    implementation("com.fasterxml.uuid:java-uuid-generator:4.0.1")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}