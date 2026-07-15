plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://mvn.flappygrant.com")
}

dependencies {
    compileOnly("com.pghserver:pghserver-api:7")
}