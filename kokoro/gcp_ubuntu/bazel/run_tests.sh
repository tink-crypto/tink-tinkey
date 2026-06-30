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

# Builds and tests tink-tinkey using Bazel.
#
# The behavior of this script can be modified using the following optional env
# variables:
#
# - CONTAINER_IMAGE (unset by default): By default when run locally this script
#   executes tests directly on the host. The CONTAINER_IMAGE variable can be set
#   to execute tests in a custom container image for local testing. E.g.:
#
#   CONTAINER_IMAGE="us-docker.pkg.dev/tink-test-infrastructure/tink-ci-images/linux-tink-java-base:latest" \
#     sh ./kokoro/gcp_ubuntu/bazel/run_tests.sh

# Generated with openssl rand -hex 10
echo "==========================================================================="
echo "Tink Script ID: db43adac95531de07eee (to quickly find the script from logs)"
echo "==========================================================================="

set -eEuo pipefail

DOCKER_EXECUTE_ARGS=()
if [[ -n "${KOKORO_ROOT:-}" ]]; then
  readonly TINK_BASE_DIR="$(echo "${KOKORO_ARTIFACTS_DIR}"/git*)"
  cd "${TINK_BASE_DIR}/tink_tinkey"
  source "./kokoro/testutils/java_test_container_images.sh"
  CONTAINER_IMAGE="${TINK_JAVA_BASE_IMAGE}"
  DOCKER_EXECUTE_ARGS+=( -k "${TINK_GCR_SERVICE_KEY}" )
fi
readonly CONTAINER_IMAGE

if [[ -n "${CONTAINER_IMAGE}" ]]; then
  DOCKER_EXECUTE_ARGS+=( -c "${CONTAINER_IMAGE}" )
fi

# Make sure we set ANDROID_HOME to an empty value so `rules_android`, which is
# transitively required by `rules_jvm_external`, doesn't try to use the Android
# SDK. See
# https://github.com/bazelbuild/rules_android/blob/ac6c4254424850a73b63ae5029f1ab5096e108c7/rules/android_sdk_repository/rule.bzl#L114-L117.
cat <<EOF > _env.txt
ANDROID_HOME=
EOF
DOCKER_EXECUTE_ARGS+=( -e _env.txt )

readonly DOCKER_EXECUTE_ARGS

./kokoro/testutils/docker_execute.sh "${DOCKER_EXECUTE_ARGS[@]}" \
  ./kokoro/testutils/run_bazel_tests.sh .
