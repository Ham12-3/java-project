#!/usr/bin/env bash

set -euo pipefail

echo "[info] Running Maven verify (tests skipped)..."
mvn -pl services/auth-service -DskipTests verify >/dev/null
echo "[ok] Maven verify passed"
