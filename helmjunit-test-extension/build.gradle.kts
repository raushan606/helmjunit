plugins {
    id("java")
    `java-library`
    `maven-publish`
}

group = "com.raushan.helmjunit"
version = "1.0.0"

repositories {
    mavenCentral()
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    implementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.18")
}

tasks.test {
    useJUnitPlatform()
}