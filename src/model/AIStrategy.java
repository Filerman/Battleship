package model;

public interface AIStrategy {
    MoveCommand executeStrategy(Board board); // Generuje ruch na podstawie strategii
}