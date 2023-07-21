# A Homebrew formula for Tinkey on Linux and macOS.
# Usage:
# brew tap tink-crypto/tink-tinkey https://github.com/tink-crypto/tink-tinkey
# brew install tinkey

class Tinkey < Formula
  desc "A command line tool to generate and manipulate keysets for the Tink cryptography library"
  homepage "https://github.com/tink-crypto/tink-tinkey"
  url "https://storage.googleapis.com/tinkey/tinkey-1.9.0.tar.gz"
  sha256 "fd00a2f7839e135224860b4d40069918f356ace3965224576c9336d4daecd374"

  def install
    bin.install "tinkey"
    bin.install "tinkey_deploy.jar"
  end
end
