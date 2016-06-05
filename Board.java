
import java.awt.Point;
import java.util.ArrayList;

public class Board {
	public static final int BOARD_WIDTH = 8;
	public Piece[][] board;
	private ArrayList<Piece> pieces = new ArrayList<Piece>();
	public Board(){
		board = new Piece[BOARD_WIDTH][BOARD_WIDTH];
		setUpBoard();
	}
	public void setUpBoard(){
		for(int i = 0 ;i < BOARD_WIDTH; i++){
			Piece p1 = new Piece(Piece.PAWN, Piece.BLACK, i, 1);
			addPiece(i,1,p1);
			Piece p2 = new Piece(Piece.PAWN, Piece.WHITE, i, BOARD_WIDTH - 2);
			addPiece(i,BOARD_WIDTH - 2, p2);
			if(i == 0 || i == 7){
				Piece r1 = new Piece(Piece.ROOK, Piece.BLACK, i, 0);
				Piece r2 = new Piece(Piece.ROOK, Piece.WHITE,i ,BOARD_WIDTH - 1);
				addPiece(i,0,r1);
				addPiece(i,BOARD_WIDTH - 1,r2);
			}
			else if(i == 1 || i == 6){
				Piece r1 = new Piece(Piece.KNIGHT, Piece.BLACK, i, 0);
				Piece r2 = new Piece(Piece.KNIGHT, Piece.WHITE,i ,BOARD_WIDTH - 1);
				addPiece(i,0,r1);
				addPiece(i,BOARD_WIDTH - 1,r2);
			}
			else if(i == 2 || i == 5){
				Piece r1 = new Piece(Piece.BISHOP, Piece.BLACK, i, 0);
				Piece r2 = new Piece(Piece.BISHOP, Piece.WHITE,i ,BOARD_WIDTH - 1);
				addPiece(i,0,r1);
				addPiece(i,BOARD_WIDTH - 1,r2);
			}
			else if(i == 3 ){
				Piece r1 = new Piece(Piece.KING, Piece.BLACK, i, 0);
				Piece r2 = new Piece(Piece.KING, Piece.WHITE,i ,BOARD_WIDTH - 1);
				addPiece(i,0,r1);
				addPiece(i,BOARD_WIDTH - 1,r2);
			}
			else if(i == 4){
				Piece r1 = new Piece(Piece.QUEEN, Piece.BLACK, i, 0);
				Piece r2 = new Piece(Piece.QUEEN, Piece.WHITE,i ,BOARD_WIDTH - 1);
				addPiece(i,0,r1);
				addPiece(i,BOARD_WIDTH - 1,r2);
			}
		}
	}
	public void movePiece(int x1, int y1, int x2, int y2, Piece p){
		removePiece(x1, y1);
		removePiece(x2, y2);
		addPiece(x2, y2, p);
		p.move(x2, y2, this, recentlyRemovedPiece, null);
	}
	public void promotePiece(int x, int y, Piece p){
		removePiece(x, y);
		addPiece(x, y, p);
	}
	public void undoMove(int x1, int y1, int x2, int y2, Piece p, boolean placeRemoved){
		Piece rp = recentlyRemovedPiece;
		if(!placeRemoved) rp = null;
		removePiece(x2, y2);
		addPiece(x1, y1, p);
		if(rp != null) addPiece(x2, y2, rp);
		p.unMove(x1, y1, this, null);
	}
	public void addPiece(int x, int y, Piece p){
		if(p == null) p = recentlyRemovedPiece;
		board[x][y] = p;
		pieces.add(p);
	}
	private Piece recentlyRemovedPiece = null;
	public void removePiece(int x, int y){
		recentlyRemovedPiece = board[x][y];
		pieces.remove(board[x][y]);
		board[x][y] = null;
	}
	public static final int NORMAL = 0;
	public static final int WIC = 1;
	public static final int BIC = 2;
	public static final int INV = 3;
	public int getBoardStatusAfterMove(Piece p, Point move){
		int status = 0;
		int x = p.x;
		int y = p.y;
		movePiece(x, y, move.x, move.y, p);
		for(Piece i : pieces){
			i.getAggressiveMoves(this);
			if(i.isAttackingKing == true) {
				if(i.team == Piece.WHITE && status < 2)
					status += BIC;
				if(i.team == Piece.BLACK && status % 2 == 0)
					status += WIC;
				break;
			}
		}
		undoMove(x, y, move.x, move.y, p, true);
		return status;
	}
	public boolean isSquareAttacked(int x, int y, int team){
		boolean isAttacked = false;
		for(Piece piece : pieces){
			if(piece.team == team){
				if(piece.getAggressiveMoves(this).contains(new Point(x, y))){
					isAttacked = true;
					break;
				}
			}
		}
		return isAttacked;
	}
	public void onMoveDone(){
		for(Piece p : pieces){
			p.pawnCanBeEP = false;
		}
	}
	public boolean justTookPiece(){
		return recentlyRemovedPiece != null;
	}

}
