# A Homebrew formula for Tinkey on Linux and macOS.
# Usage:
# brew tap tink-crypto/tink-tinkey https://github.com/tink-crypto/tink-tinkey
# brew install tinkey

class Tinkey < Formula
  desc "A command line tool to generate and manipulate keysets for the Tink cryptography library"
  homepage "https://github.com/tink-crypto/tink-tinkey"
  url "https://storage.googleapis.com/tinkey/tinkey-1.10.1.tar.gz"
  sha256 "3c0b83b85684af0b700f571540ef6a45460f44092cb5afca5e395b932e48d84c"

  def install
    bin.install "tinkey"
    bin.install "tinkey_deploy.jar"
  end
end
