plugins {
    kotlin("jvm") version "2.1.20"
}

group = "com.github.professornik"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.t12y:ssim:1.0.0")
    implementation("org.openpnp:opencv:4.9.0-0")
    implementation("org.postgresql:postgresql:42.7.8")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.liquibase:liquibase-core:4.33.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

//    implementation("org.tensorflow:tensorflow-lite:2.13.0")
//    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
//    implementation("org.tensorflow:tensorflow-lite-task-text:0.4.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}