plugins {
    kotlin("jvm") version "2.1.0"
    `maven-publish`
}

group = "com.harleylizard"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.dynatrace.hash4j:hash4j:0.20.0")
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("com.google.code.gson:gson:2.13.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "localHost"
            url = uri("http://localhost:8080/")
            isAllowInsecureProtocol = true
            credentials {
                username = "test"
                password = "test"
            }
            authentication {
                create("basic", BasicAuthentication::class.java)
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.harleylizard"
            artifactId = "deployment"
            version = "1.0-SNAPSHOT"
            from(components["java"])
        }
    }
}