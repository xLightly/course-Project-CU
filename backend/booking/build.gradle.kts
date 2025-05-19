plugins {
	java
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.google.protobuf") version "0.9.5"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}


dependencies {
	implementation("com.google.protobuf:protobuf-java:4.30.2")
    implementation("io.grpc:grpc-netty-shaded:1.51.0")
    implementation("io.grpc:grpc-protobuf:1.51.0")
    implementation("io.grpc:grpc-stub:1.51.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:4.30.2"
	}
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.51.0"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
            }
        }
    }
}

sourceSets {
	main {
		java {
			srcDir("build/generated/source/proto/main/java")
			srcDir("build/generated/source/proto/main/grpc")
		}
		proto {
			srcDir("src/main/proto")
			srcDir("../auth-microservice/api")
		}
	}
}


tasks.withType<Test> {
	useJUnitPlatform()
}
