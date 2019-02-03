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
  implementation(kotlin("stdlib"))
  implementation("com.google.guava:guava:27.0.1-jre")
  compileOnly("com.fasterxml.jackson.core:jackson-databind:2.9.8")
}
