# A Homebrew formula for Tinkey on Linux and macOS.
# Usage:
# brew tap tink-crypto/tink-tinkey https://github.com/tink-crypto/tink-tinkey
# brew install tinkey

class Tinkey < Formula
  desc "A command line tool to generate and manipulate keysets for the Tink cryptography library"
  homepage "https://github.com/tink-crypto/tink-tinkey"
  url "https://storage.googleapis.com/tinkey/tinkey-1.13.0.tar.gz"
  sha256 "293010ccc0adcbb51a19ca51bb67f6aea8a849ac8c6dabc2544087d79122226f"

  def install
    bin.install "tinkey"
    bin.install "tinkey_deploy.jar"
  end
end
