/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.*;

/**
 *
 * @author TIUS
 */
public class TicTacToe extends JFrame {

    // Name-constants to represent the seeds and cell contents
    public static final int EMPTY = 0;
    public static final int CROSS = 1;
    public static final int NOUGHT = 2;

    // Name-constants to represent the various states of the game
    public static final int PLAYING = 0;
    public static final int DRAW = 1;
    public static final int CROSS_WON = 2;
    public static final int NOUGHT_WON = 3;

    // The game board and the game status
    public static final int ROWS = 3, COLS = 3; // number of rows and columns
    public static int[][] board = new int[ROWS][COLS]; // game board in 2D array
                                                       //  containing (EMPTY, CROSS, NOUGHT)
    
    public static final int CELL_SIZE = 100;  // constant to draw each cell
    //Size of Canvas to Draw Tic Tac Toe
    public static final int CANVAS_WIDTH = CELL_SIZE * COLS;  // the width canvas
    public static final int CANVAS_HEIGHT = CELL_SIZE * ROWS; // the height canvas
    
    public static final int GRID_WIDTH = 8;                   // Grid-line's width
    public static final int GRID_WIDHT_HALF = GRID_WIDTH / 2; // Grid-line's half-width
    
    // Symbols (cross/nought) are displayed inside a cell, with padding from border
    public static final int CELL_PADDING = CELL_SIZE / 6;
    public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2; // width/height
    public static final int SYMBOL_STROKE_WIDTH = 8; // pen's stroke width to draw CROSS OR NOUGHT
    
    public static int currentState;  // the current state of the game
                                     // (PLAYING, DRAW, CROSS_WON, NOUGHT_WON)
    public static int currentPlayer; // the current player (CROSS or NOUGHT)
    public static int currentRow, currentCol; // current seed's row and column

    public static Scanner in = new Scanner(System.in); // the input Scanner
    
    private DrawCanvas canvas; // Drawing canvas (JPanel) for the game board
    private JLabel statusBar;  // Status Bar
    
    /** Constructor to setup the game and the GUI components */
    public TicTacToe() {
        canvas = new DrawCanvas();  // Construct a drawing canvas (a JPanel)
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        // The canvas (JPanel) fires a MouseEvent upon mouse-click
        canvas.addMouseListener (new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
                int mouseX = e.getX();
                int mouseY = e.getY();
                // Get the row and column clicked
                int rowSelected = mouseY / CELL_SIZE;
                int colSelected = mouseX / CELL_SIZE;

                if (currentState == PLAYING) {
                    if(currentPlayer==CROSS){ //the player moves
                        if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0
                              && colSelected < COLS && board[rowSelected][colSelected] == EMPTY) {
                            board[rowSelected][colSelected] = currentPlayer; // Make a move
                            updateGame(currentPlayer, rowSelected, colSelected); // update state
                            
                            // Switch player
                            currentPlayer = (currentPlayer == CROSS) ? NOUGHT : CROSS;
                            if(currentPlayer==NOUGHT){ // the AI Moves

                                Map res = minimax(2, NOUGHT);
                                int row_rand = (int) res.get("bestrow");
                                int col_rand = (int) res.get("bestcol");

                                //add Checker if the last cell is fill with Player, the return of minimax will -1
                                //Add this to remove index out of bounds
                                if (row_rand >= 0 && row_rand < ROWS && col_rand >= 0
                                && col_rand < COLS && board[row_rand][col_rand] == EMPTY) {
                                    board[row_rand][col_rand] = currentPlayer;  // update game-board content
                                    updateGame(currentPlayer, row_rand, col_rand); // update state
                                }
                                // Switch player
                                currentPlayer = (currentPlayer == CROSS) ? NOUGHT : CROSS;
                            }
                            
                            
                        }
                    }
                } else {       // game over
                    initGame(); // restart the game
                }
                // Refresh the drawing canvas
                repaint();  // Call-back paintComponent().
            }
        });
        
        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel("  ");
        statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(canvas, BorderLayout.CENTER);
        cp.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();  // pack all the components in this JFrame
        setTitle("Tic Tac Toe");
        setVisible(true);  // show this JFrame

        board = new int[ROWS][COLS]; // allocate array
        initGame(); // initialize the game board contents and game variables
    }
    
    /** The entry main method (the program starts here) */
    public static void main(String[] args) {
        
        // Run GUI codes in the Event-Dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TicTacToe(); // Let the constructor do the job  
            }
        });
