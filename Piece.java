package main;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Represents a piece on the chessboard, which maybe a normal chess piece, or it may be a piece that has absorbed the powers of another 
 * piece, such as a rook-knight.
 * @author Adam Hodapp
 *
 */
public class Piece {
	public static final int NUM_TYPES = 6; //Number of possible kinds of pieces, king, queen, rook, bishop, knight, pawn.
	//Arbitrary constants to represent each piece type
	public static final int ROOK = 0; 
	public static final int BISHOP = 1;
	public static final int QUEEN = 2; //Technically redundant with rook and bishop
	public static final int KING = 3;
	public static final int KNIGHT = 4;
	public static final int PAWN = 5;
	public int team; //which team a piece is, equal to WHITE or BLACK
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	private int numMoves = 0; //used by pawns for moving twice, and rooks and kings for castling
	public boolean pawnCanBeEP = false; //If this is a pawn, this flag is true if it just moved, as then it can be captured en passant
	private boolean[] types; //Which piece powers the piece has. This is a boolean array because a Piece could have several types at once
	public int imageIDs[];
	//X and Y coordinates of the pieces on the board
	public int x; 
	public int y; 
	
	public boolean isAttackingKing = false;
	
	
	public Piece(int type, int team, int x, int y){
		types = new boolean[NUM_TYPES];
		types[type] = true;
		imageIDs = new int[1];
		imageIDs[0] = type;
		this.team = team;
		this.x = x;
		this.y = y;
	}
	public ArrayList<Point> getAggressiveMoves(Board board){
		isAttackingKing = false;
		ArrayList<Point> moves = new ArrayList<Point>();
		if(types[PAWN]) getPawnMoves(moves, board.board);
		if(types[KNIGHT]) getKnightMoves(moves, board.board);
		if(types[BISHOP]) getBishopMoves(moves, board.board, 1000);
		if(types[ROOK]) getRookMoves(moves, board.board, 1000);
		if(types[QUEEN]){
			getBishopMoves(moves, board.board, 1000);
			getRookMoves(moves, board.board, 1000);
		}
		if(types[KING]){
			getBishopMoves(moves, board.board, 1);
			getRookMoves(moves, board.board, 1);
		}
		return moves;
	}
	public ArrayList<Point> getPassiveMoves(Board board){ //Separating this avoids recursive loop
		ArrayList<Point> moves = new ArrayList<Point>();
		if(isOnlyKing() ){
			getCastleMoves(moves, board);
		}
		return moves;
	}
	private boolean[] switchedTypes = new boolean[6];
	public void move(int x, int y, Board b, Piece p, Piece promotion){
		
		if(isOnlyKing() && Math.abs(x - this.x) == 2 && this.x == 3){
			System.out.println("X:" + (x < this.x ? 0 : 7) + " Y:" + y);
			System.out.println("RX: " + (this.x + x) / 2);
			b.movePiece(x < this.x ? 0 : 7, y, (this.x + x) / 2, y, b.board[x < this.x ? 0 : 7][y]);
		}
		if(isOnlyPawn() ) {
			if(Math.abs(y - this.y) == 2) pawnCanBeEP = true;
			if(x != this.x && !b.justTookPiece()) b.removePiece(x, y + (team == WHITE ? 1 : -1));
		}
		switchedTypes = new boolean[6];
		if(p != null){
			for(int i = 0; i < p.types.length; i++){
				if(p.types[i]) {
					if(!types[i]) switchedTypes[i] = true;
					types[i] = true;
				}
			}
		}
		updateimageIDs();
		numMoves++;
		this.x = x;
		this.y = y;
	}
	private void updateimageIDs(){
		if(types[KING] ) {
			int QBR = -1;
			if(types[QUEEN]  || (types[ROOK]  && types[BISHOP] )) QBR = QUEEN;
			else if (types[BISHOP] ) QBR = BISHOP;
			else if(types[ROOK] ) QBR = ROOK;
			if(types[KNIGHT]  && QBR != -1){
				imageIDs = new int[3];
				imageIDs[1] = QBR;
				imageIDs[2] = KNIGHT;
			}
			else if(types[KNIGHT] ){
				imageIDs = new int[2];
				imageIDs[1] = KNIGHT;
			}
			else if(QBR != -1){
				imageIDs = new int[2];
				imageIDs[1] = QBR;
			}
			else{
				imageIDs = new int[1];
			}
			imageIDs[0] = KING;
		}
		else if(types[QUEEN]  || (types[ROOK]  && types[BISHOP] )) {
			if(types[KNIGHT] == false){
				imageIDs = new int[1];
			}
			else{
				imageIDs = new int[2];
				imageIDs[1] = KNIGHT;
			}
			imageIDs[0] = QUEEN;
				
		}
		else if(types[ROOK] ) {
			if(types[KNIGHT]  && types[PAWN] ){
				imageIDs = new int[3];
				imageIDs[1] = KNIGHT;
				imageIDs[2] = PAWN;
			}
			else if(types[KNIGHT] ){
				imageIDs = new int[2];
				imageIDs[1] = KNIGHT;
			}
			else if(types[PAWN] ){
				imageIDs = new int[2];
				imageIDs[1] = PAWN;
			}
			else{
				imageIDs = new int[1];
			}
			imageIDs[0] = ROOK;
		}
		else if(types[BISHOP] ) {
			if(types[KNIGHT]  && types[PAWN] ){
				imageIDs = new int[3];
				imageIDs[1] = KNIGHT;
				imageIDs[2] = PAWN;
			}
			else if(types[KNIGHT] ){
				imageIDs = new int[2];
				imageIDs[1] = KNIGHT;
			}
			else if(types[PAWN] ){
				imageIDs = new int[2];
				imageIDs[1] = PAWN;
			}
			else{
				imageIDs = new int[1];
			}
			imageIDs[0] = BISHOP;
		}
		else if(types[KNIGHT] ) {
			if(types[PAWN] ){
				imageIDs = new int[2];
				imageIDs[1] = PAWN;
			}
			else{
				imageIDs = new int[1];
			}
			imageIDs[0] = KNIGHT;
		}
		else {
			imageIDs = new int[1];
			imageIDs[0] = PAWN;
		}
	}
	public void unMove(int x, int y, Board b, Piece promotion){
		if(isOnlyKing() && Math.abs(x - this.x) == 2){
			b.undoMove(x > this.x ? 0 : 7, y, (this.x + x) / 2, y, b.board[(this.x + x) / 2][y], false);
		}
		if(types[PAWN]) {
			if(Math.abs(y - this.y) == 2) pawnCanBeEP = false;
			if(x != this.x && b.board[x][y] == null) b.addPiece(this.x, y + (team == WHITE ? 1 : -1), null);
		}
		for(int i = 0; i < switchedTypes.length;i++){
			if(types[i] && switchedTypes[i]){
				types[i] = false;
			}
		}
		updateimageIDs();
		numMoves--;
		this.x = x;
		this.y = y;
	}
	public boolean isKing(){
		return types[KING];
	}
	/*
	 * Returns true if and only if the piece is a king that has not inherited the powers of any other piece type
	 */
	public boolean isOnlyKing(){
		for(int i = 0; i < 6; i++){
			if(types[i] && i != KING) return false;
			if(!types[i] && i == KING) return false;
		}
		return true;
	}
	public boolean isOnlyPawn(){
		for(int i = 0; i < 6; i++){
			if(types[i] && i != PAWN) return false;
			if(!types[i]  && i == PAWN) return false;
		}
		return true;
	}
	public Piece createPromotion(int type){
		Piece p = new Piece(type, team, x , y);
		for(int i = 0; i < types.length; i++){
			if(types[i]) p.types[i] = true;
		}
		return p;
	}
	private void getPawnMoves(ArrayList<Point> moves, Piece[][] board){
		int dy = 0;
		if(team == WHITE) dy = -1;
		else if(team == BLACK) dy = 1;
		int max = 1;
		if(numMoves == 0) max = 2;
		getMovesInDirection(-1, dy, x, y, board, 1, moves, true, true);
		getMovesInDirection(0, dy, x, y, board, max, moves, false, false);
		getMovesInDirection(1, dy, x, y, board, 1, moves, true, true);
		if(x > 0 && board[x-1][y] != null && board[x-1][y].pawnCanBeEP && board[x-1][y].team != team) moves.add(new Point(x-1, y + (team == WHITE ? -1 : 1)));
		if(x < 7 && board[x+1][y] != null && board[x+1][y].pawnCanBeEP && board[x+1][y].team != team) moves.add(new Point(x+1, y + (team == WHITE ? -1 : 1)));
	}
	private void getKnightMoves(ArrayList<Point> moves, Piece[][] board){
		getMovesInDirection(2, -1, x, y, board, 1, moves, true, false);
		getMovesInDirection(2, 1, x, y, board, 1, moves, true, false);
		getMovesInDirection(-2, 1, x, y, board, 1, moves, true, false);
		getMovesInDirection(-2, -1, x, y, board, 1, moves, true, false);
		getMovesInDirection(1, 2, x, y, board, 1, moves, true, false);
		getMovesInDirection(1, -2, x, y, board, 1, moves, true, false);
		getMovesInDirection(-1, 2, x, y, board, 1, moves, true, false);
		getMovesInDirection(-1, -2, x, y, board, 1, moves, true, false);
	}
	private void getBishopMoves(ArrayList<Point> moves, Piece[][] board, int distance){
		getMovesInDirection(1,1, x, y, board, distance, moves, true, false);
		getMovesInDirection(-1,-1, x, y, board, distance, moves, true, false);
		getMovesInDirection(1,-1, x, y, board, distance, moves, true, false);
		getMovesInDirection(-1,1, x, y, board, distance, moves, true, false);
	}
	private void getRookMoves(ArrayList<Point> moves, Piece[][] board, int distance){
		getMovesInDirection(0,1, x, y, board, distance, moves, true, false);
		getMovesInDirection(0,-1, x, y, board, distance, moves, true, false);
		getMovesInDirection(1,0, x, y, board, distance, moves, true, false);
		getMovesInDirection(-1,0, x, y, board, distance, moves, true, false);
	}
	private void getCastleMoves(ArrayList<Point> moves, Board board){
		if(numMoves != 0) return;
		if(board.board[0][y] != null && board.board[0][y].team == team && board.board[0][y].numMoves == 0 && numMoves == 0){
			boolean isValid = true;
			for(int i = 0; i <= x; i++){
				int otherTeam = team == WHITE ? BLACK : WHITE;
				if(board.isSquareAttacked(i, y, otherTeam) || (board.board[i][y] != null && i != x && i != 0)){
					isValid = false;
					break;
				}
			}
			if(isValid) moves.add(new Point(x - 2, y));
		}
		if(board.board[0][y] != null && board.board[7][y].team == team && board.board[7][y].numMoves == 0 && numMoves == 0){
			boolean isValid = true;
			for(int i = x; i <= 7; i++){
				int otherTeam = team == WHITE ? BLACK : WHITE;
				if(board.isSquareAttacked(i, y, otherTeam) || (board.board[i][y] != null && i != x && i != 7)){
					isValid = false;
					break;
				}
			}
			if(isValid) moves.add(new Point(x + 2, y));
		}
	}
	private void getMovesInDirection(int dx, int dy, int x, int y, Piece[][] board, int maxDistance, ArrayList<Point> moves, boolean canTake, boolean mustTake){
		for(int i = 0; i < maxDistance; i++){
			x += dx;
			y += dy;
			boolean isTaken = true;
			try{
				if(board[x][y] != null) isTaken = true;
				else isTaken = false;
			}catch(Exception e){
				break;
			}
			if(!isTaken && !mustTake) add(moves, new Point(x, y));
			else if (!isTaken) break;
			else if (board[x][y].team != team) {
				if(canTake) {
					add(moves, new Point(x, y));
					if(board[x][y].isKing()) {
						isAttackingKing = true;
					}
				}
				break;
			}
			else{
				break;
			}
		}
	}
	public static void add(ArrayList<Point> moves, Point p){
		if(!moves.contains(p)) moves.add(p);
	}
	
}
