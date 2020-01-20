# Absorption-Chess
A digital chess board that follows the rules of Absorption chess.  In Absorption chess, a piece gains all of the powers of a piece it captures.  For example, if a bishop takes a rook, then it becomes functionally the same as a queen.  This is cumulative;  If a pawn takes a bishop that took a knight earlier in the game, it gains the powers of all three pieces. Pawns can promote only if they do not absorb any other piece.
This Program is designed around 3 classes. The first, Graphics Window, handles mouselistening and rendering the board.  It reads the board to display the peices in their correct positions.  For a peice with multiple funtions, it will show the highest value function of the peice (IE: A rook that can also move like a knight will appear as a rook), with lower value peices displayed as smaller icons in the corner. The piece priority order for disaply is King > Queen > Rook > Bishop > Knight > Pawn. It ignores redundant icons (IE: A Queen that can move like a pawn does not show the pawn, because a Queen can already do anything a pawn can).  Players move by dragging a peice to the square, and clicking on a peice highlights the options.  The game does not allow the player to play a move that keeps them in check, as this would be illegal under normal chess rules. The GrahpicsWindow Class also creates an additional screen to choose which type of piece to promote a pawn to. Pawns can only promote to vanilla pieces, but they can gain more powers after promoting.
The second class, Board, handles the position of all the pieces in the board.  It stores a reference to each peice by location in an 8 by 8 array, and also in a single arraylist.  The Board class also is used to determine if either team is in check.
The final class, Piece, handles the individual pieces.  It stores a boolean array that has an entry for every possible function, and is responsible for determining which spaces a piece can move to.  It also helps determine the proper image configuration for the GrahpicsWindow class to display, and handles peices with special moves, such as En Passant captures and castling.
In order to run, the program has to have a file called "ChessPieces.png" in the same folder as itself that contains 300 by 300 pixel images of all the chess peices for the two teams.
