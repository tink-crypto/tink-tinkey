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
import static org.junit.Assert.assertThrows;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.RegistryConfiguration;
import com.google.crypto.tink.TinkJsonProtoKeysetFormat;
import com.google.crypto.tink.TinkProtoKeysetFormat;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.PredefinedAeadParameters;
import com.google.crypto.tink.mac.MacConfig;
import com.google.crypto.tink.mac.PredefinedMacParameters;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@code delete-key}, {@code disable-key}, {@code enable-key}, and {@code promote-key}.
 */
@RunWith(JUnit4.class)
public final class ManipulateKeyCommandsTest {
  private static final int PRIMARY_KEY_ID = 10;
  private static final int NON_PRIMARY_KEY_ID = 20;

  @BeforeClass
  public static void setUp() throws Exception {
    AeadConfig.register();
    MacConfig.register();
    KmsClientsFactory.globalInstance().addFactory(TinkeyTestKmsClient::new);
  }

  private static KeysetHandle createTwoKeyKeyset(KeyStatus secondKeyStatus) throws Exception {
    return KeysetHandle.newBuilder()
        .setConfiguration(RegistryConfiguration.get())
        .addEntry(
            KeysetHandle.generateEntryFromParameters(PredefinedMacParameters.HMAC_SHA256_128BITTAG)
                .withFixedId(PRIMARY_KEY_ID)
                .makePrimary())
        .addEntry(
            KeysetHandle.generateEntryFromParameters(PredefinedMacParameters.HMAC_SHA256_256BITTAG)
                .withFixedId(NON_PRIMARY_KEY_ID)
                .setStatus(secondKeyStatus))
        .build();
  }

  @Test
  public void deleteKey_nonPrimary_removesKey() throws Exception {
    Path path = Files.createTempDirectory(/* prefix= */ "");
    Path inputFile = Path.of(path.toString(), "input");
    Path outputFile = Path.of(path.toString(), "output");

    KeysetHandle inputKeyset = createTwoKeyKeyset(KeyStatus.ENABLED);
    Files.writeString(
        inputFile,
        TinkJsonProtoKeysetFormat.serializeKeyset(
            inputKeyset, RegistryConfiguration.get(), InsecureSecretKeyAccess.get()));

    Tinkey.main(
        new String[] {
          "delete-key",
          "--in",
          inputFile.toString(),
          "--out",
          outputFile.toString(),
          "--key-id",
          Integer.toString(NON_PRIMARY_KEY_ID),
        });

    KeysetHandle handle =
        TinkJsonProtoKeysetFormat.parseKeyset(
            Files.readString(outputFile),
            RegistryConfiguration.get(),
            InsecureSecretKeyAccess.get());

    assertThat(handle.size()).isEqualTo(1);
    assertThat(handle.getAt(0).getId()).isEqualTo(PRIMARY_KEY_ID);
    assertThat(handle.getAt(0).isPrimary()).isTrue();
    assertThat(handle.getAt(0).getKey().equalsKey(inputKeyset.getAt(0).getKey())).isTrue();
  }

  @Test
  public void deleteKey_primary_throws() throws Exception {
    Path path = Files.createTempDirectory(/* prefix= */ "");
    Path inputFile = Path.of(path.toString(), "input");
    Path outputFile = Path.of(path.toString(), "output");

    KeysetHandle inputKeyset = createTwoKeyKeyset(KeyStatus.ENABLED);
    Files.write(
        inputFile,
        TinkProtoKeysetFormat.serializeKeyset(
            inputKeyset, InsecureSecretKeyAccess.get(), RegistryConfiguration.get()));

    assertThrows(
        GeneralSecurityException.class,
        () ->
            Tinkey.main(
                new String[] {
                  "delete-key",
                  "--in",
                  inputFile.toString(),
                  "--in-format",
                  "binary",
                  "--out",
                  outputFile.toString(),
                  "--out-format",
                  "binary",
                  "--key-id",
                  Integer.toString(PRIMARY_KEY_ID),
                }));
  }

