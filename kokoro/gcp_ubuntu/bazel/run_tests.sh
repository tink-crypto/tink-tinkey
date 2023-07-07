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

# By default when run locally this script runs the command below directly on the
# host. The CONTAINER_IMAGE variable can be set to run on a custom container
# image for local testing. E.g.:
#
# CONTAINER_IMAGE="us-docker.pkg.dev/tink-test-infrastructure/tink-ci-images/linux-tink-java-base:latest" \
#  sh ./kokoro/gcp_ubuntu/bazel/run_tests.sh
#
# The user may specify TINK_BASE_DIR as the folder where to look for
# tink-java-awskms and its depndencies. That is:
#   ${TINK_BASE_DIR}/tink_java
#   ${TINK_BASE_DIR}/tink_tinkey
set -eEuo pipefail

RUN_COMMAND_ARGS=()
if [[ -n "${KOKORO_ROOT:-}" ]]; then
  TINK_BASE_DIR="$(echo "${KOKORO_ARTIFACTS_DIR}"/git*)"
  cd "${TINK_BASE_DIR}/tink_tinkey"
  source "./kokoro/testutils/java_test_container_images.sh"
  CONTAINER_IMAGE="${TINK_JAVA_BASE_IMAGE}"
  RUN_COMMAND_ARGS+=( -k "${TINK_GCR_SERVICE_KEY}" )
fi
: "${TINK_BASE_DIR:=$(cd .. && pwd)}"
readonly TINK_BASE_DIR
readonly CONTAINER_IMAGE

if [[ -n "${CONTAINER_IMAGE}" ]]; then
  RUN_COMMAND_ARGS+=( -c "${CONTAINER_IMAGE}" )
fi
readonly RUN_COMMAND_ARGS

# Check for dependencies in TINK_BASE_DIR. Any that aren't present will be
# downloaded.
readonly GITHUB_ORG="https://github.com/tink-crypto"
./kokoro/testutils/fetch_git_repo_if_not_present.sh "${TINK_BASE_DIR}" \
  "${GITHUB_ORG}/tink-java" "${GITHUB_ORG}/tink-java-awskms" \
  "${GITHUB_ORG}/tink-java-gcpkms"

cp "WORKSPACE" "WORKSPACE.bak"
./kokoro/testutils/replace_http_archive_with_local_repository.py \
  -f "WORKSPACE" -t ..

# Run cleanup on EXIT.
trap cleanup EXIT

cleanup() {
  mv WORKSPACE.bak WORKSPACE
}

./kokoro/testutils/run_command.sh "${RUN_COMMAND_ARGS[@]}" \
  ./kokoro/testutils/run_bazel_tests.sh .
