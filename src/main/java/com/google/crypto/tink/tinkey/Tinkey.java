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

import com.google.crypto.tink.daead.DeterministicAeadConfig;
import com.google.crypto.tink.hybrid.HybridConfig;
import com.google.crypto.tink.integration.gcpkms.GcpKmsClient;
import com.google.crypto.tink.jwt.JwtMacConfig;
import com.google.crypto.tink.jwt.JwtSignatureConfig;
import com.google.crypto.tink.keyderivation.KeyDerivationConfig;
import com.google.crypto.tink.prf.PrfConfig;
import com.google.crypto.tink.signature.MlDsaSignKeyManager;
import com.google.crypto.tink.signature.SignatureConfig;
import com.google.crypto.tink.signature.SlhDsaSignKeyManager;
import com.google.crypto.tink.signature.internal.MlDsaSignConscrypt;
import com.google.crypto.tink.signature.internal.SlhDsaSignConscrypt;
import com.google.crypto.tink.streamingaead.StreamingAeadConfig;
import java.security.Security;
import org.conscrypt.Conscrypt;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.google.crypto.tink.integration.awskms.AwsKmsClient;

/** Tinkey is a command-line tool to manage keys for Tink. */
public final class Tinkey {
  public static void main(String[] args) throws Exception {
    try {
      Conscrypt.checkAvailability();
      Security.addProvider(Conscrypt.newProvider());
    } catch (Throwable cause) {
      // Conscrypt is optional if not using algorithms requiring it.
    }

    DeterministicAeadConfig.register();
    HybridConfig.register(); // includes Aead and Mac
    PrfConfig.register();
    SignatureConfig.register();
    if (MlDsaSignConscrypt.isSupported()) {
      MlDsaSignKeyManager.registerPair();
    }
    if (SlhDsaSignConscrypt.isSupported()) {
      SlhDsaSignKeyManager.registerPair();
    }
    StreamingAeadConfig.register();
    JwtSignatureConfig.register();
    JwtMacConfig.register();
    KeyDerivationConfig.register();
    // Placeholder for Internal Prps. DO NOT EDIT.

    KmsClientsFactory.globalInstance().addFactory(GcpKmsClient::new);
    KmsClientsFactory.globalInstance().addFactory(AwsKmsClient::new);

    TinkeyCommands commands = new TinkeyCommands();
    CmdLineParser parser = new CmdLineParser(commands);

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.out.println("Argument wrong!");
      System.out.println(e);
      System.out.println();
      e.getParser().printUsage(System.out);
      System.exit(1);
    }
    commands.command.run();
  }

  private Tinkey() {}
}
