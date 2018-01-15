#
# Copyright (C) 2016 Stormpath, Inc.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

#!/usr/bin/env bash

# This script is initially based on Mike McQuaid's strap project, with additions:
# https://github.com/MikeMcQuaid/strap

set -e

# Keep sudo timestamp updated while Strap is running.
if [ "$1" = "--sudo-wait" ]; then
  while true; do
    mkdir -p "/var/db/sudo/$SUDO_USER"
    touch "/var/db/sudo/$SUDO_USER"
    sleep 1
  done
  exit 0
fi

[ "$1" = "--debug" ] && STRAP_DEBUG="1"
STRAP_SUCCESS=""

cleanup() {
  set +e
  if [ -n "$STRAP_SUDO_WAIT_PID" ]; then
    sudo kill "$STRAP_SUDO_WAIT_PID"
  fi
  sudo -k
  rm -f "$CLT_PLACEHOLDER"
  if [ -z "$STRAP_SUCCESS" ]; then
    if [ -n "$STRAP_STEP" ]; then
      echo "!!! $STRAP_STEP FAILED" >&2
    else
      echo "!!! FAILED" >&2
    fi
    if [ -z "$STRAP_DEBUG" ]; then
      echo "!!! Run '$0 --debug' for debugging output." >&2
      echo "!!! If you're stuck: file an issue with debugging output at:" >&2
      echo "!!!   $STRAP_ISSUES_URL" >&2
    fi
  fi
}

trap "cleanup" EXIT

if [ -n "$STRAP_DEBUG" ]; then
  set -x
else
  STRAP_QUIET_FLAG="-q"
  Q="$STRAP_QUIET_FLAG"
fi

STDIN_FILE_DESCRIPTOR="0"
[ -t "$STDIN_FILE_DESCRIPTOR" ] && STRAP_INTERACTIVE="1"

STRAP_FULL_PATH="$(cd "$(dirname "$0")" && pwd)/$(basename "$0")"

abort() { STRAP_STEP="";   echo "!!! $*" >&2; exit 1; }
log()   { STRAP_STEP="$*"; echo "--> $*"; }
logn()  { STRAP_STEP="$*"; printf -- "--> $* "; }
logk()  { STRAP_STEP="";   echo "OK"; }

sw_vers -productVersion | grep $Q -E "^10.(9|10|11|12)" || {
  abort "Run Strap on Mac OS X 10.9/10/11/12."
}

[ "$USER" = "root" ] && abort "Run Strap as yourself, not root."
groups | grep $Q admin || abort "Add $USER to the admin group."

# Initialise sudo now to save prompting later.
log "Enter your password (for sudo access):"
sudo -k
sudo /usr/bin/true
[ -f "$STRAP_FULL_PATH" ]
sudo bash "$STRAP_FULL_PATH" --sudo-wait &
STRAP_SUDO_WAIT_PID="$!"
ps -p "$STRAP_SUDO_WAIT_PID" &>/dev/null
logk

logn "Checking ~/.bash_profile:"
if [ -f "$HOME/.bash_profile" ]; then
  logk
else
  echo
  log "Creating ~/.bash_profile..."
  touch ~/.bash_profile
  logk
fi

# Homebrew
logn "Checking Homebrew:"
if command -v brew >/dev/null 2>&1; then
  logk
else
  echo
  log "Installing Homebrew..."
  HOMEBREW_PREFIX="/usr/local"
  [ -d "$HOMEBREW_PREFIX" ] || sudo mkdir -p "$HOMEBREW_PREFIX"
  sudo chown -R "$(logname):admin" "$HOMEBREW_PREFIX"
  yes '' | /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)";

  if [[ "$PATH" != *"/usr/local/bin"* ]]; then
    echo '' >> ~/.bash_profile;
    echo '# homebrew' >> ~/.bash_profile;
    echo 'export PATH="/usr/local/bin:$PATH"' >> ~/.bash_profile;
    source "$HOME/.bash_profile"
  fi

  logk
fi

logn "Checking Homebrew Cask:"
if brew tap | grep caskroom/cask >/dev/null 2>&1; then
  logk
else
  echo
  log "Tapping caskroom/cask..."
  brew tap caskroom/cask
  logk
fi

logn "Checking Homebrew updates:"
brew update
brew upgrade

logn "Checking httpie:"
if brew list | grep httpie >/dev/null 2>&1; then
  logk
else
  echo
  log "Installing httpie..."
  brew install httpie
  logk
fi


logn "Checking java:"
if brew cask list | grep java >/dev/null 2>&1; then
  logk
else
  echo
  log "Installing java..."
  brew cask install java
  logk
fi

logn "Checking JAVA_HOME:"
if grep -q JAVA_HOME "$HOME/.bash_profile"; then
  logk
else
  echo
  log "Setting JAVA_HOME..."
  echo '' >> ~/.bash_profile
  echo '# JAVA_HOME' >> ~/.bash_profile
  echo 'export JAVA_HOME="$(/usr/libexec/java_home)"' >> ~/.bash_profile
  echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.bash_profile
  logk
fi

[ -z "$JAVA_HOME" ] && JAVA_HOME="$(/usr/libexec/java_home)"
[ -z "$JAVA_HOME" ] && abort "JAVA_HOME cannot be determined."

logn "Checking java unlimited cryptography:"
JCE_DIR="$JAVA_HOME/jre/lib/security"
if [ -f "$JCE_DIR/local_policy.jar.orig" ]; then
  logk
else
  echo
  log "Installing java unlimited cryptography..."
  cd $JCE_DIR
  # backup existing JVM files that we will replace just in case:
  sudo mv local_policy.jar local_policy.jar.orig
  sudo mv US_export_policy.jar US_export_policy.jar.orig
  sudo curl -sLO 'http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip' -H 'Cookie: oraclelicense=accept-securebackup-cookie'
  sudo unzip jce_policy-8.zip
  sudo mv UnlimitedJCEPolicyJDK8/US_export_policy.jar .
  sudo mv UnlimitedJCEPolicyJDK8/local_policy.jar .
  sudo chown root:wheel US_export_policy.jar
  sudo chown root:wheel local_policy.jar
  # cleanup download file:
  sudo rm -rf jce_policy-8.zip
  sudo rm -rf UnlimitedJCEPolicyJDK8
  cd ~
  logk
fi

logn "Checking jenv:"
if brew list | grep jenv >/dev/null 2>&1; then
  logk
else
  echo
  log "Installing jenv..."
  brew install jenv

  if ! grep -q jenv "$HOME/.bash_profile"; then
    echo '' >> ~/.bash_profile;
    echo '# jenv' >> ~/.bash_profile;
    echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.bash_profile;
    echo 'if command -v jenv >/dev/null; then eval "$(jenv init -)"; fi;' >> ~/.bash_profile;
  fi

  export PATH="$HOME/.jenv/bin:$PATH"
  eval "$(jenv init -)"
  jenv add "$(/usr/libexec/java_home)"
  jenv global 1.8
  jenv enable-plugin export
  jenv enable-plugin maven
  jenv enable-plugin groovy
  jenv enable-plugin springboot
  logk
fi

logn "Checking maven:"
if brew list | grep maven >/dev/null 2>&1; then
  logk
else
  echo
  log "Installing maven..."
  brew install maven
  logk
fi

STRAP_SUCCESS="1"
log "Your system is now Strap'd!"
