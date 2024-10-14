rootProject.name = "ticket_backend_kt"
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlin", "1.9.22")
        }
    }
}
pluginManagement {
    repositories {

        maven {
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
        }
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/maven-public/")
        }


        mavenCentral()
        mavenLocal()
    }
}

