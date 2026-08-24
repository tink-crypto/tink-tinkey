// Copyright 2017 Google Inc.
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

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.RegistryConfiguration;
import com.google.crypto.tink.TinkJsonProtoKeysetFormat;
import com.google.crypto.tink.TinkProtoKeysetFormat;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.PredefinedAeadParameters;
import com.google.crypto.tink.mac.MacConfig;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/** Tests for {@code CreateKeysetCommand}. */
@RunWith(Theories.class)
public class CreateKeysetCommandTest {
  @DataPoints("templates")
  public static final String[] templates =
      new String[] {
        "HMAC_SHA256_128BITTAG",
        "ML_DSA_44",
        "SLH_DSA_SHA2_128S_RAW",
      };

  @BeforeClass
  public static void setUp() throws Exception {
    AeadConfig.register();
    MacConfig.register();
    KmsClientsFactory.globalInstance().addFactory(TinkeyTestKmsClient::new);
  }

  @Theory
  public void testCreateCleartext_shouldCreateNewKeyset(
      @FromDataPoints("templates") String templateName) throws Exception {
    Path path = Files.createTempDirectory(/* prefix= */ "");
    Path outputFile = Paths.get(path.toString(), "keyset");

    Tinkey.main(
        new String[] {
          "create-keyset", "--key-template", templateName, "--out", outputFile.toString()
        });

    KeysetHandle handle =
        TinkJsonProtoKeysetFormat.parseKeyset(
            new String(Files.readAllBytes(outputFile), UTF_8), InsecureSecretKeyAccess.get());

    assertThat(handle.size()).isEqualTo(1);
    assertThat(handle.getPrimary().getKey().getParameters())
        .isEqualTo(KeyTemplates.get(templateName).toParameters());
  }

  @Theory
  public void testCreateCleartext_explicitJson_shouldCreateNewKeyset(
      @FromDataPoints("templates") String templateName) throws Exception {
    Path path = Files.createTempDirectory(/* prefix= */ "");
    Path outputFile = Paths.get(path.toString(), "keyset");

    String commandLine =
        String.format(
            "create-keyset --key-template %s --out-format json --out %s",
            templateName, outputFile.toString());

    Tinkey.main(commandLine.split(" "));

    KeysetHandle handle =
        TinkJsonProtoKeysetFormat.parseKeyset(
            new String(Files.readAllBytes(outputFile), UTF_8), InsecureSecretKeyAccess.get());

    assertThat(handle.size()).isEqualTo(1);
    assertThat(handle.getPrimary().getKey().getParameters())
        .isEqualTo(KeyTemplates.get(templateName).toParameters());
  }

  @Theory
  public void testCreateCleartext_binary_shouldCreateNewKeyset(
      @FromDataPoints("templates") String templateName) throws Exception {
    Path path = Files.createTempDirectory(/* prefix= */ "");
    Path outputFile = Paths.get(path.toString(), "keyset");

    String commandLine =
        String.format(
            "create-keyset --key-template %s --out-format binary --out %s",
            templateName, outputFile);

    Tinkey.main(commandLine.split(" "));

    KeysetHandle handle =
        TinkProtoKeysetFormat.parseKeyset(
            Files.readAllBytes(outputFile), InsecureSecretKeyAccess.get());

    assertThat(handle.size()).isEqualTo(1);
    assertThat(handle.getPrimary().getKey().getParameters())
        .isEqualTo(KeyTemplates.get(templateName).toParameters());
  }

  @Theory
  public void testCreateCleartext_withMasterKey_shouldCreateNewKeyset(
      @FromDataPoints("templates") String templateName) throws Exception {
    Path path = Files.createTempDirectory(/* prefix= */ "");
    Path outputFile = Paths.get(path.toString(), "keyset");
    Path credentialFile = Paths.get(path.toString(), "credentials");
    TinkeyTestKmsClient.createCredentialFile(credentialFile);

    KeysetHandle masterKeyAeadKeyset =
        KeysetHandle.generateNew(PredefinedAeadParameters.AES128_GCM);
    Aead masterKeyAead = masterKeyAeadKeyset.getPrimitive(RegistryConfiguration.get(), Aead.class);
    String masterKeyUri = TinkeyTestKmsClient.createKeyUri(masterKeyAeadKeyset);

    String commandLine =
        String.format(
            "create-keyset --key-template %s --out-format binary "
                + "--master-key-uri %s "
                + "--credential %s "
                + "--out %s",
            templateName, masterKeyUri, credentialFile, outputFile.toString());

    Tinkey.main(commandLine.split(" "));

    KeysetHandle handle =
        TinkProtoKeysetFormat.parseEncryptedKeyset(
            Files.readAllBytes(outputFile), masterKeyAead, new byte[] {});

    assertThat(handle.size()).isEqualTo(1);
    assertThat(handle.getPrimary().getKey().getParameters())
        .isEqualTo(KeyTemplates.get(templateName).toParameters());
  }

  @Theory
  public void testCreateCleartext_withMasterKey_jsonFormat_shouldCreateNewKeyset(
      @FromDataPoints("templates") String templateName) throws Exception {
    Path path = Files.createTempDirectory(/* prefix= */ "");
    Path outputFile = Paths.get(path.toString(), "keyset");
    Path credentialFile = Paths.get(path.toString(), "credentials");
    TinkeyTestKmsClient.createCredentialFile(credentialFile);

    KeysetHandle masterKeyAeadKeyset =
        KeysetHandle.generateNew(PredefinedAeadParameters.AES128_GCM);
    Aead masterKeyAead = masterKeyAeadKeyset.getPrimitive(RegistryConfiguration.get(), Aead.class);
    String masterKeyUri = TinkeyTestKmsClient.createKeyUri(masterKeyAeadKeyset);

    String commandLine =
        String.format(
            "create-keyset --key-template %s --out-format json "
                + "--master-key-uri %s "
                + "--credential %s "
                + "--out %s",
            templateName, masterKeyUri, credentialFile, outputFile.toString());

    Tinkey.main(commandLine.split(" "));

    KeysetHandle handle =
        TinkJsonProtoKeysetFormat.parseEncryptedKeyset(
            new String(Files.readAllBytes(outputFile), UTF_8), masterKeyAead, new byte[] {});

    assertThat(handle.size()).isEqualTo(1);
    assertThat(handle.getPrimary().getKey().getParameters())
        .isEqualTo(KeyTemplates.get(templateName).toParameters());
  }
}
