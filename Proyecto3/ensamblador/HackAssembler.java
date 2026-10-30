package ensamblador;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class HackAssembler {

    private static final Pattern SYMBOL_PATTERN =
            Pattern.compile("[A-Za-z_.$:][A-Za-z0-9_.$:]*");

    private static final Map<String, String> DEST = new HashMap<>();
    private static final Map<String, String> COMP = new HashMap<>();
    private static final Map<String, String> JUMP = new HashMap<>();

    static {
        // Bits de destino
        DEST.put("", "000");
        DEST.put("M", "001");
        DEST.put("D", "010");
        DEST.put("MD", "011");
        DEST.put("A", "100");
        DEST.put("AM", "101");
        DEST.put("AD", "110");
        DEST.put("AMD", "111");

        // Bits de computacion
        COMP.put("0",   "0101010");
        COMP.put("1",   "0111111");
        COMP.put("-1",  "0111010");
        COMP.put("D",   "0001100");
        COMP.put("A",   "0110000");
        COMP.put("M",   "1110000");
        COMP.put("!D",  "0001101");
        COMP.put("!A",  "0110001");
        COMP.put("!M",  "1110001");
        COMP.put("-D",  "0001111");
        COMP.put("-A",  "0110011");
        COMP.put("-M",  "1110011");
        COMP.put("D+1", "0011111");
        COMP.put("A+1", "0110111");
        COMP.put("M+1", "1110111");
        COMP.put("D-1", "0001110");
        COMP.put("A-1", "0110010");
        COMP.put("M-1", "1110010");
        COMP.put("D+A", "0000010");
        COMP.put("D+M", "1000010");
        COMP.put("D-A", "0010011");
        COMP.put("D-M", "1010011");
        COMP.put("A-D", "0000111");
        COMP.put("M-D", "1000111");
        COMP.put("D&A", "0000000");
        COMP.put("D&M", "1000000");
        COMP.put("D|A", "0010101");
        COMP.put("D|M", "1010101");

        // Bits de salto
        JUMP.put("", "000");
        JUMP.put("JGT", "001");
        JUMP.put("JEQ", "010");
        JUMP.put("JGE", "011");
        JUMP.put("JLT", "100");
        JUMP.put("JNE", "101");
        JUMP.put("JLE", "110");
        JUMP.put("JMP", "111");
    }

    private final Map<String, Integer> symbols = new HashMap<>();
    private int nextVariableAddress = 16;

    public HackAssembler() {
        initializePredefinedSymbols();
    }

    // Registra los simbolos predefinidos de Hack.
    private void initializePredefinedSymbols() {
        for (int i = 0; i <= 15; i++) {
            symbols.put("R" + i, i);
        }

        symbols.put("SP", 0);
        symbols.put("LCL", 1);
        symbols.put("ARG", 2);
        symbols.put("THIS", 3);
        symbols.put("THAT", 4);
        symbols.put("SCREEN", 16384);
        symbols.put("KBD", 24576);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: java HackAssembler Prog.asm");
            System.exit(1);
        }

        Path input = Paths.get(args[0]);

        if (!Files.isRegularFile(input)) {
            System.err.println(
                    "Error: no existe el archivo: " + input);
            System.exit(1);
        }

        String filename = input.getFileName().toString();
        int dot = filename.lastIndexOf('.');

        String outputName =
                (dot > 0 ? filename.substring(0, dot) : filename)
                + ".hack";

        Path output = input.resolveSibling(outputName);

        try {
            new HackAssembler().assemble(input, output);

            System.out.println(
                    "Ensamblado correcto: " + output.toAbsolutePath());

        } catch (IOException | IllegalArgumentException ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(1);
        }
    }

    // Lee, procesa y traduce el archivo.
    public void assemble(Path input, Path output) throws IOException {
        List<String> lines =
                Files.readAllLines(input, StandardCharsets.UTF_8);

        List<String> instructions = cleanLines(lines);

        firstPass(instructions);

        List<String> machineCode = secondPass(instructions);

        Files.write(output, machineCode, StandardCharsets.UTF_8);
    }

    // Elimina comentarios, espacios y lineas vacias.
    private List<String> cleanLines(List<String> lines) {
        List<String> cleaned = new ArrayList<>();

        for (String line : lines) {
            int commentIndex = line.indexOf("//");

            if (commentIndex >= 0) {
                line = line.substring(0, commentIndex);
            }

            line = line.replaceAll("\\s+", "");

            if (!line.isEmpty()) {
                cleaned.add(line);
            }
        }

        return cleaned;
    }

    // Primera pasada: registra las etiquetas.
    private void firstPass(List<String> instructions) {
        int romAddress = 0;

        for (String instruction : instructions) {
            if (instruction.startsWith("(")
                    && instruction.endsWith(")")) {

                String label = instruction.substring(
                        1, instruction.length() - 1);

                validateSymbol(label, instruction);

                if (symbols.containsKey(label)) {
                    throw new IllegalArgumentException(
                            "Etiqueta duplicada o reservada: " + label);
                }

                symbols.put(label, romAddress);

            } else {
                romAddress++;
            }
        }
    }

    // Segunda pasada: genera las instrucciones binarias.
    private List<String> secondPass(List<String> instructions) {
        List<String> output = new ArrayList<>();

        for (String instruction : instructions) {

            // Las etiquetas no generan instrucciones.
            if (instruction.startsWith("(")
                    && instruction.endsWith(")")) {
                continue;
            }

            if (instruction.startsWith("@")) {
                output.add(
                        translateAInstruction(
                                instruction.substring(1)));
            } else {
                output.add(translateCInstruction(instruction));
            }
        }

        return output;
    }

    // Traduce instrucciones A: @numero o @simbolo.
    private String translateAInstruction(String value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Instruccion A sin valor: @");
        }

        int address;

        if (value.matches("[0-9]+")) {
            try {
                address = Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                        "Direccion numerica fuera de rango: " + value);
            }

            if (address < 0 || address > 32767) {
                throw new IllegalArgumentException(
                        "La direccion debe estar entre 0 y 32767: "
                                + value);
            }

        } else {
            validateSymbol(value, "@" + value);

            Integer knownAddress = symbols.get(value);

            // Las variables nuevas comienzan en la direccion 16.
            if (knownAddress == null) {
                knownAddress = nextVariableAddress++;

                if (knownAddress > 32767) {
                    throw new IllegalArgumentException(
                            "No quedan direcciones para variables.");
                }

                symbols.put(value, knownAddress);
            }

            address = knownAddress;
        }

        return to16BitBinary(address);
    }

    // Traduce instrucciones C: dest=comp;jump.
    private String translateCInstruction(String instruction) {
        String dest = "";
        String compAndJump = instruction;
        String jump = "";

        int equalsIndex = instruction.indexOf('=');

        if (equalsIndex >= 0) {
            dest = instruction.substring(0, equalsIndex);
            compAndJump = instruction.substring(equalsIndex + 1);
        }

        int semicolonIndex = compAndJump.indexOf(';');
        String comp = compAndJump;

        if (semicolonIndex >= 0) {
            comp = compAndJump.substring(0, semicolonIndex);
            jump = compAndJump.substring(semicolonIndex + 1);
        }

        String destBits = DEST.get(dest);
        String compBits = COMP.get(comp);
        String jumpBits = JUMP.get(jump);

        if (destBits == null) {
            throw new IllegalArgumentException(
                    "Campo dest no valido en: " + instruction);
        }

        if (compBits == null) {
            throw new IllegalArgumentException(
                    "Campo comp no valido en: " + instruction);
        }

        if (jumpBits == null) {
            throw new IllegalArgumentException(
                    "Campo jump no valido en: " + instruction);
        }

        return "111" + compBits + destBits + jumpBits;
    }

    // Convierte un numero a una cadena binaria de 16 bits.
    private static String to16BitBinary(int value) {
        String binary = Integer.toBinaryString(value);

        return "0".repeat(16 - binary.length()) + binary;
    }

    // Comprueba que un simbolo tenga un nombre valido.
    private static void validateSymbol(
            String symbol, String source) {

        if (!SYMBOL_PATTERN.matcher(symbol).matches()) {
            throw new IllegalArgumentException(
                    "Simbolo no valido '" + symbol
                            + "' en: " + source);
        }
    }
}
