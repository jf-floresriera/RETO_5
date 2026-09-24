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

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private BoardGame mGame;
    private Button[] mBoardButtons;
    private TextView mTvStatus;
    private Button mBtnRestart;
    private boolean mGameOver = false;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGame = new BoardGame();
        mTvStatus = findViewById(R.id.tv_status);
        mBtnRestart = findViewById(R.id.btn_restart);

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

        startNewGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.new_game) {
            startNewGame();
            return true;
        } else if (itemId == R.id.ai_difficulty) {
            showDialog(DIALOG_DIFFICULTY_ID);
            return true;
        } else if (itemId == R.id.quit) {
            showDialog(DIALOG_QUIT_ID);
            return true;
        } else if (itemId == R.id.about) {
            showDialog(DIALOG_ABOUT_ID);
            return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
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

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog

                                if (item == 0) mGame.setDifficultyLevel(BoardGame.DifficultyLevel.Easy);
                                else if (item == 1) mGame.setDifficultyLevel(BoardGame.DifficultyLevel.Harder);
                                else mGame.setDifficultyLevel(BoardGame.DifficultyLevel.Expert);

                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();
                break;

            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
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

        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setTextColor(Color.BLACK);
        }

        mTvStatus.setText(R.string.turn_human);
    }

    private void handleHumanMove(int location) {
        if (mGameOver) return;

        if (mGame.setMove(BoardGame.HUMAN_PLAYER, location)) {
            mBoardButtons[location].setText(String.valueOf(BoardGame.HUMAN_PLAYER));
            mBoardButtons[location].setTextColor(Color.parseColor("#1E88E5"));
            mBoardButtons[location].setEnabled(false);

            int winner = mGame.checkForWinner();
            if (winner != 0) {
                endGame(winner);
                return;
            }

            // Turno de la IA
            mTvStatus.setText(R.string.turn_computer);
            disableAllBoardButtons();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (mGameOver) return;
                int computerMove = mGame.getComputerMove();
                if (computerMove != -1 && mGame.setMove(BoardGame.COMPUTER_PLAYER, computerMove)) {
                    mBoardButtons[computerMove].setText(String.valueOf(BoardGame.COMPUTER_PLAYER));
                    mBoardButtons[computerMove].setTextColor(Color.parseColor("#E53935"));
                    mBoardButtons[computerMove].setEnabled(false);
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

    private void endGame(int winnerCode) {
        mGameOver = true;
        disableAllBoardButtons();

        switch (winnerCode) {
            case 1:
                mTvStatus.setText(R.string.result_tie);
                break;
            case 2:
                mTvStatus.setText(R.string.result_human_win);
                break;
            case 3:
                mTvStatus.setText(R.string.result_computer_win);
                break;
        }
    }
}