  @Test
  public void deleteKey_unknownKeyId_throws() throws Exception {
    Path path = Files.createTempDirectory(/* prefix= */ "");
    Path inputFile = Path.of(path.toString(), "input");
    Path outputFile = Path.of(path.toString(), "output");

    KeysetHandle inputKeyset = createTwoKeyKeyset(KeyStatus.ENABLED);
    Files.write(
        inputFile,
        TinkProtoKeysetFormat.serializeKeyset(
            inputKeyset, InsecureSecretKeyAccess.get(), RegistryConfiguration.get()));

    assertThrows(
        GeneralSecurityException.class,
        () ->
            Tinkey.main(
                new String[] {
                  "delete-key",
                  "--in",
                  inputFile.toString(),
                  "--in-format",
                  "binary",
                  "--out",
                  outputFile.toString(),
                  "--out-format",
                  "binary",
                  "--key-id",
                  "999",
                }));
  }

  @Test
  public void disableKey_nonPrimary_disablesKey() throws Exception {
    Path path = Files.createTempDirectory(/* prefix= */ "");
    Path inputFile = Path.of(path.toString(), "input");
    Path outputFile = Path.of(path.toString(), "output");

    KeysetHandle inputKeyset = createTwoKeyKeyset(KeyStatus.ENABLED);
    Files.write(
        inputFile,
        TinkProtoKeysetFormat.serializeKeyset(
            inputKeyset, InsecureSecretKeyAccess.get(), RegistryConfiguration.get()));

    Tinkey.main(
        new String[] {
          "disable-key",
          "--in",
          inputFile.toString(),
          "--in-format",
          "binary",
          "--out",
          outputFile.toString(),
          "--out-format",
          "binary",
          "--key-id",
          Integer.toString(NON_PRIMARY_KEY_ID),
        });

    KeysetHandle handle =
        TinkProtoKeysetFormat.parseKeyset(
            Files.readAllBytes(outputFile),
            InsecureSecretKeyAccess.get(),
            RegistryConfiguration.get());

    assertThat(handle.size()).isEqualTo(2);
    assertThat(handle.getAt(0).getId()).isEqualTo(PRIMARY_KEY_ID);
    assertThat(handle.getAt(0).getStatus()).isEqualTo(KeyStatus.ENABLED);
    assertThat(handle.getAt(1).getId()).isEqualTo(NON_PRIMARY_KEY_ID);
    assertThat(handle.getAt(1).getStatus()).isEqualTo(KeyStatus.DISABLED);
  }

  @Test
  public void disableKey_primary_throws() throws Exception {
    Path path = Files.createTempDirectory(/* prefix= */ "");
    Path inputFile = Path.of(path.toString(), "input");
    Path outputFile = Path.of(path.toString(), "output");

    KeysetHandle inputKeyset = createTwoKeyKeyset(KeyStatus.ENABLED);
    Files.write(
        inputFile,
        TinkProtoKeysetFormat.serializeKeyset(
            inputKeyset, InsecureSecretKeyAccess.get(), RegistryConfiguration.get()));

    assertThrows(
        GeneralSecurityException.class,
        () ->
            Tinkey.main(
                new String[] {
                  "disable-key",
                  "--in",
                  inputFile.toString(),
                  "--in-format",
                  "binary",
                  "--out",
                  outputFile.toString(),
                  "--out-format",
                  "binary",
                  "--key-id",
                  Integer.toString(PRIMARY_KEY_ID),
                }));
  }

