package uk.ac.wlv.noughtsandcrosses;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static final int NOUGHT = 0;
    public static final int CROSS = 1;
    public static final int EMPTY = 2;
    int[][] gameBoard;
    int i, j, k = 0;
    Button[][] squares;
    TextView textMessage;
    ComputerPlayer mComputerPlayer;
    // Called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setBoard();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add("New Game");
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        setBoard();
        return true;
    }
    // Set up the game board.
    private void setBoard() {
        mComputerPlayer = new ComputerPlayer();
        squares = new Button[4][4];
        gameBoard = new int[4][4];
        textMessage = findViewById(R.id.dialogue);
        squares[1][3] = findViewById(R.id.one);
        squares[1][2] = findViewById(R.id.two);
        squares[1][1] = findViewById(R.id.three);
        squares[2][3] = findViewById(R.id.four);
        squares[2][2] = findViewById(R.id.five);
        squares[2][1] = findViewById(R.id.six);
        squares[3][3] = findViewById(R.id.seven);
        squares[3][2] = findViewById(R.id.eight);
        squares[3][1] = findViewById(R.id.nine);
        for (i = 1; i <= 3; i++) {
            for (j = 1; j <= 3; j++)
                gameBoard[i][j] = EMPTY;  //clear the board to empty
        }
        textMessage.setText("Click a square to start.");
// add the click listeners for each button
        for (i = 1; i <= 3; i++) {
            for (j = 1; j <= 3; j++) {
                squares[i][j].setOnClickListener(new MyClickListener(i,
                        j));
                squares[i][j].setText(" ");
                squares[i][j].setEnabled(true);
            }
        }
    }
    // check the board to see if someone has won
    private boolean checkBoard() {
        boolean gameOver = false;
//Check for a line of noughts
        if ((gameBoard[1][1] == NOUGHT && gameBoard[2][2] == NOUGHT &&
                gameBoard[3][3] == NOUGHT)
                || (gameBoard[1][3] == NOUGHT && gameBoard[2][2] == NOUGHT
                && gameBoard[3][1] == NOUGHT)
                || (gameBoard[1][2] == NOUGHT && gameBoard[2][2] == NOUGHT
                && gameBoard[3][2] == NOUGHT)
                || (gameBoard[1][3] == NOUGHT && gameBoard[2][3] == NOUGHT
                && gameBoard[3][3] == NOUGHT)
                || (gameBoard[1][1] == NOUGHT && gameBoard[1][2] == NOUGHT
                && gameBoard[1][3] == NOUGHT)
                || (gameBoard[2][1] == NOUGHT && gameBoard[2][2] == NOUGHT
                && gameBoard[2][3] == NOUGHT)
                || (gameBoard[3][1] == NOUGHT && gameBoard[3][2] == NOUGHT
                && gameBoard[3][3] == NOUGHT)
                || (gameBoard[1][1] == NOUGHT && gameBoard[2][1] == NOUGHT
                && gameBoard[3][1] == NOUGHT)) {
            textMessage.setText("You win!");
            gameOver = true;
        }
// Check for a line of crosses
        else if ((gameBoard[1][1] == CROSS && gameBoard[2][2] == CROSS &&
                gameBoard[3][3] == CROSS)
                || (gameBoard[1][3] == CROSS && gameBoard[2][2] == CROSS
                && gameBoard[3][1] == CROSS)
                || (gameBoard[1][2] == CROSS && gameBoard[2][2] == CROSS
                && gameBoard[3][2] == CROSS)
                || (gameBoard[1][3] == CROSS && gameBoard[2][3] == CROSS
                && gameBoard[3][3] == CROSS)
                || (gameBoard[1][1] == CROSS && gameBoard[1][2] == CROSS
                && gameBoard[1][3] == CROSS)
                || (gameBoard[2][1] == CROSS && gameBoard[2][2] == CROSS
                && gameBoard[2][3] == CROSS)
                || (gameBoard[3][1] == CROSS && gameBoard[3][2] == CROSS
                && gameBoard[3][3] == CROSS)
                || (gameBoard[1][1] == CROSS && gameBoard[2][1] == CROSS
                && gameBoard[3][1] == CROSS)) {
            textMessage.setText("You lose!");
            gameOver = true;
        } else {
            boolean empty = false;
            for (i = 1; i <= 3; i++) {
                for (j = 1; j <= 3; j++) {
                    if (gameBoard[i][j] == EMPTY) {
                        empty = true;
                        break;
                    }
                }
            }
            if (!empty) {
                gameOver = true;
                textMessage.setText("It's a draw!");
            }
        }
        return gameOver;
    }
    class MyClickListener implements View.OnClickListener {
        int x;
        int y;
        public MyClickListener(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public void onClick(View view) {
            if (squares[x][y].isEnabled()) {
                squares[x][y].setEnabled(false);
                squares[x][y].setText("O");
                gameBoard[x][y] = 0;
                textMessage.setText("");
                if (!checkBoard()) {
                    mComputerPlayer.takeTurn();
                }
            }
        }
    }
    private class ComputerPlayer {
        public void takeTurn() {
            if (blockPlayerWin()) {
                return;
            } else if (canComputerWin()) {
                return;
            }else {
// Otherwise just pick a random square
                Random rand = new Random();
                int a = 1 + rand.nextInt(3);
                int b = 1 + rand.nextInt(3);
                while (gameBoard[a][b] != EMPTY) {
                    a = 1 + rand.nextInt(3);
                    b = 1 + rand.nextInt(3);
                }
                markSquare(a, b);
            }
        }
// Checking every square for a possible human player win and block it
        private boolean blockPlayerWin() {
            if (gameBoard[1][1] == EMPTY &&
                    ((gameBoard[1][2] == NOUGHT && gameBoard[1][3] ==
                            NOUGHT) ||
                            (gameBoard[2][2] == NOUGHT && gameBoard[3][3]
                                    == NOUGHT) ||
                            (gameBoard[2][1] == NOUGHT && gameBoard[3][1]
                                    == NOUGHT))) {
                markSquare(1, 1);
            } else if (gameBoard[1][2] == EMPTY &&
                    ((gameBoard[2][2] == NOUGHT && gameBoard[3][2] ==
                            NOUGHT) ||
                            (gameBoard[1][1] == NOUGHT && gameBoard[1][3]
                                    == NOUGHT))) {
                markSquare(1, 2);
            } else if (gameBoard[1][3] == EMPTY &&
                    ((gameBoard[1][1] == NOUGHT && gameBoard[1][2] ==
                            NOUGHT) ||
                            (gameBoard[3][1] == NOUGHT && gameBoard[2][2]
                                    == NOUGHT) ||
                            (gameBoard[2][3] == NOUGHT && gameBoard[3][3]
                                    == NOUGHT))) {
                markSquare(1, 3);
            } else
                return false;
            return true;
        }

        private boolean canComputerWin() {
            if(gameBoard[1][1]==EMPTY && gameBoard[1][2] == CROSS && gameBoard[1][3] == CROSS) {
                markSquare(1,1);
                return true;
            } else if(gameBoard[1][1]==EMPTY && gameBoard[2][2] == CROSS && gameBoard[3][3] == CROSS) {
                markSquare(1,1);
                return true;
            } else {
                return false;
            }
        }

        private void markSquare(int x, int y) {
            squares[x][y].setEnabled(false);
            squares[x][y].setText("X");
            gameBoard[x][y] = CROSS;
            checkBoard();
        }
    }
}