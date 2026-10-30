package desensamblador;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HackDisassembler {

    private static final Map<String, String> COMP = new HashMap<>();
    private static final Map<String, String> DEST = new HashMap<>();
    private static final Map<String, String> JUMP = new HashMap<>();

    static {
        // Bits de computacion
        addComp("0101010", "0");
        addComp("0111111", "1");
        addComp("0111010", "-1");
        addComp("0001100", "D");
        addComp("0110000", "A");
        addComp("1110000", "M");
        addComp("0001101", "!D");
        addComp("0110001", "!A");
        addComp("1110001", "!M");
        addComp("0001111", "-D");
        addComp("0110011", "-A");
        addComp("1110011", "-M");
        addComp("0011111", "D+1");
        addComp("0110111", "A+1");
        addComp("1110111", "M+1");
        addComp("0001110", "D-1");
        addComp("0110010", "A-1");
        addComp("1110010", "M-1");
        addComp("0000010", "D+A");
        addComp("1000010", "D+M");
        addComp("0010011", "D-A");
        addComp("1010011", "D-M");
        addComp("0000111", "A-D");
        addComp("1000111", "M-D");
        addComp("0000000", "D&A");
        addComp("1000000", "D&M");
        addComp("0010101", "D|A");
        addComp("1010101", "D|M");

        // Bits de destino
        DEST.put("000", "");
        DEST.put("001", "M");
        DEST.put("010", "D");
        DEST.put("011", "MD");
        DEST.put("100", "A");
        DEST.put("101", "AM");
        DEST.put("110", "AD");
        DEST.put("111", "AMD");

        // Bits de salto
        JUMP.put("000", "");
        JUMP.put("001", "JGT");
        JUMP.put("010", "JEQ");
        JUMP.put("011", "JGE");
        JUMP.put("100", "JLT");
        JUMP.put("101", "JNE");
        JUMP.put("110", "JLE");
        JUMP.put("111", "JMP");
    }

    private static void addComp(String bits, String mnemonic) {
        COMP.put(bits, mnemonic);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println(
                "Uso: java HackDisassembler prog.hack"
            );
            System.exit(1);
        }

        Path input = Paths.get(args[0]);

        if (!Files.isRegularFile(input)) {
            System.err.println(
                "Error: no existe el archivo " + input
            );
            System.exit(1);
        }

        String filename = input.getFileName().toString();

        if (!filename.toLowerCase().endsWith(".hack")) {
            System.err.println(
                "Error: el archivo de entrada debe terminar en .hack"
            );
            System.exit(1);
        }

        String base = filename.substring(0, filename.length() - 5);
        Path output = input.resolveSibling(base + "Dis.asm");

        try {
            disassemble(input, output);
            System.out.println(
                "Desensamblado correcto: " + output.toAbsolutePath()
            );
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void disassemble(Path input, Path output)
            throws IOException {

        List<String> lines =
            Files.readAllLines(input, StandardCharsets.UTF_8);

        StringBuilder result = new StringBuilder();
        int lineNumber = 0;

        for (String original : lines) {
            lineNumber++;
            String bits = original.trim();

            // Ignora líneas vacías
            if (bits.isEmpty()) {
                continue;
            }

            if (!bits.matches("[01]{16}")) {
                throw new IllegalArgumentException(
                    "Linea " + lineNumber
                    + ": se esperaban exactamente 16 bits: "
                    + original
                );
            }

            String assembly = decode(bits, lineNumber);

            if (result.length() > 0) {
                result.append(System.lineSeparator());
            }

            result.append(assembly);
        }

        Files.writeString(
            output,
            result.toString() + System.lineSeparator(),
            StandardCharsets.UTF_8
        );
    }

    private static String decode(String bits, int lineNumber) {

        // Instrucción A: 0 seguido de una dirección de 15 bits
        if (bits.charAt(0) == '0') {
            int address = Integer.parseInt(bits.substring(1), 2);
            return "@" + address;
        }

        // Instrucción C: debe comenzar con 111
        if (!bits.startsWith("111")) {
            throw new IllegalArgumentException(
                "Instrucción inválida en la línea " + lineNumber
            );
        }

        String compBits = bits.substring(3, 10);
        String destBits = bits.substring(10, 13);
        String jumpBits = bits.substring(13, 16);

        String comp = COMP.get(compBits);
        String dest = DEST.get(destBits);
        String jump = JUMP.get(jumpBits);

        if (comp == null || dest == null || jump == null) {
            throw new IllegalArgumentException(
                "Codificación no reconocida en la línea "
                + lineNumber + ": " + bits
            );
        }

        StringBuilder instruction = new StringBuilder();

        if (!dest.isEmpty()) {
            instruction.append(dest).append("=");
        }

        instruction.append(comp);

        if (!jump.isEmpty()) {
            instruction.append(";").append(jump);
        }

        return instruction.toString();
    }
}
