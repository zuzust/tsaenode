#!/usr/bin/env bash

# vim:ft=sh
# File: install.sh


VERSION=1.0

INSTALL_DIR="${HOME}/Applications/tsaenode"

TSAENODE_HOME=${INSTALL_DIR}
JAR_DIR="${PWD}/tsaenode-command/target"
JAR_FILE="${JAR_DIR}/tsaenode-command-jar-with-dependencies.jar"

MVN=$(which mvn)
JAVA=$(which java)


if [[ -z "${MVN}" || -z "${JAVA}" ]]; then
  echo "[TSAEnode] Requirements not met."
  echo "[TSAEnode] Visit <https://github.com/zuzust/tsaenode> for further information."
  exit -1
fi

export TSAENODE_HOME

echo "[TSAEnode] Compiling sources..."
mvn clean package -Dmaven.test.skip=true

echo
echo "[TSAEnode] Installing executables..."
[[ ! -d "${TSAENODE_HOME}" ]] && mkdir -p "${TSAENODE_HOME}"
cp "${JAR_FILE}" "${TSAENODE_HOME}/tsaenode-${VERSION}.jar" \
  && cp "${PWD}/tsaenode" "${TSAENODE_HOME}/tsaenode" \
  && cp "${PWD}/tsaenode-sim" "${TSAENODE_HOME}/tsaenode-sim" \
  && cp "${PWD}/tsaenode.conf" "${TSAENODE_HOME}/tsaenode.conf" \
  && cp "${PWD}/tsaenode-sim.conf" "${TSAENODE_HOME}/tsaenode-sim.conf" \
  && chmod +x "${TSAENODE_HOME}/tsaenode" \
  && chmod +x "${TSAENODE_HOME}/tsaenode-sim"

echo
echo "[TSAEnode] Adding env variables to ${HOME}/.bashrc..."
echo >> "${HOME}/.bashrc"
echo "### Added by TSAEnode" >> "${HOME}/.bashrc"
echo "export TSAENODE_HOME=\"${TSAENODE_HOME}\"" >> "${HOME}/.bashrc"
echo "export PATH=\"${TSAENODE_HOME}:\$PATH\"" >> "${HOME}/.bashrc"

echo
echo "[TSAEnode] Installation complete."
echo "[TSAEnode] Remember to source ${HOME}/.bashrc the very first time."
echo "[TSAEnode] Visit <https://github.com/zuzust/tsaenode> for further information."

unset JAVA MVN JAR_FILE JAR_DIR INSTALL_DIR VERSION
