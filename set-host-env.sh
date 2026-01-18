#!/usr/bin/env bash
set -e

ENV_FILE=".env"

# 1) Detect Windows host IPv4 (language-independent)
HOST_IP=$(
  ipconfig.exe \
  | tr -d '\r' \
  | grep -oE '([0-9]{1,3}\.){3}[0-9]{1,3}' \
  | grep -vE '^169\.254\.|^127\.' \
  | head -n 1
)

if [ -z "$HOST_IP" ]; then
  echo "ERROR: HOST IPv4 not found (ipconfig parsing failed)." >&2
  exit 1
fi

echo "Detected HOST IP: $HOST_IP"

# 2) Update/insert HOST= in .env
if [ ! -f "$ENV_FILE" ]; then
  echo "ERROR: .env not found in current directory." >&2
  exit 1
fi

grep -v '^HOST=' "$ENV_FILE" > "$ENV_FILE.tmp" || true
echo "HOST=$HOST_IP" >> "$ENV_FILE.tmp"
mv "$ENV_FILE.tmp" "$ENV_FILE"

echo "Written: HOST=$HOST_IP into $ENV_FILE"
