workspace(name = "tink_tinkey")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

RULES_JVM_EXTERNAL_TAG = "5.3"
RULES_JVM_EXTERNAL_SHA ="d31e369b854322ca5098ea12c69d7175ded971435e55c18dd9dd5f29cc5249ac"

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    sha256 = RULES_JVM_EXTERNAL_SHA,
    url = "https://github.com/bazelbuild/rules_jvm_external/releases/download/%s/rules_jvm_external-%s.tar.gz" % (RULES_JVM_EXTERNAL_TAG, RULES_JVM_EXTERNAL_TAG)
)

load("@rules_jvm_external//:repositories.bzl", "rules_jvm_external_deps")

rules_jvm_external_deps()

load("@rules_jvm_external//:setup.bzl", "rules_jvm_external_setup")

rules_jvm_external_setup()

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "args4j:args4j:2.33",
        "com.google.auto.service:auto-service-annotations:1.1.1",
        "com.google.auto.service:auto-service:1.1.1",
        "com.google.auto:auto-common:1.2.2",
        "com.google.crypto.tink:tink-awskms:1.9.1",
        "com.google.crypto.tink:tink-gcpkms:1.9.0",
        "com.google.crypto.tink:tink:1.11.0",
        "com.google.truth:truth:1.1.5",
        "junit:junit:4.13.2",
    ],
    repositories = ["https://repo1.maven.org/maven2"],
)
