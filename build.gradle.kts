import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.StandardOpenOption

plugins {
  maven
  `java-library`
  kotlin("jvm") version "1.3.72"
}
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath("org.ow2.asm:asm-tree:9.0")
  }
}

tasks.getByName<Wrapper>("wrapper") {
  gradleVersion = "6.8.1"
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<Test> {
  useJUnitPlatform()
}

repositories {
  jcenter()
}
dependencies {
  implementation(dependencies.create(kotlin("stdlib"), closureOf<ExternalModuleDependency> {
    exclude("org.jetbrains", "annotations")
  }))
  api(platform(kotlin("bom")))
  api(platform("com.fasterxml.jackson:jackson-bom:2.11.4"))

  implementation("com.google.guava", "guava", "28.2-jre") {
    exclude("com.google.code.findbugs", "jsr305")
    exclude("org.checkerframework", "checker-qual")
    exclude("com.google.errorprone", "error_prone_annotations")
    exclude("com.google.j2objc", "j2objc-annotations")
  }
  compileOnly("com.fasterxml.jackson.core", "jackson-databind")

  testImplementation("io.kotlintest", "kotlintest-runner-junit5", "3.4.2")
  testImplementation("com.fasterxml.jackson.module", "jackson-module-kotlin")
}


fun fileSystem(uri: String) = fileSystem(URI.create(uri))
fun fileSystem(uri: URI) = FileSystems.newFileSystem(uri, mapOf<String, String>())

// Hide the codec package
tasks["jar"].doLast {
  val fileName = if (version == Project.DEFAULT_VERSION) project.name else "${project.name}-$version"

  fileSystem("jar:" + File("$buildDir/libs/$fileName.jar").toURI()).use { fileSystem ->
    Files
        .list(fileSystem.getPath("io/github/portfoligno/json/ast/codec"))
        .forEach { path ->
          val cls = ClassNode()

          Files.newInputStream(path).use {
            ClassReader(it).accept(cls, 0)
          }
          cls.access = cls.access and Opcodes.ACC_PUBLIC.inv()

          val writer = ClassWriter(0)
          cls.accept(writer)
          Files.write(path, writer.toByteArray(), StandardOpenOption.TRUNCATE_EXISTING)
        }
  }
}
