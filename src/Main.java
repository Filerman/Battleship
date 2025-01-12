import controller.GameController;
import model.GameStatistics;

import java.util.Scanner;

/**
 * Klasa startowa aplikacji – zawiera menu główne w pętli.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameController controller = new GameController();

        boolean exit = false;
        while (!exit) {
            System.out.println("\n===== MENU GŁÓWNE =====");
            System.out.println("1. Rozpocznij nową grę");
            System.out.println("2. Wyświetl statystyki i ranking");
            System.out.println("3. Wyświetl historię rozgrywek");
            System.out.println("4. Zakończ");

            System.out.print("Wybierz opcję: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    // Uruchamiamy rozgrywkę
                    controller.setupGame();
                    // Po zakończeniu (lub przerwaniu) rozgrywki wracamy do menu
                }
                case "2" -> {
                    // Wyświetlamy statystyki i ranking
                    GameStatistics stats = controller.getStatistics();
                    System.out.println(stats.getRanking());
                }
                case "3" -> {
                    // Historia rozgrywek
                    GameStatistics stats = controller.getStatistics();
                    System.out.println(stats.getGameHistory());
                }
                case "4" -> {
                    exit = true;
                    System.out.println("Do zobaczenia!");
                }
                default -> System.out.println("Nieprawidłowy wybór. Spróbuj ponownie.");
            }
        }

        scanner.close();
    }
}
