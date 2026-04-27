plugins {
    kotlin("jvm") version "2.1.20"
    war
//    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "com.ssnagin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    // REST TESTS
    testImplementation("io.rest-assured:rest-assured:5.5.0")
    testImplementation("io.rest-assured:kotlin-extensions:5.5.0")

    compileOnly("jakarta.platform:jakarta.jakartaee-web-api:10.0.0")
    compileOnly("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")

    implementation("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.20.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.1")

    providedCompile("jakarta.platform:jakarta.jakartaee-api:10.0.0")
    implementation("org.eclipse.persistence:org.eclipse.persistence.jpa:4.0.8")

    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")

    implementation("at.favre.lib:bcrypt:0.10.2")

    providedCompile("org.eclipse.persistence:eclipselink:4.0.8")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.war {
    webAppDirectory = file("src/main/webapp")
    archiveFileName.set("lab4.war")
}

// 1. Compile

tasks.register("compile") {
    dependsOn(tasks.compileKotlin)
}

// 2. Build

tasks.named("build") {

    dependsOn("compile")

    doLast {
        var path = tasks.war.get().archiveFile.get().asFile.absolutePath

        logger.info("WAR Path: $path")
    }
}

// 3. Clean

tasks.named("clean2") {

    

    doLast {

    }

}

// 7. Music

tasks.register("music") {

    val snMusicPath = project.findProperty("sn.music.path")

    println(snMusicPath)

    doLast {
        val os = System.getProperty("os.name").lowercase()
        println(os)

        when {
            os.contains("win") -> exec {
                commandLine("cmd", "/c", "start", "/min", snMusicPath)
            }

            os.contains("linux") -> exec {
                commandLine("ffplay", "-nodisp", "-autoexit", snMusicPath)
            }
        }

    }
}

//ktlint {
//    version.set("1.3.1")
//    verbose.set(true)
//}
