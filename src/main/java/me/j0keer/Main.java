package me.j0keer;

import java.util.Scanner;

public class Main {
    public static String VERSION = "1.0.0";

    public static void main(String[] args) {
        LDProgramation program = new LDProgramation();
        program.onEnable();

        Scanner scanner = new Scanner(System.in);
        boolean confirmation = false;
        while (true) {
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("exit")) {
                if (!confirmation) {
                    program.console("{warning}Are you sure you want to stop the program? Type 'exit' again to confirm.");
                    confirmation = true;
                    continue;
                }
                program.console("{info}LDProgramation is stopping...");
                break;
            }
            String command = line.split(" ")[0];
            String[] commandArgs = line.substring(command.length()).trim().split(" ");
            program.onCommand(command, commandArgs);
        }
    }
}