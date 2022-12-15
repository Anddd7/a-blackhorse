import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.22"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

group = "com.thoughtworks.blackhorse"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    flatDir {
        dirs("libs")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.1")

    implementation(files("libs/plantuml.1.2021.5.jar"))

    implementation("org.apache.httpcomponents:httpmime:4.5.13")
    implementation("com.google.guava:guava:31.1-jre")

    implementation("com.google.code.gson:gson:2.9.0")

    implementation("ch.qos.logback:logback-classic:1.2.11")

    // testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

task<JavaExec>("rebuild") {
    dependsOn("build")
    group = "Execution"
    description = "Rebuild all changed stories"
    classpath = sourceSets.main.get().runtimeClasspath
    main = "com.thoughtworks.blackhorse.automation.AutoBuildKt"
}

task<Copy>("initGitHooks") {
    from("gradle/git-hooks")
    into(".git/hooks")
    fileMode = Integer.parseInt("0755", 8)
}

tasks.getByPath(":build").dependsOn(":initGitHooks")
