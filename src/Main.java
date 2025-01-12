import controller.GameController;
import model.*;
import view.ConsoleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        GameController controller = new GameController();
        controller.setupGame(); // tutaj przeniesiona logika z dawniej
    }
}
