package com.javarush.task.task35.task3513;

import java.util.*;

//содержит игровую логику и хранит игровое поле
public class Model {
    //определяет ширину игрового поля
    private final static int FIELD_WIDTH = 4;
    //текущий счет
    protected int score;
    //максимальный вес плитки на игровом поле
    protected int maxTile;
    //Стек предыдущих состояний игрового поля
    private Stack<Tile[][]> previousStates = new Stack<>();
    //Стек предыдущих очков игры
    private Stack<Integer> previousScores = new Stack<>();
    //Маркер того, что состояние игры изменилось
    private boolean isSaveNeeded = true;

    private Tile[][] gameTiles;

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public Model() {
        resetGameTiles();
        score = 0;
        maxTile = 2;
    }

    //Перебирает все возможные варианты движения через поворот массива игры и проверяет
    //Возможно ли в текущей позиции сделать ход так, чтобы состояние игрового поля изменилось
    public boolean canMove(){
        boolean isChange = false;
        //Для четырех поворотов
        for (int i = 0; i < 4; i++) {
            isChange = canMoveToOneOfSide();
            if (!isChange) {            //проверка на возможность хода с изменением состояния поля
                rotateRight();          //если нет, то повернуть поле
            } else {                    //если ход возможен
                while ((4 - i) > 0) {   //повернуть поле в исходное положение
                    rotateRight();
                    i++;
                }
                break;
            }
        }
        return isChange;
    }

