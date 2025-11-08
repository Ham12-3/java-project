#!/usr/bin/env bash

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

command_exists() {
    command -v "$1" >/dev/null 2>&1
}

require_command() {
    local cmd="$1"
    if ! command_exists "$cmd"; then
        echo "[error] Missing required command: ${cmd}" >&2
        exit 1
    fi
    echo "[ok] ${cmd} detected"
}

echo "[info] Checking required toolchain..."
for tool in java mvn node npm docker; do
    require_command "$tool"
done

if docker compose version >/dev/null 2>&1; then
    echo "[ok] docker compose detected"
else
    echo "[error] docker compose (plugin) not found. Please install Docker Compose V2." >&2
    exit 1
fi

install_git_hook() {
    local hook_path="${REPO_ROOT}/.git/hooks/pre-commit"
    if [ ! -d "${REPO_ROOT}/.git" ]; then
        echo "[warn] .git directory not found; skipping hook installation"
        return
    fi

    cat >"$hook_path" <<'HOOK'
#!/usr/bin/env bash
set -euo pipefail

if [ -x "./scripts/pre-commit.sh" ]; then
    ./scripts/pre-commit.sh
else
    echo "pre-commit hook skipped (scripts/pre-commit.sh not executable)" >&2
fi
HOOK
    chmod +x "$hook_path"
    echo "[ok] Installed pre-commit hook"
}

install_git_hook

echo "[info] Ensuring local environment examples exist..."
cp -n "${REPO_ROOT}/services/auth-service/src/main/resources/application.yml" \
    "${REPO_ROOT}/services/auth-service/src/main/resources/application.yml.example" \
    2>/dev/null || true

echo "[done] Bootstrap complete. You can now continue with README step 2."
