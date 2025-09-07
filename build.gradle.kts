plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Reactor core API (Mono, Flux, operators)
    implementation("io.projectreactor:reactor-core:3.6.5")
    // JUnit 5 and Reactor StepVerifier support for testing
    testImplementation("io.projectreactor:reactor-test:3.6.5")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.5")
    testImplementation(platform("org.junit:junit-bom"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}