package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class GraphicsWindow implements MouseListener, MouseMotionListener,WindowListener {
	private JFrame frame;
	private JLayeredPane panel;
	private int pixelWidth = 720;
	private int pixelHeight = 720;
	public static final int SQUARE_WIDTH = 720/Board.BOARD_WIDTH;
	public static final int MARGIN_RADIUS = 2;
	private Board board;
	public Image[] pieceImages = new Image[12];
	private int turn = Piece.WHITE;
	private boolean isWaitingOnPromotion = false;
	private Piece toBePromoted;
	private JFrame promotionFrame;
	public static void main(String[] args){
		GraphicsWindow gw = new GraphicsWindow();
		gw.init();
	}
	public void init(){
		frame = new JFrame();
        frame.setSize(pixelWidth, pixelHeight + 20);
        panel = frame.getLayeredPane();
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.addWindowListener(this);
        board = new Board();
        try{
        	BufferedImage bi = ImageIO.read(new File("src/ChessPieces.png"));
        	
        	for(int i = 0; i < 6; i++){
        		for(int j = 0; j < 2; j++){
        			pieceImages[i + 6 * j] = bi.getSubimage(300 * i, j * 400, 300, 400);
        		}
        	}
        }
        catch(Exception e){
        	System.out.println("Image Didn't Load");
        	System.out.println(e.getMessage());
        }
        drawBoard();
	}
	public BufferedImage image;
	public void drawBoard(){
	        JLabel boardLabel = new JLabel();
	        image = new BufferedImage(pixelWidth, pixelHeight, 5);
	    
	        Graphics g = image.getGraphics();
	        g.setColor(Color.black);
	        g.fillRect(0, 0, pixelWidth, pixelHeight);
	        
	        for (int i = 0; i < pixelWidth; i += SQUARE_WIDTH){
	        	for (int j = 0; j < pixelHeight; j += SQUARE_WIDTH){
	        		if ((i + j) % (2 * SQUARE_WIDTH) == 0) g.setColor(new Color(0xDFFF00));
	        		else g.setColor(new Color(0xABCFED));
	        		g.fillRect(i + MARGIN_RADIUS, j + MARGIN_RADIUS, SQUARE_WIDTH - 2 * MARGIN_RADIUS, SQUARE_WIDTH - 2 * MARGIN_RADIUS);
	        	}
	        }
	        boardLabel.setIcon(new ImageIcon(image));
	        boardLabel.setBounds(0, 0, 720, 720);
	        boardLabel.setVisible(true);
	        for(int i = 0; i < Board.BOARD_WIDTH; i++){
	        	for(int j = 0; j < Board.BOARD_WIDTH; j++){
	        		addPiece(i * SQUARE_WIDTH, j * SQUARE_WIDTH);
	        	}
	        }
	        panel.removeAll();
	        panel.add(boardLabel);
	        panel.repaint();
	}
	public void removePiece(int x, int y){
		Graphics g = image.getGraphics();
		if ((x + y) % (2 * SQUARE_WIDTH) == 0) g.setColor(new Color(0xDFFF00));
		else g.setColor(new Color(0xABCFED));
		g.fillRect(x + MARGIN_RADIUS, y + MARGIN_RADIUS, SQUARE_WIDTH - 2 * MARGIN_RADIUS, SQUARE_WIDTH - 2 * MARGIN_RADIUS);
		JLabel boardLabel = new JLabel();
        boardLabel.setIcon(new ImageIcon(image));
        boardLabel.setBounds(0, 0, 720, 720);
        boardLabel.setVisible(true);
		panel.removeAll();
		panel.add(boardLabel);
		panel.repaint();
	}
	public boolean isMoveValid(Piece piece, Point point){
		int status = board.getBoardStatusAfterMove(piece, new Point(point.x ,point.y ));
		return !((status == Board.WIC && turn == Piece.WHITE) || (status == Board.BIC && turn == Piece.BLACK) || status == Board.INV);
	}
	public void addPiece(int x, int y){
		Graphics g = image.getGraphics();
		Piece piece = board.board[x / SQUARE_WIDTH][y / SQUARE_WIDTH];
		if(piece != null){
			try{
				drawPiece(getImageForPiece(piece), g, x , y , SQUARE_WIDTH);
			}
			catch(Exception e) {
				System.out.println("The image didn't read :/");
			}
		}
		panel.repaint();
	}
	public Image[] getImageForPiece(Piece p){
		int i = 6;
		if(p.team == Piece.BLACK) i = 0;
		Image[] images = new Image[p.imageIDs.length];
		for(int j = 0; j < images.length; j++){
			images[j] = pieceImages[p.imageIDs[j] + i];
		}
		return images;
	}
	public void createPromotionFrame(int x, int y, int team){
		promotionFrame = new JFrame("Promote:");
		promotionFrame.setBounds(x * SQUARE_WIDTH , y * SQUARE_WIDTH , SQUARE_WIDTH * 2, SQUARE_WIDTH * 4 * 2);
		BufferedImage image = new BufferedImage(SQUARE_WIDTH * 2, SQUARE_WIDTH * 4 * 2, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = image.getGraphics();
	    g.setColor(Color.black);
	    g.fillRect(0, 0, pixelWidth, pixelHeight);
	    for(int i = 0; i < 4 * SQUARE_WIDTH; i += SQUARE_WIDTH){
	    	if ((i) % (2 * SQUARE_WIDTH) == 0) g.setColor(new Color(0xDFFF00));
    		else g.setColor(new Color(0xABCFED));
    		g.fillRect(MARGIN_RADIUS, i * 2 + MARGIN_RADIUS,  2 * SQUARE_WIDTH - 2 * MARGIN_RADIUS, 2 * SQUARE_WIDTH - 2 * MARGIN_RADIUS);
	    }
	    Piece[] options = {new Piece(Piece.BISHOP, team, x , y), new Piece(Piece.KNIGHT, team, x , y), new Piece(Piece.QUEEN, team, x , y), new Piece(Piece.ROOK, team, x , y)};
	    for(int i = 0; i < options.length; i++){
	    	drawPiece(getImageForPiece(options[i]), g, 0, i * 2 * SQUARE_WIDTH , SQUARE_WIDTH * 2);
	    }
	    JLabel promotion = new JLabel();
	    promotion.setIcon(new ImageIcon(image));
	    promotion.setBounds(0, 0, SQUARE_WIDTH * 2 + 20, SQUARE_WIDTH * 4 * 2 + 20);
	    promotionFrame.addMouseListener(this);
	    promotionFrame.setVisible(true);
	    promotionFrame.add(promotion);
	    promotionFrame.repaint();
	    isWaitingOnPromotion = true;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if(!isWaitingOnPromotion){
			int x = e.getX();
			int y = e.getY();
			x -= x % SQUARE_WIDTH;
			y -= y % SQUARE_WIDTH;
			if(board.board[x / SQUARE_WIDTH][y / SQUARE_WIDTH] != null){
				if(board.board[x / SQUARE_WIDTH][y / SQUARE_WIDTH].team == turn){
					ArrayList<Point> moves = board.board[x / SQUARE_WIDTH][y / SQUARE_WIDTH].getAggressiveMoves(board);
					for(Point p : board.board[x / SQUARE_WIDTH][y / SQUARE_WIDTH].getPassiveMoves(board)) moves.add(p);
					Graphics g = image.getGraphics();
					for(Point p : moves){
						if(isMoveValid(board.board[x / SQUARE_WIDTH][y / SQUARE_WIDTH],p)){
							g.setColor(new Color(0, 0, 0, 128));
							g.fillRect(p.x * SQUARE_WIDTH + MARGIN_RADIUS, p.y * SQUARE_WIDTH + MARGIN_RADIUS, SQUARE_WIDTH - 2 * MARGIN_RADIUS, SQUARE_WIDTH - 2 * MARGIN_RADIUS);
						}
					}
				}
			}
		}
		else{
			int id = e.getY() / (2 * SQUARE_WIDTH);
			int type = 0;
			if(id == 0) type = Piece.BISHOP;
			else if(id == 1) type = Piece.KNIGHT;
			else if(id == 2) type = Piece.QUEEN;
			else type = Piece.ROOK;
			Piece p = toBePromoted.createPromotion(type);
			board.promotePiece(toBePromoted.x, toBePromoted.y, p);
			if(turn == Piece.WHITE) turn = Piece.BLACK;
			else turn = Piece.WHITE;
			promotionFrame.dispose();
			promotionFrame = null;
			isWaitingOnPromotion = false;
			toBePromoted = null;
			drawBoard();
			
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		x -= x % SQUARE_WIDTH;
		y -= y % SQUARE_WIDTH;
		if(board.board[x / SQUARE_WIDTH][y / SQUARE_WIDTH] != null){
			if(board.board[x / SQUARE_WIDTH][y / SQUARE_WIDTH].team == turn){
				removePiece(x, y);
				selectedpieceX = x / SQUARE_WIDTH;
				selectedpieceY = y / SQUARE_WIDTH;
				selectedPiece = board.board[selectedpieceX][selectedpieceY];
			}
		}
		mouseDragged(e);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		x -= x % SQUARE_WIDTH;
		y -= y % SQUARE_WIDTH;
		if(selectedPiece != null){
			panel.remove(aboveLabel);
			ArrayList<Point> moves = selectedPiece.getAggressiveMoves(board);
			for(Point p : selectedPiece.getPassiveMoves(board)) Piece.add(moves, p);
			if(moves.contains(new Point(x / SQUARE_WIDTH,y / SQUARE_WIDTH)) && isMoveValid(selectedPiece, new Point(x / SQUARE_WIDTH,y / SQUARE_WIDTH))){	
				board.onMoveDone();
				board.movePiece(selectedpieceX, selectedpieceY, x / SQUARE_WIDTH, y / SQUARE_WIDTH, selectedPiece);
				if(selectedPiece.isOnlyPawn() && ((selectedPiece.team == Piece.WHITE && y / SQUARE_WIDTH == 0) || (selectedPiece.team == Piece.BLACK && y / SQUARE_WIDTH == 7))){
					toBePromoted = selectedPiece;
					createPromotionFrame(x / SQUARE_WIDTH, y / SQUARE_WIDTH, selectedPiece.team);
				}
				else{
					if(turn == Piece.WHITE) turn = Piece.BLACK;
					else turn = Piece.WHITE;
				}
			}
			drawBoard();
			selectedPiece = null;
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	Piece selectedPiece = null;
	int selectedpieceX = 0;
	int selectedpieceY = 0;
	JLabel aboveLabel;
	@Override
	public void mouseDragged(MouseEvent e) {
		if(selectedPiece == null) return;
		if(aboveLabel != null)
			panel.remove(aboveLabel);
		aboveLabel = new JLabel();
		BufferedImage image = new BufferedImage(SQUARE_WIDTH, SQUARE_WIDTH, BufferedImage.TYPE_4BYTE_ABGR);
		
		drawPiece(getImageForPiece(selectedPiece),image.getGraphics(), 0, 0, SQUARE_WIDTH);
		aboveLabel.setIcon(new ImageIcon(image));
		aboveLabel.setBounds(e.getX() - 36, e.getY() - 36, SQUARE_WIDTH, SQUARE_WIDTH);
		aboveLabel.setVisible(true);
		aboveLabel.setOpaque(false);
		panel.add(aboveLabel);
		panel.moveToFront(aboveLabel);
		panel.repaint();
	}
	@Override
	public void mouseMoved(MouseEvent e) {}
	public void drawPiece(Image[] peices, Graphics g, int x, int y, int width){
		g.drawImage(peices[0], x, y, width, width, null);
		if(peices.length > 1) g.drawImage(peices[1], x + 2 * width / 3, y, width / 3, width / 3, null);
		if(peices.length > 2) g.drawImage(peices[2], x, y, width / 3, width / 3, null);
		if(peices.length > 3) g.drawImage(peices[2], x, y + 2 * width / 3, width / 3, width / 3, null);
	}
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {
		System.exit(0);
	}
	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
}
