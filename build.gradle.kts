allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.flyte.gg/releases")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/") {
            name = "sonatype"
        }
        maven {
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
        }
        maven(url = "https://repo.codemc.org/repository/maven-public/")
    }
}

project(":paper") {

}

project(":velocity") {}