import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.20"
    war
    id("org.wildfly.build.provision") version "0.0.11"
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

kotlin {
    jvmToolchain(17)
}

// 1. Compile

tasks.register("compile") {
    dependsOn(tasks.compileKotlin)
}

// 2. Build - уже есть

tasks.war {
    webAppDirectory = file("src/main/webapp")
    archiveFileName.set("lab4.war")
}

tasks.named("build") {

    setDependsOn(emptyList<Any>())

    dependsOn("compile")
    dependsOn("war")

    doLast {
        println("Sources were successfully built: " + "lab4.war")
    }
}

// 3. Clean - уже есть

// 4. Test

tasks.test {

    dependsOn("build")

    useJUnitPlatform()
}

// 5. Music

tasks.register("music") {
    dependsOn("build")

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


val altSourceDir = layout.buildDirectory.dir("alt-src")

tasks.register<Copy>("prepareAltSources") {
    from("src/main/kotlin")
    into(altSourceDir)

    rename { fileName ->
        fileName
            .replace("PointResource.kt", "AltPointResource.kt")
            .replace("UserResource.kt", "AltUserResource.kt")
            .replace("PointWSResource.kt", "AltPointWSResource.kt")
            .replace("PointDto.kt", "AltPointDto.kt")
    }

    filter { line ->
        line
            .replace("PointResource", "AltPointResource")
            .replace("UserResource", "AltUserResource")
            .replace("PointWSResource", "AltPointWSResource")
            .replace("PointDto", "AltPointDto")
            .replace("username", "altUsername")
            .replace("password", "altPassword")
    }
}

sourceSets {

    create("alt") {

        java.srcDir(altSourceDir)
        compileClasspath += sourceSets.main.get().compileClasspath
    }
}

tasks.named("compileAltKotlin") {
    dependsOn("prepareAltSources")
}

tasks.register<Jar>("altJar") {
    dependsOn("compileAltKotlin")
    archiveBaseName.set("lab4" + "-alt")
    archiveVersion.set("1.0")

    // Берем скомпилированные классы из нового SourceSet
    from(sourceSets["alt"].output)

    // Берем ресурсы из основной папки ресурсов
    from("src/main/resources") {
        include("**/*")
    }

    doLast {
        println("✅ Alt JAR: ${archiveFile.get().asFile.absolutePath}")
    }
}

// 6. Alt
tasks.register("alt") {
    dependsOn("build")
    dependsOn("altJar")
}
//ktlint {
//    version.set("1.3.1")
//    verbose.set(true)
//}
