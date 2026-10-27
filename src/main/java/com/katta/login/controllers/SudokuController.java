package com.katta.login.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.katta.login.dto.SudokuResponse;
import com.katta.login.service.SudokuSolveService;

@RestController
public class SudokuController {

    private final SudokuSolveService sudokuSolveService;
    public SudokuController(SudokuSolveService sudokuSolveService) {
        this.sudokuSolveService = sudokuSolveService;
    }

    @PostMapping("/api/sudoku/solve")
    public ResponseEntity<SudokuResponse>  solveSudoku(char[][] board) {
        return ResponseEntity.ok(
                this.sudokuSolveService.solveSudoku(board)
        );
    }
}