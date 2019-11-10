package com.javarush.task.task35.task3513;

//Оисывает эффективность хода
public class MoveEfficiency implements Comparable<MoveEfficiency> {
    //количество пустых плиток
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    //сравнивает эффективность двух ходов
    @Override
    public int compareTo(MoveEfficiency o) {
        if (this.numberOfEmptyTiles != o.numberOfEmptyTiles) {
            return Integer.compare(this.numberOfEmptyTiles, o.numberOfEmptyTiles);
            //return new Integer(this.numberOfEmptyTiles).compareTo(new Integer(o.numberOfEmptyTiles));
        } else {
            if (this.score != o.score) {
                return Integer.compare(this.score, o.score);
                //return new Integer(this.score).compareTo(new Integer(o.score));
            }
        }
        return 0;
    }
    
}
