package com.example.tictactoe.basic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private BoardGame mGame;
    private Button[] mBoardButtons;
    private TextView mTvStatus;
    private Button mBtnRestart;
    private boolean mGameOver = false;
    private char mCurrentPlayer = BoardGame.HUMAN_PLAYER; // Used for 2-player mode

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;
    static final int DIALOG_MODE_ID = 3;

    private TextView mTvScoreHuman;
    private TextView mTvScoreComputer;
    private TextView mTvScoreTies;

    private int mScoreHuman = 0;
    private int mScoreComputer = 0;
    private int mScoreTies = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGame = new BoardGame();
        mTvStatus = findViewById(R.id.tv_status);
        mBtnRestart = findViewById(R.id.btn_restart);

        mTvScoreHuman = findViewById(R.id.tv_score_human);
        mTvScoreComputer = findViewById(R.id.tv_score_computer);
        mTvScoreTies = findViewById(R.id.tv_score_ties);
        updateScoreBoard();

        mBoardButtons = new Button[BoardGame.BOARD_SIZE];
        mBoardButtons[0] = findViewById(R.id.btn_0);
        mBoardButtons[1] = findViewById(R.id.btn_1);
        mBoardButtons[2] = findViewById(R.id.btn_2);
        mBoardButtons[3] = findViewById(R.id.btn_3);
        mBoardButtons[4] = findViewById(R.id.btn_4);
        mBoardButtons[5] = findViewById(R.id.btn_5);
        mBoardButtons[6] = findViewById(R.id.btn_6);
        mBoardButtons[7] = findViewById(R.id.btn_7);
        mBoardButtons[8] = findViewById(R.id.btn_8);

        for (int i = 0; i < mBoardButtons.length; i++) {
            final int index = i;
            mBoardButtons[i].setOnClickListener(v -> handleHumanMove(index));
        }

        mBtnRestart.setOnClickListener(v -> startNewGame());

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.new_game) {
                startNewGame();
                return true;
            } else if (itemId == R.id.game_mode) {
                showDialog(DIALOG_MODE_ID);
                return true;
            } else if (itemId == R.id.ai_difficulty) {
                if (mGame.getGameMode() == BoardGame.GameMode.TwoPlayer) {
                    Toast.makeText(this, "Dificultad sólo disponible en 1 Jugador", Toast.LENGTH_SHORT).show();
                } else {
                    showDialog(DIALOG_DIFFICULTY_ID);
                }
                return true;
            } else if (itemId == R.id.about) {
                showDialog(DIALOG_ABOUT_ID);
                return true;
            }
            return false;
        });

        startNewGame();
    }

    // Ya no usamos el menú de arriba en el ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Retornamos falso para que no muestre el menú superior, ya que tenemos el de abajo.
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
            case DIALOG_MODE_ID:
                builder.setTitle(R.string.mode_choose);
                final CharSequence[] modes = {
                        getResources().getString(R.string.mode_1_player),
                        getResources().getString(R.string.mode_2_players)};

                int selectedMode = mGame.getGameMode() == BoardGame.GameMode.SinglePlayer ? 0 : 1;

                builder.setSingleChoiceItems(modes, selectedMode, (d, item) -> {
                    d.dismiss();
                    if (item == 0) mGame.setGameMode(BoardGame.GameMode.SinglePlayer);
                    else mGame.setGameMode(BoardGame.GameMode.TwoPlayer);
                    Toast.makeText(getApplicationContext(), modes[item], Toast.LENGTH_SHORT).show();
                    
                    // Resetear el marcador cuando se cambia de modo
                    mScoreHuman = 0;
                    mScoreComputer = 0;
                    mScoreTies = 0;
                    updateScoreBoard();
                    
                    startNewGame(); // Start new game when mode changes
                });
                dialog = builder.create();
                break;

            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                int selected = 2; // Default to expert
                BoardGame.DifficultyLevel current = mGame.getDifficultyLevel();
                if (current == BoardGame.DifficultyLevel.Easy) selected = 0;
                else if (current == BoardGame.DifficultyLevel.Harder) selected = 1;

                builder.setSingleChoiceItems(levels, selected, (d, item) -> {
                    d.dismiss();
                    if (item == 0) mGame.setDifficultyLevel(BoardGame.DifficultyLevel.Easy);
                    else if (item == 1) mGame.setDifficultyLevel(BoardGame.DifficultyLevel.Harder);
                    else mGame.setDifficultyLevel(BoardGame.DifficultyLevel.Expert);
                    Toast.makeText(getApplicationContext(), levels[item], Toast.LENGTH_SHORT).show();
                });
                dialog = builder.create();
                break;

            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, (d, which) -> MainActivity.this.finish())
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;

            case DIALOG_ABOUT_ID:
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    private void startNewGame() {
        mGame.clearBoard();
        mGameOver = false;
        mCurrentPlayer = BoardGame.HUMAN_PLAYER;

        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setTextColor(Color.BLACK);
        }

        if (mGame.getGameMode() == BoardGame.GameMode.SinglePlayer) {
            mTvStatus.setText(R.string.turn_human);
        } else {
            mTvStatus.setText(R.string.turn_human); // Para el modo de 2 jugadores, empieza X
        }
    }

    private void handleHumanMove(int location) {
        if (mGameOver) return;

        if (mGame.getGameMode() == BoardGame.GameMode.SinglePlayer) {
            // LÓGICA DE UN JUGADOR (vs IA)
            if (mGame.setMove(BoardGame.HUMAN_PLAYER, location)) {
                setButton(location, BoardGame.HUMAN_PLAYER, "#1E88E5");

                int winner = mGame.checkForWinner();
                if (winner != 0) {
                    endGame(winner);
                    return;
                }

                mTvStatus.setText(R.string.turn_computer);
                disableAllBoardButtons();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (mGameOver) return;
                    int computerMove = mGame.getComputerMove();
                    if (computerMove != -1 && mGame.setMove(BoardGame.COMPUTER_PLAYER, computerMove)) {
                        setButton(computerMove, BoardGame.COMPUTER_PLAYER, "#E53935");
                    }

                    int compWinner = mGame.checkForWinner();
                    if (compWinner != 0) {
                        endGame(compWinner);
                    } else {
                        mTvStatus.setText(R.string.turn_human);
                        enableAvailableBoardButtons();
                    }
                }, 500);
            }
        } else {
            // LÓGICA DE DOS JUGADORES (Local)
            if (mGame.setMove(mCurrentPlayer, location)) {
                if (mCurrentPlayer == BoardGame.HUMAN_PLAYER) {
                    setButton(location, mCurrentPlayer, "#1E88E5");
                    mCurrentPlayer = BoardGame.COMPUTER_PLAYER;
                    mTvStatus.setText(R.string.turn_human_2);
                } else {
                    setButton(location, mCurrentPlayer, "#E53935");
                    mCurrentPlayer = BoardGame.HUMAN_PLAYER;
                    mTvStatus.setText(R.string.turn_human);
                }

                int winner = mGame.checkForWinner();
                if (winner != 0) {
                    endGame(winner);
                }
            }
        }
    }

    private void setButton(int location, char player, String color) {
        mBoardButtons[location].setText(String.valueOf(player));
        mBoardButtons[location].setTextColor(Color.parseColor(color));
        mBoardButtons[location].setEnabled(false);
    }

    private void disableAllBoardButtons() {
        for (Button btn : mBoardButtons) {
            btn.setEnabled(false);
        }
    }

    private void enableAvailableBoardButtons() {
        for (int i = 0; i < mBoardButtons.length; i++) {
            if (mGame.getBoardOccupant(i) == BoardGame.EMPTY_SPACE) {
                mBoardButtons[i].setEnabled(true);
            }
        }
    }

    private void updateScoreBoard() {
        mTvScoreHuman.setText("Jugador 1: " + mScoreHuman);
        if (mGame.getGameMode() == BoardGame.GameMode.SinglePlayer) {
            mTvScoreComputer.setText("IA: " + mScoreComputer);
        } else {
            mTvScoreComputer.setText("Jugador 2: " + mScoreComputer);
        }
        mTvScoreTies.setText("Empates: " + mScoreTies);
    }

    private void endGame(int winnerCode) {
        mGameOver = true;
        disableAllBoardButtons();

        switch (winnerCode) {
            case 1:
                mTvStatus.setText(R.string.result_tie);
                mScoreTies++;
                break;
            case 2:
                mTvStatus.setText(R.string.result_human_win);
                mScoreHuman++;
                break;
            case 3:
                if (mGame.getGameMode() == BoardGame.GameMode.SinglePlayer) {
                    mTvStatus.setText(R.string.result_computer_win);
                } else {
                    mTvStatus.setText(R.string.result_human_2_win);
                }
                mScoreComputer++;
                break;
        }
        updateScoreBoard();
    }
}