  @Test
  public void enableKey_disabledKey_enablesKey() throws Exception {
    Path path = Files.createTempDirectory(/* prefix= */ "");
    Path inputFile = Path.of(path.toString(), "input");
    Path outputFile = Path.of(path.toString(), "output");

    KeysetHandle inputKeyset = createTwoKeyKeyset(KeyStatus.DISABLED);
    Files.write(
        inputFile,
        TinkProtoKeysetFormat.serializeKeyset(
            inputKeyset, InsecureSecretKeyAccess.get(), RegistryConfiguration.get()));

    Tinkey.main(
        new String[] {
          "enable-key",
          "--in",
          inputFile.toString(),
          "--in-format",
          "binary",
          "--out",
          outputFile.toString(),
          "--out-format",
          "binary",
          "--key-id",
          Integer.toString(NON_PRIMARY_KEY_ID),
        });

    KeysetHandle handle =
        TinkProtoKeysetFormat.parseKeyset(
            Files.readAllBytes(outputFile),
            InsecureSecretKeyAccess.get(),
            RegistryConfiguration.get());

    assertThat(handle.size()).isEqualTo(2);
    assertThat(handle.getAt(1).getId()).isEqualTo(NON_PRIMARY_KEY_ID);
    assertThat(handle.getAt(1).getStatus()).isEqualTo(KeyStatus.ENABLED);
  }

  @Test
  public void promoteKey_enabledKeyEncrypted_setsNewPrimary() throws Exception {
    Path path = Files.createTempDirectory(/* prefix= */ "");
    Path inputFile = Path.of(path.toString(), "input");
    Path outputFile = Path.of(path.toString(), "output");
    Path credentialFile = Path.of(path.toString(), "credentials");
    TinkeyTestKmsClient.createCredentialFile(credentialFile);

    KeysetHandle masterKeyAeadKeyset =
        KeysetHandle.generateNew(PredefinedAeadParameters.AES128_GCM, RegistryConfiguration.get());
    Aead masterKeyAead = masterKeyAeadKeyset.getPrimitive(RegistryConfiguration.get(), Aead.class);
    String masterKeyUri = TinkeyTestKmsClient.createKeyUri(masterKeyAeadKeyset);

    KeysetHandle inputKeyset = createTwoKeyKeyset(KeyStatus.ENABLED);
    Files.write(
        inputFile,
        TinkProtoKeysetFormat.serializeEncryptedKeyset(
            inputKeyset, masterKeyAead, new byte[] {}, RegistryConfiguration.get()));

    Tinkey.main(
        new String[] {
          "promote-key",
          "--in",
          inputFile.toString(),
          "--in-format",
          "binary",
          "--out",
          outputFile.toString(),
          "--out-format",
          "binary",
          "--key-id",
          Integer.toString(NON_PRIMARY_KEY_ID),
          "--master-key-uri",
          masterKeyUri,
          "--credential",
          credentialFile.toString(),
        });

    KeysetHandle handle =
        TinkProtoKeysetFormat.parseEncryptedKeyset(
            Files.readAllBytes(outputFile),
            masterKeyAead,
            new byte[] {},
            RegistryConfiguration.get());

    assertThat(handle.size()).isEqualTo(2);
    assertThat(handle.getAt(0).isPrimary()).isFalse();
    assertThat(handle.getAt(1).isPrimary()).isTrue();
    assertThat(handle.getPrimary().getId()).isEqualTo(NON_PRIMARY_KEY_ID);
  }

  @Test
  public void promoteKey_disabledKey_throws() throws Exception {
    Path path = Files.createTempDirectory(/* prefix= */ "");
    Path inputFile = Path.of(path.toString(), "input");
    Path outputFile = Path.of(path.toString(), "output");

    KeysetHandle inputKeyset = createTwoKeyKeyset(KeyStatus.DISABLED);
    Files.write(
        inputFile,
        TinkProtoKeysetFormat.serializeKeyset(
            inputKeyset, InsecureSecretKeyAccess.get(), RegistryConfiguration.get()));

    assertThrows(
        GeneralSecurityException.class,
        () ->
            Tinkey.main(
                new String[] {
                  "promote-key",
                  "--in",
                  inputFile.toString(),
                  "--in-format",
                  "binary",
                  "--out",
                  outputFile.toString(),
                  "--out-format",
                  "binary",
                  "--key-id",
                  Integer.toString(NON_PRIMARY_KEY_ID),
                }));
  }
}
