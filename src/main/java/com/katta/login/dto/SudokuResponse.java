package com.katta.login.dto;

public class SudokuResponse{

    private boolean isvalid;
    private char[][] sudokuArray;

    public SudokuResponse(
            boolean isvalid,
            char[][] sudokuArray) {

        this.isvalid = isvalid;
        this.sudokuArray = sudokuArray;
    }

    public boolean getIsvalid() {
        return isvalid;
    }

    public char[][] getSudokuArray() {
        return sudokuArray;
    }
}