package com.example.demoonschool.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class MineSweeper {
    private int rows;
    private int cols;
    private int mines;
    private int cellLeftForWin;
    private int flags;
    private Cell[][] board;

    public MineSweeper(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.mines = (this.rows * this.cols / 8) + 1;
        this.cellLeftForWin = 0;

        this.board = new Cell[this.rows][this.cols];

        generateRandomMinePosition();
        calculateSurroundingMines();
    }

    private void generateRandomMinePosition() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                board[row][col] = new Cell();
                board[row][col].isMine = false;
                board[row][col].isRevealed = false;
                board[row][col].isFlagged = false;
            }
        }

        Random random = new Random();
        Set<Integer> uniqueNumbers = new HashSet<>();

        while (uniqueNumbers.size() < mines) {
            int randomNumber = random.nextInt(rows * cols);
            board[randomNumber/cols][randomNumber%cols].isMine = true;
            uniqueNumbers.add(randomNumber);
        }
    }

    private void calculateSurroundingMines() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                board[row][col].nearbyMines = 0;
            }
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!board[row][col].isMine) {
                    int count = 0;
                    // Check the 8 neighboring cells
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            int newRow = row + dr;
                            int newCol = col + dc;
                            // Check if the neighboring cell is within bounds
                            if (isValidCell(newRow, newCol) && board[newRow][newCol].isMine) {
                                count++;
                            }
                        }
                    }
                    board[row][col].nearbyMines = count;
                }
            }
        }
    }

    public void revealCell(int row, int col) {
        board[row][col].isRevealed = true;
        this.cellLeftForWin++;
    }

    public List<int[]> floodFill(int row, int col) {
        List<int[]> revealedPositions = new ArrayList<>();;

        if (board[row][col].isRevealed) return revealedPositions;

        revealCell(row,col);
        revealedPositions.add(new int[]{row, col});

        if (board[row][col].nearbyMines != 0) return revealedPositions;

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{row, col});

        int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1};
        int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int currRow = curr[0];
            int currCol = curr[1];

            for (int i = 0; i < 8; i++) {
                int newRow = currRow + dx[i];
                int newCol = currCol + dy[i];

                if (isValidCell(newRow, newCol) && !board[newRow][newCol].isRevealed) {
                    revealCell(newRow, newCol);
                    revealedPositions.add(new int[]{newRow, newCol});
                    if (board[newRow][newCol].nearbyMines == 0) {
                        queue.offer(new int[]{newRow, newCol});
                    }
                }
            }
        }

        return revealedPositions;
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public int getNumberOfRows() {
        return rows;
    }

    public int getNumberOfColumns() {
        return cols;
    }

    public boolean isMineAt(int row, int col) {
        return board[row][col].isMine;
    }

    public int getSurroundingMinesCount(int row, int col) {
        return board[row][col].nearbyMines;
    }

    public boolean isWinner() {
        return (cellLeftForWin == rows * cols - mines);
    }

    public void setFlagOn(int row, int col) {
        if (isValidCell(row,col)) {
            board[row][col].isFlagged = true;
            flags++;
            board[row][col].isRevealed = true;
        }
    }

    public void setFlagOff(int row, int col) {
        if (isValidCell(row,col)) {
            board[row][col].isFlagged = false;
            flags--;
            board[row][col].isRevealed = false;
        }
    }

    public boolean isFlagOn(int row, int col) {
        if (isValidCell(row, col)) {
            return board[row][col].isFlagged;
        }
        return false;
    }

    public boolean canFlag() {
        return flags <= mines;
    }

    public boolean isRevealed(int row, int col) {
        if (isValidCell(row,col)) {
            return board[row][col].isRevealed;
        }
        return false;
    }
}
