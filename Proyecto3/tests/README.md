# Documentación de pruebas
## 1. Pruebas de error
El ensamblador detecta el siguiente tipo de errores:
| Categoría | Descripción | Ejemplo |
|---|---|---|
| Instrucción A | Valor ausente, negativo, fuera de rango o símbolo inválido. | `@32768` |
| Etiquetas | Etiqueta mal formada, vacía, duplicada o con nombre inválido. | `(LOOP` |
| Destino | Campo de destino inválido. | `DM=D+1` |
| Cómputo | Operación inexistente o vacía. | `D=XYZ` |
| Salto | Salto inválido o vacío. | `D;JXX` |
| Estructura | Más de un separador o texto que no es una instrucción válida. | `D=M=A` |
### Ejecución
Desde la raíz del proyecto, ejecutar el ensamblador con cada archivo de prueba:

```bash
./ensamblador.sh tests/errores/Test1.asm
./ensamblador.sh tests/errores/Test2.asm
./ensamblador.sh tests/errores/Test3.asm
```
### Resultado esperado
El ensamblador debe detectar el error, informar el problema y evitar crear el archivo.
El desensamblador detecta los siguientes errores:
| Categoría | Descripción | Ejemplo |
|---|---|---|
| Longitud | La línea no contiene exactamente 16 caracteres. | `000000000000101` |
| Caracteres | La instrucción contiene caracteres distintos de `0` y `1`. | `0000000000000201` |
| Bits 14–13 | Una instrucción C no comienza con los bits requeridos `111`. | `1010110000010000` |
| Cómputo | El campo de cómputo no corresponde a un código válido. | `1110000001010000` |
### Ejecución
Ejecutar cada archivo inválido con el desensamblador:
```bash
./desensamblador.sh tests/errores/Error1.hack
./desensamblador.sh tests/errores/Error2.hack
./desensamblador.sh tests/errores/Error3.hack
```
### Resultado esperado
El desensamblador indica cual fue el error, muestra el mensaje de error, rechaza las entrada inválidas y no acpeta la instrucción
## 2. Criterios de aceptacioón
Una prueba se considera aprobada cuando:

1. El programa identifica correctamente la entrada inválida.
2. El programa informa el error de forma comprensible.
3. El programa no genera una traducción válida para la instrucción incorrecta.
4. El comportamiento observado coincide con el resultado esperado de la prueba.

## Registro de resultados
Los resultados deben registrarse después de ejecutar cada prueba.

| Archivo | Categoría | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| `Test1.asm` | Cómputo del ensamblador | Rechazar la operación inválida. | Error: Campo comp no valido en: D=XYZ. Operación rechazada |
| `Test2.asm` | Operación inválida | Rechazar la operación inválida. | Error: Campo comp no valido en: D=XYZ. Operación rechazada|
| `Test3.asm` | Valor negativo | Rechazar la operación inválida. | Error: Simbolo no valido '-5' en: @-5|
| `Error1.hack` | Longitud del desensamblador | Rechazar la longitud incorrecta. | Error: L??nea 1: se esperaban exactamente 16 bits: 000000000000101. Opearción rechazada |
| `Error2.hack` | Caracteres del desensamblador | Rechazar caracteres inválidos. | Operación rechazada |
| `Error3.hack` | Cómputo del desensamblador | Rechazar el código inexistente. | $ ./desensamblador.sh tests/errores/Error3.hack Error: Codificación no reconocida en la l??nea 1: 1110000001010000. Operación rechazada |
