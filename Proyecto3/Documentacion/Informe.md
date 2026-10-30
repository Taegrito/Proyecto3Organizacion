# Informe técnico
## 1. Introducción
Este proyecto consiste en el desarrollo de un ensamblador y desensamblador para el lenguaje Hack, utilizando Java. 

El ensamblador tiene como finalidad traducir un programa en lenguaje ensamblador (.asm) a lenguaje binario para que la CPU pueda correr las instrucciones.

El desensamblador hace este proceso inverso, pasa de instrucciones binarias de 16bits a lenguaje ensamblador.

El proyecto también contempla la validación de entradas, la detección de errores y la ejecución de los scripts.

## 2. Descripción general del sistema
El sistema está compuesto por dos herraminetas principales: el ensamblador y el desensamblador. Ambos están implementados en java y se utilizan para transformar programas entre dos representaciones de lenguaje Hack.

El ensamblador recibe un archivo fuente con instrucciones simbólicas y genera una representación binaria. Para ello, debe interpretar las instrucciones, identificar sus componentes y traducirlas de acuerdo con el formato definido por la arquitectura Hack.

El desensamblador recibe un archivo con instrucciones binarias y analiza su contenido para reconstruir las instrucciones simbólicas correspondientes. Durante este proceso, debe verificar que las instrucciones cumplan con las condiciones de formato y codificación admitidas por la implementación.

Adicionalmente, el proyecto cuenta con scirpts para compilar y ejecutar las herramientas, así como directorios destinados a almacenar los archivos fuentes y las pruebas

## 3. Estructura de archivos 
El proyecto se organiza de la siguiente manera
```bash
Proyecto6/
├── Documentacion/
├── ensamblador/
├── desensamblador/
├── tests/
├── compilar.sh
├── ensamblador.sh
└── desensamblador.sh
```
La función de cada elemento es la siguiente:
* _ensamblador/_: contiene el código fuente de java encargado de construir instrucciones binarias de acuerdo a un progrma en lenguaje ensamblador.
* _desensamblador/_: contiene el código fuente de java encargado de construir instrucciones simbólicas en base a instrucciones binarias.
* _tests/_: contiene los diversos archivos para poder realizar pruebas del funcionamiento correcto del ensablador y desensamblador, junto con errores para demostrar el funcionamiento de la detección de errores.
* _compilar sh:_ automatiza el proceso de compilación de las clases Java.
* _ensamblador.sh_: permite ejecutar el ensamblador desde la terminal.
* _desensamblador.sh_: permite ejecutar el desensamblador desde la terminal.
## 4. Diseño e implementación del ensamblador 
### 4.1 Lectura del archivo fuente
El programa examina un archivo .asm y prepara su contenido para el análisis y la traducción.

Aquí se consideran las líneas vacías y los espacios que puedan aparecer, de acuerdo a las reglas impleentadas.
### 4.2 Traducción de instrucciones tipo A
El formato general de este tipo de instrucciones @valor. 

El ensamblador identifica el valor indicado, comprueba que cumpla con las restricciones de instrucción y lo convierte a su representación binaria.
### 4.3 Traducción de instrucciones tipo C
Las instrucciones C permiten realizar operaciones y, opcionalmente, especificar un destino y un salto.

No todos los componentes tienen que aparecer simultáneamente. El ensamblador debe interpretar los campos presentes y producir la codificación binaria correspondiente.
### 4.4 Manejo de etiquuetas y símbolos
Las etiquetas permiten identificar posiciones del programa mediante nombres simbólicos. Durante la traducción, el ensamblador debe reconocer las declaraciones de etiquetas y resolver las referencias correspondientes, si esta funcionalidad está implementada.

También debe verificar las restricciones aplicables a los nombres de etiquetas y detectar declaraciones duplicadas cuando corresponda.

## 5. Diseño e implementación del desensamblador
### 5.1 Lectura del archivo binario
El programa lee el archivo de entrada y examina las instrucciones una por una. Cada instrucción debe cumplir con el formato esperado por el lenguaje Hack.
### 5.2 Validación de instrucciones
Antes de convertir una instrucción, el desensamblador debe verificar que tenga la longitud correcta, que contenga únicamente los caracteres binarios permitidos y que respete los patrones de codificación reconocidos.

Entre las situaciones que deben verificarse se encuentran:
* Líneas con una longitud distinta de 16 bits.
* Caracteres diferentes de 0 y 1.
* Instrucciones C con bits iniciales inválidos.
* Combinaciones de bits que no correspondan a una operación admitida.
### 5.3 Conversión a instrucciones simbólicas (.asm)
Una vez validada la instrucción, el programa interpreta su codificación y determina la instrucción simbólica equivalente.
## Conclusiones
El desarrollo del ensamblador y el desensamblador permite trabajar con dos representaciones del lenguaje Hack: las instrucciones simbólicas y su codificación binaria.

La implementación busca automatizar el proceso de traducción, incorporar validaciones y facilitar la ejecución de las herramientas mediante scripts.
