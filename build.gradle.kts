plugins {
    `java-library`
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(25))

repositories {
    mavenCentral()
    maven("https://mvn.flappygrant.com")
}

dependencies {
    compileOnly("com.pghserver:pghserver-api:9")
}