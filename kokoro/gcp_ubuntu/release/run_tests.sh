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

IS_KOKORO="false"
if [[ -n "${KOKORO_ROOT:-}" ]]; then
  IS_KOKORO="true"
fi
readonly IS_KOKORO

if [[ "${IS_KOKORO}" == "true" ]]; then
  # Note: When running Tink tests on Kokoro either <KOKORO_ARTIFACTS_DIR>/git
  # or <KOKORO_ARTIFACTS_DIR>/github is present. The presence of any other
  # folder in KOKORO_ARTIFACTS_DIR that matches git* will make the test fail.
  TINK_BASE_DIR="$(echo "${KOKORO_ARTIFACTS_DIR}"/git*)"
  cd "${TINK_BASE_DIR}/tink_tinkey"
fi

: "${TINK_BASE_DIR:=$(cd .. && pwd)}"

# Check for dependencies in TINK_BASE_DIR. Any that aren't present will be
# downloaded.
readonly GITHUB_ORG="https://github.com/tink-crypto"
./kokoro/testutils/fetch_git_repo_if_not_present.sh "${TINK_BASE_DIR}" \
  "${GITHUB_ORG}/tink-java" "${GITHUB_ORG}/tink-java-awskms" \
  "${GITHUB_ORG}/tink-java-gcpkms"

cp "WORKSPACE" "WORKSPACE.bak"

./kokoro/testutils/replace_http_archive_with_local_repository.py \
  -f "WORKSPACE" -t "${TINK_BASE_DIR}"

readonly GITHUB_JOB_NAME="tink/github/tinkey/gcp_ubuntu/release/continuous"

RELEASE_TINKEY_ARGS=()
if [[ "${IS_KOKORO}" == "true" \
      && "${KOKORO_JOB_NAME}" == "${GITHUB_JOB_NAME}" ]]; then
  gcloud auth activate-service-account \
    --key-file="${KOKORO_KEYSTORE_DIR}/70968_tink_tinkey_release_service_key"
  gcloud config set project tink-test-infrastructure
else
  # Run in dry-run mode.
  RELEASE_TINKEY_ARGS+=( -d )
fi
readonly RELEASE_TINKEY_ARGS

./release_tinkey.sh "${RELEASE_TINKEY_ARGS[@]}"

mv "WORKSPACE.bak" "WORKSPACE"
