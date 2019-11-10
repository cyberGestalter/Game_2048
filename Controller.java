package com.javarush.task.task35.task3513;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//следит за нажатием клавиш во время игры (обработка пользовательского ввода с клавиатуры)
public class Controller extends KeyAdapter {
    private Model model;
    private View view;
    //Bес плитки, при достижении которого игра будет считаться выигранной
    private static final int WINNING_TILE = 2048;

    public Controller(Model model) {
        this.model = model;
        this.view = new View(this);
    }

    public View getView() {
        return view;
    }

    public Tile[][] getGameTiles() {
        return model.getGameTiles();
    }
    public int getScore() {
        return model.score;
    }

    //Возврат игрового поля в начальное состояние
    public void resetGame() {
        model.score = 0;
        view.isGameWon = false;
        view.isGameLost = false;
        model.resetGameTiles();
    }

    @Override
    //Обработка пользовательского ввода
    public void keyPressed(KeyEvent e) {
        //super.keyPressed(e);
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) resetGame();
        if (!model.canMove()) view.isGameLost = true;
        if ((!view.isGameLost) && (!view.isGameWon)) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT : model.left();
                    break;
                case KeyEvent.VK_RIGHT : model.right();
                    break;
                case KeyEvent.VK_UP : model.up();
                    break;
                case KeyEvent.VK_DOWN : model.down();
                    break;
                case KeyEvent.VK_Z : model.rollback();
                    break;
                case KeyEvent.VK_R : model.randomMove();
                    break;
                case KeyEvent.VK_A : model.autoMove();
                    break;
            }
        }
        if (model.maxTile == Controller.WINNING_TILE) view.isGameWon = true;
        view.repaint();
    }
}
