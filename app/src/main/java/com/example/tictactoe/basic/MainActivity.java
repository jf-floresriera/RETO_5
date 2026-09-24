package com.example.tictactoe.basic;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BoardGame mGame;
    private BoardView mBoardView;
    private TextView mTvStatus;
    private Button mBtnRestart;
    private boolean mGameOver = false;
    private char mCurrentPlayer = BoardGame.HUMAN_PLAYER;

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

    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputerMediaPlayer;

    @SuppressLint("ClickableViewAccessibility")
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

        mBoardView = findViewById(R.id.board);
        mBoardView.setGame(mGame);

        mBoardView.setOnTouchListener(mTouchListener);

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

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sword);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.swish);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHumanMediaPlayer != null) {
            mHumanMediaPlayer.release();
            mHumanMediaPlayer = null;
        }
        if (mComputerMediaPlayer != null) {
            mComputerMediaPlayer.release();
            mComputerMediaPlayer = null;
        }
    }

    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Determine which cell was touched
                int col = (int) event.getX() / mBoardView.getBoardCellWidth();
                int row = (int) event.getY() / mBoardView.getBoardCellHeight();
                int pos = row * 3 + col;

                if (pos >= 0 && pos < BoardGame.BOARD_SIZE && !mGameOver) {
                    if (mGame.getBoardOccupant(pos) == BoardGame.EMPTY_SPACE) {
                        handleHumanMove(pos);
                    }
                }
            }
            return false;
        }
    };

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
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
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

                    mScoreHuman = 0;
                    mScoreComputer = 0;
                    mScoreTies = 0;
                    updateScoreBoard();
                    startNewGame();
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
        mBoardView.invalidate(); // Redraw the board
        mGameOver = false;
        mCurrentPlayer = BoardGame.HUMAN_PLAYER;

        if (mGame.getGameMode() == BoardGame.GameMode.SinglePlayer) {
            mTvStatus.setText(R.string.turn_human);
        } else {
            mTvStatus.setText(R.string.turn_human);
        }
    }

    private void handleHumanMove(int location) {
        if (mGame.getGameMode() == BoardGame.GameMode.SinglePlayer) {
            // LÓGICA DE UN JUGADOR (vs IA)
            if (setMove(BoardGame.HUMAN_PLAYER, location)) {
                if (mHumanMediaPlayer != null) mHumanMediaPlayer.start();

                int winner = mGame.checkForWinner();
                if (winner != 0) {
                    endGame(winner);
                    return;
                }

                mTvStatus.setText(R.string.turn_computer);

                // Delay the computer's move
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (mGameOver) return;
                    int computerMove = mGame.getComputerMove();
                    if (computerMove != -1 && setMove(BoardGame.COMPUTER_PLAYER, computerMove)) {
                        if (mComputerMediaPlayer != null) mComputerMediaPlayer.start();
                    }

                    int compWinner = mGame.checkForWinner();
                    if (compWinner != 0) {
                        endGame(compWinner);
                    } else {
                        mTvStatus.setText(R.string.turn_human);
                    }
                }, 500); // Wait 500ms before making the computer move
            }
        } else {
            // LÓGICA DE DOS JUGADORES (Local)
            if (setMove(mCurrentPlayer, location)) {
                if (mCurrentPlayer == BoardGame.HUMAN_PLAYER) {
                    if (mHumanMediaPlayer != null) mHumanMediaPlayer.start();
                    mCurrentPlayer = BoardGame.COMPUTER_PLAYER;
                    mTvStatus.setText(R.string.turn_human_2);
                } else {
                    if (mComputerMediaPlayer != null) mComputerMediaPlayer.start();
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

    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate(); // Redraw the board
            return true;
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
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