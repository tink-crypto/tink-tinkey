# A Homebrew formula for Tinkey on Linux and macOS.
# Usage:
# brew tap tink-crypto/tink-tinkey https://github.com/tink-crypto/tink-tinkey
# brew install tinkey

class Tinkey < Formula
  desc "A command line tool to generate and manipulate keysets for the Tink cryptography library"
  homepage "https://github.com/tink-crypto/tink-tinkey"
  url "https://storage.googleapis.com/tinkey/tinkey-1.12.0.tar.gz"
  sha256 "9d908b457a2c0612fafd18bb351e05bbe85f604c5b9abe61809f5eb06280fc56"

  def install
    bin.install "tinkey"
    bin.install "tinkey_deploy.jar"
  end
end
