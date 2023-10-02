# A Homebrew formula for Tinkey on Linux and macOS.
# Usage:
# brew tap tink-crypto/tink-tinkey https://github.com/tink-crypto/tink-tinkey
# brew install tinkey

class Tinkey < Formula
  desc "A command line tool to generate and manipulate keysets for the Tink cryptography library"
  homepage "https://github.com/tink-crypto/tink-tinkey"
  url "https://storage.googleapis.com/tinkey/tinkey-1.10.0.tar.gz"
  sha256 "9b22c0be8d9712297fbfea9e460ec32aedf545179d7fc3fa1c2424e0994bf9f8"

  def install
    bin.install "tinkey"
    bin.install "tinkey_deploy.jar"
  end
end
