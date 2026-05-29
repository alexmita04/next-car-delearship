#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT="$ROOT/out"
LIB="$ROOT/lib"
JAVAFX_DIR="$LIB/javafx-sdk"
POSTGRES_JAR="$LIB/postgresql-42.7.5.jar"

detect_javafx_url() {
  local os arch
  os="$(uname -s | tr '[:upper:]' '[:lower:]')"
  arch="$(uname -m)"
  case "$os" in
    darwin)
      if [[ "$arch" == "arm64" ]]; then
        echo "https://download.oracle.com/java/26/latest/javafx-26_macos-aarch64_bin-sdk.tar.gz"
      else
        echo "https://download.oracle.com/java/26/latest/javafx-26_macos-x64_bin-sdk.tar.gz"
      fi
      ;;
    linux)
      echo "https://download.oracle.com/java/26/latest/javafx-26_linux-x64_bin-sdk.tar.gz"
      ;;
    *)
      echo "Unsupported OS: $os" >&2
      exit 1
      ;;
  esac
}

ensure_javafx() {
  if [[ -d "$JAVAFX_DIR/lib" ]]; then
    return
  fi

  echo "Descărcare JavaFX SDK..."
  local url tmp extracted
  url="$(detect_javafx_url)"
  tmp="$(mktemp -t javafx.XXXXXX.tar.gz)"
  curl -L "$url" -o "$tmp"
  mkdir -p "$LIB"
  tar -xzf "$tmp" -C "$LIB"
  rm "$tmp"
  extracted="$(find "$LIB" -maxdepth 1 -type d -name 'javafx-sdk-*' | head -1)"
  if [[ -z "$extracted" ]]; then
    echo "Nu s-a putut extrage JavaFX SDK." >&2
    exit 1
  fi
  mv "$extracted" "$JAVAFX_DIR"
  echo "JavaFX instalat în $JAVAFX_DIR"
}

compile() {
  ensure_javafx
  mkdir -p "$OUT"

  javac \
    -d "$OUT" \
    -cp "$POSTGRES_JAR" \
    --module-path "$JAVAFX_DIR/lib" \
    --add-modules javafx.controls \
    -sourcepath "$ROOT/src:$ROOT/src/interfaces/model:$ROOT/src/interfaces" \
    $(find "$ROOT/src" -name "*.java")

  cp "$ROOT/src/resources/db.properties" "$OUT/"
  echo "Compilare reușită: $OUT"
}

run_gui() {
  compile
  java \
    -cp "$OUT:$POSTGRES_JAR" \
    --module-path "$JAVAFX_DIR/lib" \
    --add-modules javafx.controls \
    ui.CarDealershipApp
}

run_console() {
  compile
  java -cp "$OUT:$POSTGRES_JAR" Main
}

case "${1:-gui}" in
  compile) compile ;;
  gui) run_gui ;;
  console) run_console ;;
  *)
    echo "Utilizare: $0 [compile|gui|console]"
    exit 1
    ;;
esac
