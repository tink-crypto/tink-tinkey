# A Homebrew formula for Tinkey on Linux and macOS.
# Usage:
# brew tap tink-crypto/tink-tinkey https://github.com/tink-crypto/tink-tinkey
# brew install tinkey

class Tinkey < Formula
  desc "A command line tool to generate and manipulate keysets for the Tink cryptography library"
  homepage "https://github.com/tink-crypto/tink-tinkey"
  url "https://storage.googleapis.com/tinkey/tinkey-1.14.1.tar.gz"
  sha256 "e6aadbc5c31e4e6d3c2c2671f44191f1e7b85126a59cb14648b76666e599395b"

  def install
    bin.install "tinkey"
    bin.install "tinkey_deploy.jar"
  end
end
