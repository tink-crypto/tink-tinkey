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

set -euo pipefail

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

# WARNING: Setting this environment varialble to "true" will cause this script
# to actually perform a release.
: "${DO_MAKE_RELEASE:=false}"

if [[ ! "${DO_MAKE_RELEASE}" =~ ^(false|true)$ ]]; then
  echo "DO_MAKE_RELEASE must be either \"true\" or \"false\"" >&2
  exit 1
fi

# If we are running on Kokoro cd into the repository.
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

RELEASE_TINKEY_ARGS=()
if [[ "${DO_MAKE_RELEASE}" == "false" ]]; then
  # Run in dry-run mode.
  RELEASE_TINKEY_ARGS+=( -d )
fi
readonly RELEASE_TINKEY_ARGS

if [[ "${IS_KOKORO}" == "true" && "${DO_MAKE_RELEASE}" == "true" ]]; then
  gcloud auth activate-service-account \
    --key-file="${KOKORO_KEYSTORE_DIR}/70968_tink_tinkey_release_service_key"
  gcloud config set project tink-test-infrastructure
fi

./release_tinkey.sh "${RELEASE_TINKEY_ARGS[@]}" "${RELEASE_VERSION}"

mv "WORKSPACE.bak" "WORKSPACE"
