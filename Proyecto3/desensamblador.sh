#!/usr/bin/env bash
set -e

ROOT="$(cd "$(dirname "$0")" && pwd)"

if [ "$#" -ne 1 ]; then
    echo "Uso: ./desensamblador.sh ruta/archivo.hack" >&2
    exit 1
fi

INPUT="$1"

if [ ! -f "$INPUT" ]; then
    echo "Error: no existe el archivo: $INPUT" >&2
    exit 1
fi

cd "$ROOT"

java -cp "$ROOT" desensamblador.HackDisassembler "$INPUT"