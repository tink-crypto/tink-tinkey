workspace(name = "tink_tinkey")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
    name = "tink_java",
    urls = ["https://github.com/tink-crypto/tink-java/archive/main.zip"],
    strip_prefix = "tink-java-main",
)

http_archive(
    name = "tink_java_awskms",
    urls = ["https://github.com/tink-crypto/tink-java-awskms/archive/main.zip"],
    strip_prefix = "tink-java-awskms-main",
)

http_archive(
    name = "tink_java_gcpkms",
    urls = ["https://github.com/tink-crypto/tink-java-gcpkms/archive/main.zip"],
    strip_prefix = "tink-java-gcpkms-main",
)

load("@tink_java//:tink_java_deps.bzl", "tink_java_deps", "TINK_MAVEN_ARTIFACTS")

tink_java_deps()

load("@tink_java//:tink_java_deps_init.bzl", "tink_java_deps_init")

tink_java_deps_init()

load("@tink_java_gcpkms//:tink_java_gcpkms_deps.bzl", "TINK_JAVA_GCPKMS_MAVEN_ARTIFACTS")

load("@tink_java_awskms//:tink_java_awskms_deps.bzl", "TINK_JAVA_AWSKMS_MAVEN_ARTIFACTS")

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = TINK_MAVEN_ARTIFACTS +
      TINK_JAVA_GCPKMS_MAVEN_ARTIFACTS +
      TINK_JAVA_AWSKMS_MAVEN_ARTIFACTS + [
      "args4j:args4j:2.33",
    ],
    repositories = [
        "https://maven.google.com",
        "https://repo1.maven.org/maven2",
    ],
)
