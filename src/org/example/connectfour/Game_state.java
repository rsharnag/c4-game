package org.example.connectfour;
public class Game_state {
	byte[][] board;
	int[][] score_array;
	int[] score;
	int winner;
	int num_of_pieces;
	public Game_state(int size_x,int size_y,int win_places){
		
		this.score=new int[2];
		this.board=new byte[size_x][size_y];
		this.score_array=new int[2][win_places];
	}
	
}
