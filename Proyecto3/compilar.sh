
set -e

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

echo "Compilando ensamblador y desensamblador..."

javac -d . ensamblador/*.java desensamblador/*.java

echo "Compilacion completada correctamente."