package com.katta.login.service;

import org.springframework.stereotype.Service;

import com.katta.login.dto.SudokuResponse;


@Service
public class SudokuSolveService {

    public static SudokuResponse solveSudoku(char[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {

                if (board[row][col] == 'X') {

                    for (char num = '1'; num <= '9'; num++) {

                        if (isValid(board, row, col, num)) {
                            board[row][col] = num;

                            if (solveSudoku(board).getIsvalid()) {
                                return new SudokuResponse(true, board);
                            }

                            // Backtrack
                            board[row][col] = 'X';
                        }
                    }

                    return new SudokuResponse(false, board);  // No valid number found
                }
            }
        }

        return new SudokuResponse(true, board);  // Solved
    }

    private static boolean isValid(
            char[][] board, int row, int col, char num) {

        for (int i = 0; i < 9; i++) {

            // Check row
            if (board[row][i] == num)
                return false;

            // Check column
            if (board[i][col] == num)
                return false;

            // Check 3x3 box
            int boxRow = 3 * (row / 3) + i / 3;
            int boxCol = 3 * (col / 3) + i % 3;

            if (board[boxRow][boxCol] == num)
                return false;
        }

        return true;
    }
}