//        // Initialize the game-board and current status
//        initGame();
//        // Play the game once
//        do {
//           playerMove(currentPlayer); // update currentRow and currentCol
//           updateGame(currentPlayer, currentRow, currentCol); // update currentState
//           printBoard();
//           // Print message if game-over
//           if (currentState == CROSS_WON) {
//              System.out.println("'X' won! Bye!");
//           } else if (currentState == NOUGHT_WON) {
//              System.out.println("'O' won! Bye!");
//           } else if (currentState == DRAW) {
//              System.out.println("It's a Draw! Bye!");
//           }
//           // Switch player
//           currentPlayer = (currentPlayer == CROSS) ? NOUGHT : CROSS;
//        } while (currentState == PLAYING); // repeat if not game-over
    }
 
    /** Initialize the game-board contents and the current states */
    public static void initGame() {
       for (int row = 0; row < ROWS; ++row) {
          for (int col = 0; col < COLS; ++col) {
             board[row][col] = EMPTY;  // all cells empty
          }
       }
       currentState = PLAYING; // ready to play
       currentPlayer = CROSS;  // cross plays first
    }
 
    /** NOT USED Because use GUI */
    /** Player with the "theSeed" makes one move, with input validation.
        Update global variables "currentRow" and "currentCol". */
    public static void playerMove(int theSeed) {
       boolean validInput = false;  // for input validation
        do {
            if (theSeed == CROSS) { // CROSS as the player
                System.out.print("Player 'X', enter your move (row[1-3] column[1-3]): ");
                int row = in.nextInt() - 1;  // array index starts at 0 instead of 1
                int col = in.nextInt() - 1;
                if (row >= 0 && row < ROWS && col >= 0 && col < COLS && board[row][col] == EMPTY) {
                   currentRow = row;
                   currentCol = col;
                   board[currentRow][currentCol] = theSeed;  // update game-board content
                   validInput = true;  // input okay, exit loop
                } else {
                  System.out.println("This move at (" + (row + 1) + "," + (col + 1)
                       + ") is not valid. Try again...");
                }
            } 
            else { //NOUGHT as the AI
                
                Map res = minimax(2, NOUGHT);
                int row_rand = (int) res.get("bestrow");
                int col_rand = (int) res.get("bestcol");

                if (row_rand >= 0 && row_rand < ROWS && col_rand >= 0 && col_rand < COLS && board[row_rand][col_rand] == EMPTY) {
                    currentRow = row_rand;
                    currentCol = col_rand;
                    board[currentRow][currentCol] = theSeed;  // update game-board content
                    validInput = true;  // input okay, exit loop
                    System.out.print("Player 'O', enter your move (row[1-3] column[1-3]): "+(row_rand+1)+" "+(col_rand+1)+"\n");
                }
            }

        } while (!validInput);  // repeat until input is valid
    }
 
    /** Update the "currentState" after the player with "theSeed" has placed on
        (currentRow, currentCol). */
    public static void updateGame(int theSeed, int currentRow, int currentCol) {
        if (hasWon(theSeed, currentRow, currentCol)) {  // check if winning move
           currentState = (theSeed == CROSS) ? CROSS_WON : NOUGHT_WON;
        } else if (isDraw()) {  // check for draw
            currentState = DRAW;
        }
        // Otherwise, no change to currentState (still PLAYING).
    }
 
    /** Return true if it is a draw (no more empty cell) */
    // TODO: Shall declare draw if no player can "possibly" win
    public static boolean isDraw() {
        for (int row = 0; row < ROWS; ++row) {
           for (int col = 0; col < COLS; ++col) {
              if (board[row][col] == EMPTY) {
                 return false;  // an empty cell found, not draw, exit
              }
           }
        }
        return true;  // no empty cell, it's a draw
    }
 
    /** Return true if the player with "theSeed" has won after placing at
        (currentRow, currentCol) */
    public static boolean hasWon(int theSeed, int currentRow, int currentCol) {
        return (board[currentRow][0] == theSeed         // 3-in-the-row
                    && board[currentRow][1] == theSeed
                    && board[currentRow][2] == theSeed
                || board[0][currentCol] == theSeed      // 3-in-the-column
                    && board[1][currentCol] == theSeed
                    && board[2][currentCol] == theSeed
                || currentRow == currentCol            // 3-in-the-diagonal
                    && board[0][0] == theSeed
                    && board[1][1] == theSeed
                    && board[2][2] == theSeed
                || currentRow + currentCol == 2  // 3-in-the-opposite-diagonal
                    && board[0][2] == theSeed
                    && board[1][1] == theSeed
                    && board[2][0] == theSeed);
    }
    /** NOT USED because use GUI */
    /** Print the game board */
    public static void printBoard() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                printCell(board[row][col]); // print each of the cells
                if (col != COLS - 1) {
                   System.out.print("|");   // print vertical partition
                }
            }
            System.out.println();
            if (row != ROWS - 1) {
                System.out.println("-----------"); // print horizontal partition
            }
        }
        System.out.println();
    }
   
    /** NOT USED - Because use GUI */
    /** Print a cell with the specified "content" */
    public static void printCell(int content) {
        switch (content) {
            case EMPTY:  System.out.print("   "); break;
            case NOUGHT: System.out.print(" O "); break;
            case CROSS:  System.out.print(" X "); break;
        }
    }

    /** evaluation function of minimax */
    public static int evaluate(){
        int result=0;
        // Evaluate score for each of the 8 lines (3 rows, 3 columns, 2 diagonals)
        result += evalLine(0, 0, 0, 1, 0, 2);  // row 0
        result += evalLine(1, 0, 1, 1, 1, 2);  // row 1
        result += evalLine(2, 0, 2, 1, 2, 2);  // row 2
        result += evalLine(0, 0, 1, 0, 2, 0);  // col 0
        result += evalLine(0, 1, 1, 1, 2, 1);  // col 1
        result += evalLine(0, 2, 1, 2, 2, 2);  // col 2
        result += evalLine(0, 0, 1, 1, 2, 2);  // diagonal
        result += evalLine(0, 2, 1, 1, 2, 0);  // alternate diagonal
        
        return result;
    }
    /** the heuristic function of tic tac toe:
     * @Return +100, +10, +1 for 3-, 2-, 1-in-a-line for computer.
        -100, -10, -1 for 3-, 2-, 1-in-a-line for opponent.
        0 otherwise
     */
    public static int evalLine(int row1,int col1, int row2,int col2, int row3, int col3){
        int score=0;
        //First cell
        if(board[row1][col1]==NOUGHT){
            score=1;
        }
        else if(board[row1][col1]==CROSS){
            score=-1;
        }
        // Second cell
        if (board[row2][col2] == NOUGHT) {
            if (score == 1) {   // cell1 is NOUGHT
                score = 10;
            } else if (score == -1) {  // cell1 is CROSS
                score = 0;
            } else {  // cell1 is empty
                score = 1;
            }
        } else if (board[row2][col2] == CROSS) {
            if (score == -1) { // cell1 is CROSS
                score = -10;
            } else if (score == 1) { // cell1 is NOUGHT
                score = 0;
            } else {  // cell1 is empty
                score = -1;
            }
        }
        if(score>0){   
            // Third cell
            if (board[row3][col3] == NOUGHT) {
                if (score > 0) {  // cell1 and/or cell2 is NOUGHT
                    score *= 10;
                } else if (score < 0) {  // cell1 and/or cell2 is CROSS
                    score = 0;
                } else {  // cell1 and cell2 are empty
                    score = 1;
                }
            } else if (board[row3][col3] == CROSS) {
                if (score < 0) {  // cell1 and/or cell2 is NOUGHT
                    score *= 10;
                } else if (score > 1) {  // cell1 and/or cell2 is CROSS
                    score = 0;
                } else {  // cell1 and cell2 are empty
                    score = -1;
                }
            }
        }
        return score;
    }
    /** Recursive minimax at level of depth for either maximizing or minimizing player.
       Return Map which has field: bestscore, bestrow, bestcol  */
    public static Map minimax(int depth, int player){
        Map result = new HashMap();
        
        // Generate possible next moves in a List of int[2] of {row, col}.
        List<int[]> nextMoves = generateMoves();
        
        int bestScore = (currentPlayer == NOUGHT) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int currentScore;
        int bestRow = -1;
        int bestCol = -1;
        
        if (nextMoves.isEmpty() || depth == 0) {
            // Gameover or depth reached, evaluate score
            bestScore = evaluate();
        }else{
            for (int[] move : nextMoves) {
                // Try this move for the current "player"
                board[move[0]][move[1]] = currentPlayer;
                if (currentPlayer == NOUGHT) {  // NOUGHT is maximizing player
                   currentScore = (int) minimax(depth - 1, CROSS).get("bestscore");
                   if (currentScore > bestScore) {
                      bestScore = currentScore;
                      bestRow = move[0];
                      bestCol = move[1];
                   }
                } else {  // CROSS is minimizing player
                   currentScore = (int) minimax(depth - 1, NOUGHT).get("bestscore");
                   if (currentScore < bestScore) {
                      bestScore = currentScore;
                      bestRow = move[0];
                      bestCol = move[1];
                   }
                }
                // Undo move
                board[move[0]][move[1]] = EMPTY;
         }
        }
        
        result.put("bestscore", bestScore);
        result.put("bestrow", bestRow);
        result.put("bestcol", bestCol);
        
        return result;
    }
    
    /** Find all valid next moves.
       Return List of moves in int[2] of {row, col} or empty list if one of player won */
    public static List<int[]> generateMoves(){
        List <int[]> nextMoves = new ArrayList<int[]>();
        
        //If the game is end i.e. one of the player won, there is no next move
        if(hasAllWon(CROSS) || hasAllWon(NOUGHT)){
            return nextMoves;
        }
        // Search for empty cells and add to the List
        for (int row = 0; row < ROWS; ++row) {
           for (int col = 0; col < COLS; ++col) {
              if (board[row][col] == EMPTY) {
                 nextMoves.add(new int[] {row, col});
              }
           }
        }
        return nextMoves;
    }
    
    /** Return true if the player with "theSeed" has won in all case*/
    public static boolean hasAllWon(int theSeed) {
        return (board[0][0] == theSeed         // 3-in-the-row
                    && board[0][1] == theSeed
                    && board[0][2] == theSeed
                || board[1][0] == theSeed         // 3-in-the-row
                    && board[1][1] == theSeed
                    && board[1][2] == theSeed
                || board[2][0] == theSeed         // 3-in-the-row
                    && board[2][1] == theSeed
                    && board[2][2] == theSeed
                || board[0][0] == theSeed      // 3-in-the-column
                    && board[1][0] == theSeed
                    && board[2][0] == theSeed
                || board[0][1] == theSeed      // 3-in-the-column
                    && board[1][1] == theSeed
                    && board[2][1] == theSeed
                || board[0][2] == theSeed      // 3-in-the-column
                    && board[1][2] == theSeed
                    && board[2][2] == theSeed
                || board[0][0] == theSeed // 3-in-the-diagonal
                    && board[1][1] == theSeed
                    && board[2][2] == theSeed
                || board[0][2] == theSeed  // 3-in-the-opposite-diagonal
                    && board[1][1] == theSeed
                    && board[2][0] == theSeed);
    }
    
    /**
    *  Inner class DrawCanvas (extends JPanel) used for custom graphics drawing.
    */
    class DrawCanvas extends JPanel {
        @Override
        public void paintComponent(Graphics g) {  // invoke via repaint()
            super.paintComponent(g);    // fill background
            setBackground(Color.WHITE); // set its background color

            // Draw the grid-lines
            g.setColor(Color.LIGHT_GRAY);
            for (int row = 1; row < ROWS; ++row) {
               g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDHT_HALF,
                     CANVAS_WIDTH-1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
            }
            for (int col = 1; col < COLS; ++col) {
               g.fillRoundRect(CELL_SIZE * col - GRID_WIDHT_HALF, 0,
                     GRID_WIDTH, CANVAS_HEIGHT-1, GRID_WIDTH, GRID_WIDTH);
            }

            // Draw the Seeds of all the cells if they are not empty
            // Use Graphics2D which allows us to set the pen's stroke
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));  // Graphics2D only
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    int x1 = col * CELL_SIZE + CELL_PADDING;
                    int y1 = row * CELL_SIZE + CELL_PADDING;
                    if (board[row][col] == CROSS) {
                       g2d.setColor(Color.RED);
                       int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
                       int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
                       g2d.drawLine(x1, y1, x2, y2);
                       g2d.drawLine(x2, y1, x1, y2);
                    } else if (board[row][col] == NOUGHT) {
                       g2d.setColor(Color.BLUE);
                       g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
                    }
                }
            }

            // Print status-bar message
            if (currentState == PLAYING) {
                statusBar.setForeground(Color.BLACK);
                if (currentPlayer == CROSS) {
                   statusBar.setText("X's Turn");
                } else {
                   statusBar.setText("O's Turn");
                }
            } else if (currentState == DRAW) {
               statusBar.setForeground(Color.RED);
               statusBar.setText("It's a Draw! Click to play again.");
            } else if (currentState == CROSS_WON) {
               statusBar.setForeground(Color.RED);
               statusBar.setText("'X' Won! Click to play again.");
            } else if (currentState == NOUGHT_WON) {
               statusBar.setForeground(Color.RED);
               statusBar.setText("'O' Won! Click to play again.");
           }
        }
     }
}
