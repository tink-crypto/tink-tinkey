#!/bin/bash
# Copyright 2023 Google LLC
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

# Builds and releases a tink-tinkey artifact.
#
# The behavior of this script can be modified using the following optional env
# variables:
#
# - CONTAINER_IMAGE (unset by default): By default when run locally this script
#   executes tests directly on the host. The CONTAINER_IMAGE variable can be set
#   to execute tests in a custom container image for local testing. E.g.:
#
#   CONTAINER_IMAGE="us-docker.pkg.dev/tink-test-infrastructure/tink-ci-images/linux-tink-java-base:latest" \
#     sh ./kokoro/gcp_ubuntu/release/run_tests.sh
set -eEuo pipefail

# Fail if RELEASE_VERSION is not set.
if [[ -z "${RELEASE_VERSION:-}" ]]; then
  echo "RELEASE_VERSION must be set" >&2
  exit 1
fi

IS_KOKORO="false"
if [[ -n "${KOKORO_ARTIFACTS_DIR:-}" ]]; then
  IS_KOKORO="true"
fi
readonly IS_KOKORO

DOCKER_EXECUTE_ARGS=()

if [[ "${IS_KOKORO}" == "true" ]]; then
  readonly TINK_BASE_DIR="$(echo "${KOKORO_ARTIFACTS_DIR}"/git*)"
  cd "${TINK_BASE_DIR}/tink_tinkey"
  source "./kokoro/testutils/java_test_container_images.sh"
  CONTAINER_IMAGE="${TINK_JAVA_BASE_IMAGE}"
  DOCKER_EXECUTE_ARGS+=( -k "${TINK_GCR_SERVICE_KEY}" )
fi
readonly CONTAINER_IMAGE

if [[ -n "${CONTAINER_IMAGE:-}" ]]; then
  DOCKER_EXECUTE_ARGS+=( -c "${CONTAINER_IMAGE}" )
fi

# WARNING: Setting this environment varialble to "true" will cause this script
# to actually perform a release.
: "${DO_MAKE_RELEASE:=false}"

if [[ ! "${DO_MAKE_RELEASE}" =~ ^(false|true)$ ]]; then
  echo "DO_MAKE_RELEASE must be either \"true\" or \"false\"" >&2
  exit 1
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

# Run cleanup on EXIT.
trap cleanup EXIT

cleanup() {
  rm -rf _activate_gcloud_account_and_release_tinkey.sh _env.txt
}

if [[ "${DO_MAKE_RELEASE}" == "true" ]]; then
  # Copy the service key to make sure it is available to the container.
  cp "${KOKORO_KEYSTORE_DIR}/70968_tink_tinkey_release_service_key" \
    release_service_key

  cat <<EOF > _activate_gcloud_account_and_release_tinkey.sh
#!/bin/bash
set -euo pipefail

gcloud auth activate-service-account --key-file=release_service_key
gcloud config set project tink-test-infrastructure
./release_tinkey.sh "${RELEASE_VERSION}"
EOF

  chmod +x _activate_gcloud_account_and_release_tinkey.sh
  ./kokoro/testutils/docker_execute.sh "${DOCKER_EXECUTE_ARGS[@]}" \
    ./_activate_gcloud_account_and_release_tinkey.sh
else
  # Run in dry-run mode.
  ./kokoro/testutils/docker_execute.sh "${DOCKER_EXECUTE_ARGS[@]}" \
    ./release_tinkey.sh -d "${RELEASE_VERSION}"
fi
