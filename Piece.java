
import java.awt.Point;
import java.util.ArrayList;

public class Piece {
	public static final int NUM_TYPES = 6;
	public static final int ROOK = 0;
	public static final int BISHOP = 1;
	public static final int QUEEN = 2;
	public static final int KING = 3;
	public static final int KNIGHT = 4;
	public static final int PAWN = 5;
	public int team; //true = white
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	private int numMoves = 0; //used by pawns for moving twice, and rooks and kings for castling
	public boolean pawnCanBeEP = false;
	private boolean[] types;
	public int imageID[];
	public int x;
	public int y;
	public boolean isAttackingKing = false;
	public Piece(int type, int team, int x, int y){
		types = new boolean[NUM_TYPES];
		types[type] = true;
		imageID = new int[1];
		imageID[0] = type;
		this.team = team;
		this.x = x;
		this.y = y;
	}
	public ArrayList<Point> getAggressiveMoves(Board board){
		isAttackingKing = false;
		ArrayList<Point> moves = new ArrayList<Point>();
		if(types[PAWN] == true) getPawnMoves(moves, board.board);
		if(types[KNIGHT] == true) getKnightMoves(moves, board.board);
		if(types[BISHOP] == true) getBishopMoves(moves, board.board, 1000);
		if(types[ROOK] == true) getRookMoves(moves, board.board, 1000);
		if(types[QUEEN] == true){
			getBishopMoves(moves, board.board, 1000);
			getRookMoves(moves, board.board, 1000);
		}
		if(types[KING] == true){
			getBishopMoves(moves, board.board, 1);
			getRookMoves(moves, board.board, 1);
		}
		return moves;
	}
	public ArrayList<Point> getPassiveMoves(Board board){ //Separating this avoids recursive loop
		ArrayList<Point> moves = new ArrayList<Point>();
		if(isOnlyKing() == true){
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
		if(isOnlyPawn() == true) {
			if(Math.abs(y - this.y) == 2) pawnCanBeEP = true;
			if(x != this.x && !b.justTookPiece()) b.removePiece(x, y + (team == WHITE ? 1 : -1));
		}
		switchedTypes = new boolean[6];
		if(p != null){
			for(int i = 0; i < p.types.length; i++){
				if(p.types[i]) {
					if(types[i] != true) switchedTypes[i] = true;
					types[i] = true;
				}
			}
		}
		updateImageID();
		numMoves++;
		this.x = x;
		this.y = y;
	}
	private void updateImageID(){
		if(types[KING] == true) {
			int QBR = -1;
			if(types[QUEEN] == true || (types[ROOK] == true && types[BISHOP] == true)) QBR = QUEEN;
			else if (types[BISHOP] == true) QBR = BISHOP;
			else if(types[ROOK] == true) QBR = ROOK;
			if(types[KNIGHT] == true && QBR != -1){
				imageID = new int[3];
				imageID[1] = QBR;
				imageID[2] = KNIGHT;
			}
			else if(types[KNIGHT] == true){
				imageID = new int[2];
				imageID[1] = KNIGHT;
			}
			else if(QBR != -1){
				imageID = new int[2];
				imageID[1] = QBR;
			}
			else{
				imageID = new int[1];
			}
			imageID[0] = KING;
		}
		else if(types[QUEEN] == true || (types[ROOK] == true && types[BISHOP] == true)) {
			if(types[KNIGHT] == false){
				imageID = new int[1];
			}
			else{
				imageID = new int[2];
				imageID[1] = KNIGHT;
			}
			imageID[0] = QUEEN;
				
		}
		else if(types[ROOK] == true) {
			if(types[KNIGHT] == true && types[PAWN] == true){
				imageID = new int[3];
				imageID[1] = KNIGHT;
				imageID[2] = PAWN;
			}
			else if(types[KNIGHT] == true){
				imageID = new int[2];
				imageID[1] = KNIGHT;
			}
			else if(types[PAWN] == true){
				imageID = new int[2];
				imageID[1] = PAWN;
			}
			else{
				imageID = new int[1];
			}
			imageID[0] = ROOK;
		}
		else if(types[BISHOP] == true) {
			if(types[KNIGHT] == true && types[PAWN] == true){
				imageID = new int[3];
				imageID[1] = KNIGHT;
				imageID[2] = PAWN;
			}
			else if(types[KNIGHT] == true){
				imageID = new int[2];
				imageID[1] = KNIGHT;
			}
			else if(types[PAWN] == true){
				imageID = new int[2];
				imageID[1] = PAWN;
			}
			else{
				imageID = new int[1];
			}
			imageID[0] = BISHOP;
		}
		else if(types[KNIGHT] == true) {
			if(types[PAWN] == true){
				imageID = new int[2];
				imageID[1] = PAWN;
			}
			else{
				imageID = new int[1];
			}
			imageID[0] = KNIGHT;
		}
		else imageID[0] = PAWN;
	}
	public void unMove(int x, int y, Board b, Piece promotion){
		if(isOnlyKing() && Math.abs(x - this.x) == 2){
			b.undoMove(x > this.x ? 0 : 7, y, (this.x + x) / 2, y, b.board[(this.x + x) / 2][y], false);
		}
		if(types[PAWN] == true) {
			if(Math.abs(y - this.y) == 2) pawnCanBeEP = false;
			if(x != this.x && b.board[x][y] == null) b.addPiece(this.x, y + (team == WHITE ? 1 : -1), null);
		}
		for(int i = 0; i < switchedTypes.length;i++){
			if(types[i] == true && switchedTypes[i] == true){
				types[i] = false;
			}
		}
		updateImageID();
		numMoves--;
		this.x = x;
		this.y = y;
	}
	public boolean isKing(){
		return types[KING];
	}
	public boolean isOnlyKing(){
		for(int i = 0; i < 6; i++){
			if(types[i] == true && i != KING) return false;
			if(types[i] == false && i == KING) return false;
		}
		return true;
	}
	public boolean isOnlyPawn(){
		for(int i = 0; i < 6; i++){
			if(types[i] == true && i != PAWN) return false;
			if(types[i] == false && i == PAWN) return false;
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
					if(board[x][y].isKing() == true) {
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
