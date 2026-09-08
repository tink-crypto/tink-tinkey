# A Homebrew formula for Tinkey on Linux and macOS.
# Usage:
# brew tap tink-crypto/tink-tinkey https://github.com/tink-crypto/tink-tinkey
# brew install tinkey

class Tinkey < Formula
  desc "A command line tool to generate and manipulate keysets for the Tink cryptography library"
  homepage "https://github.com/tink-crypto/tink-tinkey"
  url "https://storage.googleapis.com/tinkey/tinkey-1.14.0.tar.gz"
  sha256 "177a31279317eabbdf2745560026ea78aaf48695a8bd5ea9ebc47e2e529be323"

  def install
    bin.install "tinkey"
    bin.install "tinkey_deploy.jar"
  end
end
