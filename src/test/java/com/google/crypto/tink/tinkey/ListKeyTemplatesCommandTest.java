// Copyright 2026 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package com.google.crypto.tink.tinkey;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for {@code ListKeyTemplatesCommand}. */
@RunWith(JUnit4.class)
public final class ListKeyTemplatesCommandTest {

  @Test
  public void testListKeyTemplates_works() throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream, true, UTF_8.name());
    PrintStream oldOut = System.out;
    System.setOut(printStream);
    try {
      Tinkey.main(new String[] {"list-key-templates"});
    } finally {
      System.setOut(oldOut);
    }

    List<String> lines = Arrays.asList(new String(outputStream.toByteArray(), UTF_8).split("\\R"));
    assertThat(lines)
        .containsExactly(
            "The following key templates are supported:",
            "AES128_CTR_HMAC_SHA256",
            "AES128_CTR_HMAC_SHA256_1MB",
            "AES128_CTR_HMAC_SHA256_4KB",
            "AES128_CTR_HMAC_SHA256_RAW",
            "AES128_EAX",
            "AES128_EAX_RAW",
            "AES128_GCM",
            "AES128_GCM_HKDF_1MB",
            "AES128_GCM_HKDF_4KB",
            "AES128_GCM_RAW",
            "AES128_GCM_SIV",
            "AES128_GCM_SIV_RAW",
            "AES256_CMAC",
            "AES256_CMAC_PRF",
            "AES256_CMAC_RAW",
            "AES256_CTR_HMAC_SHA256",
            "AES256_CTR_HMAC_SHA256_1MB",
            "AES256_CTR_HMAC_SHA256_4KB",
            "AES256_CTR_HMAC_SHA256_RAW",
            "AES256_EAX",
            "AES256_EAX_RAW",
            // Placeholder for internal PRP key templates. DO NOT EDIT.
            "AES256_GCM",
            "AES256_GCM_HKDF_1MB",
            "AES256_GCM_HKDF_4KB",
            "AES256_GCM_RAW",
            "AES256_GCM_SIV",
            "AES256_GCM_SIV_RAW",
            "AES256_SIV",
            "AES256_SIV_RAW",
            "AES_CMAC",
            "AES_CMAC_PRF",
            "CHACHA20_POLY1305",
            "CHACHA20_POLY1305_RAW",
            "DHKEM_P256_HKDF_SHA256_HKDF_SHA256_AES_128_GCM",
            "DHKEM_P256_HKDF_SHA256_HKDF_SHA256_AES_128_GCM_RAW",
            "DHKEM_P256_HKDF_SHA256_HKDF_SHA256_AES_256_GCM",
            "DHKEM_P256_HKDF_SHA256_HKDF_SHA256_AES_256_GCM_RAW",
            "DHKEM_P384_HKDF_SHA384_HKDF_SHA384_AES_128_GCM",
            "DHKEM_P384_HKDF_SHA384_HKDF_SHA384_AES_128_GCM_RAW",
            "DHKEM_P384_HKDF_SHA384_HKDF_SHA384_AES_256_GCM",
            "DHKEM_P384_HKDF_SHA384_HKDF_SHA384_AES_256_GCM_RAW",
            "DHKEM_P521_HKDF_SHA512_HKDF_SHA512_AES_128_GCM",
            "DHKEM_P521_HKDF_SHA512_HKDF_SHA512_AES_128_GCM_RAW",
            "DHKEM_P521_HKDF_SHA512_HKDF_SHA512_AES_256_GCM",
            "DHKEM_P521_HKDF_SHA512_HKDF_SHA512_AES_256_GCM_RAW",
            "DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_AES_128_GCM",
            "DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_AES_128_GCM_RAW",
            "DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_AES_256_GCM",
            "DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_AES_256_GCM_RAW",
            "DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_CHACHA20_POLY1305",
            "DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_CHACHA20_POLY1305_RAW",
            "ECDSA_P256",
            "ECDSA_P256_IEEE_P1363",
            "ECDSA_P256_IEEE_P1363_WITHOUT_PREFIX",
            "ECDSA_P256_RAW",
            "ECDSA_P384",
            "ECDSA_P384_IEEE_P1363",
            "ECDSA_P384_SHA384",
            "ECDSA_P384_SHA512",
            "ECDSA_P521",
            "ECDSA_P521_IEEE_P1363",
            "ECIES_P256_COMPRESSED_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256",
            "ECIES_P256_COMPRESSED_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256_RAW",
            "ECIES_P256_COMPRESSED_HKDF_HMAC_SHA256_AES128_GCM",
            "ECIES_P256_COMPRESSED_HKDF_HMAC_SHA256_AES128_GCM_RAW",
            "ECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256",
            "ECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256_RAW",
            "ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM",
            "ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM_COMPRESSED_WITHOUT_PREFIX",
            "ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM_RAW",
            "ED25519",
            "ED25519WithRawOutput",
            "ED25519_RAW",
            "HKDF_SHA256",
            "HKDF_SHA256_DERIVES_AES128_GCM",
            "HKDF_SHA256_DERIVES_AES256_GCM",
            "HKDF_SHA256_DERIVES_AES256_GCM_HKDF_1MB",
            "HKDF_SHA256_DERIVES_AES256_SIV",
            "HKDF_SHA256_DERIVES_ED25519",
            "HKDF_SHA256_DERIVES_HMAC_SHA256_128BITTAG",
            "HKDF_SHA256_DERIVES_HMAC_SHA256_PRF",
            "HKDF_SHA256_DERIVES_XCHACHA20_POLY1305",
            "HMAC_SHA256_128BITTAG",
            "HMAC_SHA256_128BITTAG_RAW",
            "HMAC_SHA256_256BITTAG",
            "HMAC_SHA256_256BITTAG_RAW",
            "HMAC_SHA256_PRF",
            "HMAC_SHA512_128BITTAG",
            "HMAC_SHA512_128BITTAG_RAW",
            "HMAC_SHA512_256BITTAG",
            "HMAC_SHA512_256BITTAG_RAW",
            "HMAC_SHA512_512BITTAG",
            "HMAC_SHA512_512BITTAG_RAW",
            "HMAC_SHA512_PRF",
            "JWT_ES256",
            "JWT_ES256_RAW",
            "JWT_ES384",
            "JWT_ES384_RAW",
            "JWT_ES512",
            "JWT_ES512_RAW",
            "JWT_HS256",
            "JWT_HS256_RAW",
            "JWT_HS384",
            "JWT_HS384_RAW",
            "JWT_HS512",
            "JWT_HS512_RAW",
            "JWT_ML_DSA_44",
            "JWT_ML_DSA_44_RAW",
            "JWT_ML_DSA_65",
            "JWT_ML_DSA_65_RAW",
            "JWT_ML_DSA_87",
            "JWT_ML_DSA_87_RAW",
            "JWT_PS256_2048_F4",
            "JWT_PS256_2048_F4_RAW",
            "JWT_PS256_3072_F4",
            "JWT_PS256_3072_F4_RAW",
            "JWT_PS384_3072_F4",
            "JWT_PS384_3072_F4_RAW",
            "JWT_PS512_4096_F4",
            "JWT_PS512_4096_F4_RAW",
            "JWT_RS256_2048_F4",
            "JWT_RS256_2048_F4_RAW",
            "JWT_RS256_3072_F4",
            "JWT_RS256_3072_F4_RAW",
            "JWT_RS384_3072_F4",
            "JWT_RS384_3072_F4_RAW",
            "JWT_RS512_4096_F4",
            "JWT_RS512_4096_F4_RAW",
            "ML_DSA_44",
            "ML_DSA_44_RAW",
            "ML_DSA_65",
            "ML_DSA_65_RAW",
            "ML_DSA_87",
            "ML_DSA_87_RAW",
            "RSA_SSA_PKCS1_3072_SHA256_F4",
            "RSA_SSA_PKCS1_3072_SHA256_F4_RAW",
            "RSA_SSA_PKCS1_3072_SHA256_F4_WITHOUT_PREFIX",
            "RSA_SSA_PKCS1_4096_SHA512_F4",
            "RSA_SSA_PKCS1_4096_SHA512_F4_RAW",
            "RSA_SSA_PSS_3072_SHA256_F4",
            "RSA_SSA_PSS_3072_SHA256_F4_RAW",
            "RSA_SSA_PSS_3072_SHA256_SHA256_32_F4",
            "RSA_SSA_PSS_4096_SHA512_F4",
            "RSA_SSA_PSS_4096_SHA512_F4_RAW",
            "RSA_SSA_PSS_4096_SHA512_SHA512_64_F4",
            "SLH_DSA_SHA2_128S",
            "SLH_DSA_SHA2_128S_RAW",
            "XAES_256_GCM_160_BIT_NONCE_NO_PREFIX",
            "XAES_256_GCM_192_BIT_NONCE",
            "XAES_256_GCM_192_BIT_NONCE_NO_PREFIX",
            "XCHACHA20_POLY1305",
            "XCHACHA20_POLY1305_RAW",
            "X_AES_GCM_8_BYTE_SALT_NO_PREFIX")
        .inOrder();
  }
}
