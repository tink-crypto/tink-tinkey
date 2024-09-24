# A Homebrew formula for Tinkey on Linux and macOS.
# Usage:
# brew tap tink-crypto/tink-tinkey https://github.com/tink-crypto/tink-tinkey
# brew install tinkey

class Tinkey < Formula
  desc "A command line tool to generate and manipulate keysets for the Tink cryptography library"
  homepage "https://github.com/tink-crypto/tink-tinkey"
  url "https://storage.googleapis.com/tinkey/tinkey-1.11.0.tar.gz"
  sha256 "425a551254847323078aaa80c0087bb228d7672e0c8663807aa06c50dcffa75a"

  def install
    bin.install "tinkey"
    bin.install "tinkey_deploy.jar"
  end
end
