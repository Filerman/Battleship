package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BoardDirector {
    private BoardBuilderInterface builder;
    private Scanner scanner;

    public BoardDirector(BoardBuilderInterface builder, Scanner scanner) {
        this.builder = builder;
        this.scanner = scanner;
    }

    /**
     * Interaktywna budowa planszy – użytkownik podaje rozmiar oraz dodaje kamienie.
     */
    public Board constructInteractively() {
        builder.reset()
                .setWaterChar('.')
                .setShipChar('S')
                .setHitChar('X')
                .setMissChar('O');
        System.out.print("Podaj rozmiar planszy: ");
        int size = Integer.parseInt(scanner.nextLine().trim());
        builder.setSize(size);

        while (true) {
            System.out.print("Czy chcesz dodać kamień? (tak/nie): ");
            String answer = scanner.nextLine().trim();
            if (!answer.equalsIgnoreCase("tak")) {
                break;
            }
            System.out.print("Podaj wiersz (0-" + (size - 1) + "): ");
            int row = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Podaj kolumnę (0-" + (size - 1) + "): ");
            int col = Integer.parseInt(scanner.nextLine().trim());
            List<Position> positions = new ArrayList<>();
            positions.add(new Position(row, col));
            builder.addStone(positions);
            System.out.println("Kamień dodany na pozycji (" + row + ", " + col + ").");
        }
        return builder.build();
    }

    /**
     * Buduje standardową planszę bez kamieni.
     */
    public Board constructStandardBoardWithoutStones() {
        builder.reset()
                .setSize(10)
                .setWaterChar('.')
                .setShipChar('S')
                .setHitChar('X')
                .setMissChar('O');
        return builder.build();
    }
}
