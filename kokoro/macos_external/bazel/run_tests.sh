#!/bin/bash
# Copyright 2022 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
################################################################################

# The user may specify TINK_BASE_DIR for setting a local copy of Tink to use
# when running the script locally.

set -euo pipefail

export XCODE_VERSION=14
export DEVELOPER_DIR="/Applications/Xcode_${XCODE_VERSION}.app/Contents/Developer"
export ANDROID_HOME="/usr/local/share/android-sdk"
export COURSIER_OPTS="-Djava.net.preferIPv6Addresses=true"

# If we are running on Kokoro cd into the repository.
if [[ -n "${KOKORO_ROOT:-}" ]]; then
  # Note: When running Tink tests on Kokoro either <KOKORO_ARTIFACTS_DIR>/git
  # or <KOKORO_ARTIFACTS_DIR>/github is present. The presence of any other
  # folder in KOKORO_ARTIFACTS_DIR that matches git* will make the test fail.
  TINK_BASE_DIR="$(echo "${KOKORO_ARTIFACTS_DIR}"/git*)"
  cd "${TINK_BASE_DIR}/tink_tinkey"
  chmod +x "${KOKORO_GFILE_DIR}/use_bazel.sh"
  "${KOKORO_GFILE_DIR}/use_bazel.sh" "$(cat .bazelversion)"
  export JAVA_HOME=$(/usr/libexec/java_home -v "1.8.0_292")
fi

: "${TINK_BASE_DIR:=$(cd .. && pwd)}"

# Check for dependencies in TINK_BASE_DIR. Any that aren't present will be
# downloaded.
readonly GITHUB_ORG="https://github.com/tink-crypto"
./kokoro/testutils/fetch_git_repo_if_not_present.sh "${TINK_BASE_DIR}" \
  "${GITHUB_ORG}/tink-java" "${GITHUB_ORG}/tink-java-awskms" \
  "${GITHUB_ORG}/tink-java-gcpkms"

# Sourcing required to update caller's environment.
./kokoro/testutils/copy_credentials.sh "testdata" "all"
./kokoro/testutils/update_android_sdk.sh

cp "WORKSPACE" "WORKSPACE.bak"

./kokoro/testutils/replace_http_archive_with_local_repository.py \
  -f "WORKSPACE" \
  -t "${TINK_BASE_DIR}"

# Tests that require AWS/Google Cloud KMS credentials are only run in Kokoro.
TINK_TINKEY_MANUAL_TARGETS=()
if [[ -n "${KOKORO_ROOT:-}" ]]; then
  TINK_TINKEY_MANUAL_TARGETS+=(
    "//src/test/java/com/google/crypto/tink/tinkey:AddKeyCommandTest"
    "//src/test/java/com/google/crypto/tink/tinkey:CreatePublicKeysetCommandTest"
    "//src/test/java/com/google/crypto/tink/tinkey:CreateKeysetCommandTest"
    "//src/test/java/com/google/crypto/tink/tinkey:RotateKeysetCommandTest"
  )
fi
readonly TINK_TINKEY_MANUAL_TARGETS

./kokoro/testutils/run_bazel_tests.sh . "${TINK_TINKEY_MANUAL_TARGETS[@]}"

mv "WORKSPACE.bak" "WORKSPACE"
