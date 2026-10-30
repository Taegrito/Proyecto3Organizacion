# Proyecto Integrador 3 - Ensamblador y Desensamblador
## Estudiantes
* Ana Isabella Gómez García
## Descripción 
Este repositorio contiene la solución al proyecto integrador #3.

El proyecto se basa en el proyecto 6 de Nand2Tetris e incluye componenetes adicionales propuestos por el estudiante.

## Compilación y Ejecución
### Lenguaje y versión
* Lenguaje: java
* Versión: JDK 15
* Sistema Operativo: Windows Git Bash
### Dependencias

- JDK 15 o una versión compatible.
- Git Bash para ejecutar los scripts `.sh`.

Verificar la instalación:

```bash
java -version
javac -version
```
## Estructura del Código
El proyecto se encuentra distribuido de la siguiente manera:
```text
.
proyecto3/
 ├── compilar.sh
 ├── ensamblador.sh
 ├── desensamblador.sh
 ├── README.md
 ├── ensamblador
 ├── desensamblador
 ├── pruebas/
 │ ├── ensamblador/
 │ ├── desensamblador/
 │ ├── errores/
 │ └── README.md
 └── DOCUMENTACION/
 └── INFORME.m
```

## Explicación del código

### 1. Ensamblador (`ensamblador/`)

El ensamblador convierte un programa escrito en lenguaje ensamblador Hack (`.asm`) en código máquina binario (`.hack`), utilizando el formato de instrucciones de la plataforma Hack.

Su funcionamiento se divide en las siguientes etapas:

1. **Lectura del archivo:** recibe como argumento la ruta del archivo `.asm` y lee sus instrucciones.
2. **Procesamiento:** ignora los comentarios y las líneas vacías, y analiza las instrucciones válidas.
3. **Resolución de símbolos:** identifica las etiquetas y las direcciones simbólicas para traducirlas a direcciones numéricas.
4. **Traducción:** convierte las instrucciones A y C al formato binario de 16 bits.
5. **Generación del archivo:** escribe las instrucciones binarias en un archivo `.hack`.

El ensamblador también debe validar la entrada e informar los errores detectados, como instrucciones inválidas, direcciones fuera de rango, etiquetas mal formadas y campos de destino, cómputo o salto incorrectos.

### 2. Desensamblador (`desensamblador/`)

El desensamblador realiza el proceso inverso: recibe un archivo de instrucciones binarias Hack (`.hack`) y genera un archivo de texto con instrucciones ensamblador (`.asm`).

Su funcionamiento comprende las siguientes etapas:

1. **Lectura del archivo:** recibe la ruta del archivo `.hack`.
2. **Validación:** verifica que las instrucciones tengan exactamente 16 bits y que contengan únicamente los caracteres `0` y `1`.
3. **Identificación de instrucciones:** determina si cada instrucción corresponde a una instrucción A o C.
4. **Decodificación:** interpreta los campos binarios de las instrucciones y los convierte en sus representaciones simbólicas, utilizando las tablas de códigos del lenguaje Hack.
5. **Generación del archivo:** guarda las instrucciones traducidas en un archivo `.asm`.

El desensamblador debe detectar códigos inválidos, incluidos los campos de cómputo que no existen en la tabla de instrucciones y las instrucciones C que no cumplen con el formato binario requerido.

Como el código máquina no conserva necesariamente los nombres originales de las etiquetas, los comentarios ni los espacios del archivo fuente, el resultado del desensamblado puede ser funcionalmente equivalente sin ser idéntico al programa original.

### 3. Script `compilar.sh`

Este script automatiza la compilación de los archivos fuente Java del ensamblador y del desensamblador.

Utiliza el compilador `javac` y genera los archivos `.class` necesarios para ejecutar los programas. Si ocurre un error durante la compilación, el proceso se detiene para evitar continuar con una compilación incompleta.

### 4. Script `ensamblador.sh`

Este script ejecuta el ensamblador y recibe un único argumento: la ruta del archivo `.asm`.

Antes de ejecutar el programa, verifica que se haya proporcionado un argumento y que el archivo de entrada exista. Después invoca la clase principal del ensamblador para generar el archivo `.hack` correspondiente.

### 5. Script `desensamblador.sh`

Este script ejecuta el desensamblador y recibe un único argumento: la ruta del archivo `.hack`.

Comprueba que el argumento exista y que el archivo de entrada esté disponible. Luego ejecuta la clase principal del desensamblador para producir el archivo `.asm` de salida.

Los scripts están diseñados para ejecutarse desde la carpeta raíz del proyecto mediante Git Bash en Windows. Las rutas se manejan mediante argumentos, evitando depender de una ruta absoluta específica del computador.

### 6. Pruebas

El proyecto organiza las pruebas en tres categorías:

- **tests/ensamblador/:** contiene programas de entrada para comprobar la traducción de `.asm` a `.hack`.
- **tests/desensamblador/:** contiene archivos binarios para comprobar la traducción de `.hack` a `.asm`.
- **tests/errores/:** contiene entradas inválidas para verificar que los programas detecten y reporten los errores establecidos en los requisitos.

Las pruebas se ejecutan mediante los scripts correspondientes y sus resultados se comparan con las salidas esperadas.

### 7. Documentación

La carpeta `DOCUMENTACION/` contiene el informe técnico del proyecto en `INFORME.md`. Este documento describe el diseño de la solución, las decisiones de implementación, las validaciones realizadas y los resultados de las pruebas.
