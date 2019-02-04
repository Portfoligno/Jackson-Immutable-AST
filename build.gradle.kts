plugins {
  maven
  `java-library`
  kotlin("jvm") version "1.3.20"
}
tasks.getByName<Wrapper>("wrapper") {
  gradleVersion = "5.1.1"
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
  jcenter()
}
dependencies {
  implementation(create(kotlin("stdlib"), closureOf<ExternalModuleDependency> {
    exclude("org.jetbrains", "annotations")
  }))
  implementation("com.google.guava:guava:27.0.1-jre")
  compileOnly("com.fasterxml.jackson.core:jackson-databind:2.9.8")

  testImplementation("io.kotlintest:kotlintest-runner-junit5:3.2.1")
  testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
}