    //Возможно ли в текущей позиции сделать ход так, чтобы состояние игрового поля изменилось
    private boolean canMoveToOneOfSide(){
        boolean isChange = false;
        for(int i = 0; i < gameTiles.length; i++){
            if(compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])){
                isChange = true;
            }
        }
        return isChange;
    }

    //Получает список пустых плиток игрового поля
    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTilesList = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].isEmpty()) {
                    emptyTilesList.add(gameTiles[i][j]);
                }
            }
        }
        return emptyTilesList;
    }

    //Добавляет случайным образом случайное значение одной из плиток игрового поля
    private void addTile(){
        List<Tile> emptyTilesList = getEmptyTiles();
        if(emptyTilesList.size() > 0) {
            emptyTilesList.get((int) (Math.random() * emptyTilesList.size())).value = (Math.random() < 0.9) ? 2 : 4;
        }
    }

    //Сбрасывает игру до начального состояния
    public void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
        //score = 0;
        //maxTile = 0;
    }

    //Сжатие плиток, таким образом, чтобы все пустые плитки были справа, т.е. ряд {4, 2, 0, 4} становится рядом {4, 2, 4, 0}
    //Возвращает true в случае, если он вносил изменения во входящий массив, иначе - false
    private boolean compressTiles(Tile[] tiles){
        boolean changes = false;
        for (int i = 0; i<tiles.length; i++){
            if(tiles[i].isEmpty()){
                int j = i;
                while (tiles[j].isEmpty() && j < tiles.length-1){
                    j++;
                }
                if(!tiles[j].isEmpty()) changes = true;
                tiles[i] = tiles[j];
                tiles[j] = new Tile();
            }
        }
        return changes;
    }

    //Слияние плиток одного номинала, т.е. ряд {4, 4, 2, 0} становится рядом {8, 2, 0, 0}
    //Параметр - в метод mergeTiles всегда передается массив плиток без пустых в середине
    //Возвращает true в случае, если он вносил изменения во входящий массив, иначе - false
    private boolean mergeTiles(Tile[] tiles){
        boolean changes = false;
        for (int i = 0; i < tiles.length-1; i++){
            if((!tiles[i].isEmpty())&&(tiles[i].value == tiles[i+1].value)){
                tiles[i].value += tiles[i].value;
                tiles[i+1].value = 0;
                score += tiles[i].value;
                changes = true;
                if(tiles[i].value > maxTile) maxTile = tiles[i].value;
                compressTiles(tiles);

            }
        }
        return changes;
    }

    //Транспонирование матрицы(двумерного массива) - строки становятся столбцами и наоборот
    //Делает возможным только ход движением вверх
    /*private void transpose() {
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = i+1; j < gameTiles.length; j++) {
                Tile temp = gameTiles[i][j];
                gameTiles[i][j] = gameTiles[j][i];
                gameTiles[j][i] = temp;
            }
        }
    }*/

    //Движение влево + добавляет новые плитки на поле при изменении его состояния методами compressTiles и mergeTiles
    public void left(){
        //Сохранение текущего состояния игры при условии, если его еще не было
        if (isSaveNeeded) saveState(gameTiles);
        boolean isChange = false;
        for(int i = 0; i < gameTiles.length; i++){
            if(compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])){
                isChange = true;
            }
        }
        if(isChange && getEmptyTiles().size() != 0) addTile();
        //Отметка о том, что состояние игры изменилось
        isSaveNeeded = true;
    }

    //Поворачивает игровое поле вправо
    private void rotateRight() {
        Tile[][] result = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                result[j][gameTiles.length - i - 1] = gameTiles[i][j];
            }
        }
        gameTiles = result;
    }

    //Движение вправо - поворот поля два раза вправо (аналог двух поворотов влево),
    // выполнение сути метода и возвращение поля в исходное положение
    public void right() {
        //Сохранение текущего состояния игры
        if (isSaveNeeded) saveState(gameTiles);
        boolean isChange = false;
        rotateRight();
        rotateRight();

        for(int i = 0; i < gameTiles.length; i++){
            if(compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])){
                isChange = true;
            }
        }
        //left();
        rotateRight();
        rotateRight();
        if(isChange && getEmptyTiles().size() != 0) {
            addTile();
        }
        isSaveNeeded = true;
    }

    //Движение вверх - поворот поля три раза вправо (аналог одного поворота влево),
    // выполнение сути метода и возвращение поля в исходное положение
    public void up() {
        //Сохранение текущего состояния игры
        if (isSaveNeeded) saveState(gameTiles);
        boolean isChange = false;
        //transpose();
        rotateRight();
        rotateRight();
        rotateRight();
        for(int i = 0; i < gameTiles.length; i++){
            if(compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])){
                isChange = true;
            }
        }
        //left();
        rotateRight();
        //transpose();
        if(isChange && getEmptyTiles().size() != 0) {
            addTile();
        }
        isSaveNeeded = true;
    }

    //Движение вниз - поворот поля один раз вправо, выполнение сути метода и возвращение поля в исходное положение
    public void down() {
        //Сохранение текущего состояния игры
        if (isSaveNeeded) saveState(gameTiles);
        boolean isChange = false;
        rotateRight();
        for(int i = 0; i < gameTiles.length; i++){
            if(compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])){
                isChange = true;
            }
        }
        //left();
        rotateRight();
        rotateRight();
        rotateRight();

        if(isChange && getEmptyTiles().size() != 0) {
            addTile();
        }
        isSaveNeeded = true;
    }
    //Сохраняет текущее игровое состояние и счет в стеки-хранилища
    private void saveState(Tile[][] currentStateForSave) {
        Tile[][] gameTilesForSave = new Tile[currentStateForSave.length][currentStateForSave.length];
        for (int i = 0; i < currentStateForSave.length; i++) {
            for (int j = 0; j < currentStateForSave.length; j++) {
                Tile element = new Tile(currentStateForSave[i][j].value);
                gameTilesForSave[i][j] = element;
            }
        }
        previousStates.push(gameTilesForSave);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    //Устанавливает текущее игровое состояние равным последнему находящемуся в стеках
    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }

    }

    //Двигает клетки игрового поля в случайном направлении
    public void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0: left();
                break;
            case 1: right();
                break;
            case 2: up();
                break;
            case 3: down();
                break;
        }
    }

    //Определяет, отличается ли вес плиток в массиве gameTiles от веса плиток в верхнем массиве стека previousStates
    public boolean hasBoardChanged(){
        int sumGameTiles = 0;
        int sumSavedState = 0;
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
                sumGameTiles += gameTiles[i][j].value;
            }
        }
        for (int i = 0; i < previousStates.peek().length; i++) {
            for (int j = 0; j < previousStates.peek().length; j++) {
                sumSavedState += previousStates.peek()[i][j].value;
            }
        }
        if (sumGameTiles != sumSavedState) return true;
        return false;
    }

    //Определяет эффективность переданного хода
    public MoveEfficiency getMoveEfficiency(Move move){
        MoveEfficiency moveEfficiency;
        move.move();
        boolean isBoardChanged = hasBoardChanged();

        if (!isBoardChanged) {
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        } else {
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
            rollback();
        }
        return moveEfficiency;
    }

    //Выбирает лучший из возможных ходов и выполнять его
    public void autoMove() {
        //Создание приоритетной очереди и ее заполнение вариантами следующего хода с учетом правила сравнения MoveEfficiency
        PriorityQueue<MoveEfficiency> efficiencyOfMove = new PriorityQueue<>(4, Collections.reverseOrder());
        efficiencyOfMove.offer(getMoveEfficiency(this::left));
        efficiencyOfMove.offer(getMoveEfficiency(this::up));
        efficiencyOfMove.offer(getMoveEfficiency(this::right));
        efficiencyOfMove.offer(getMoveEfficiency(this::down));

        efficiencyOfMove.peek().getMove().move();
    }
}
