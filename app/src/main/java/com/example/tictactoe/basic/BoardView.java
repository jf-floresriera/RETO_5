package com.example.tictactoe.basic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;

public class BoardView extends View {
    // Width of the board grid lines
    public static final int GRID_WIDTH = 6;

    private Bitmap mHumanBitmap;
    private Bitmap mComputerBitmap;
    private Paint mPaint;
    private BoardGame mGame;

    public BoardView(Context context) {
        super(context);
        initialize();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public void initialize() {
        // En lugar de usar BitmapFactory (que no soporta Vectores XML de forma nativa),
        // convertimos nuestros vectores vectoriales (x_img.xml y o_img.xml) a Bitmaps para dibujarlos.
        mHumanBitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.x_img);
        mComputerBitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.o_img);
        
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), 
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void setGame(BoardGame game) {
        mGame = game;
    }

    public int getBoardCellWidth() {
        return getWidth() / 3;
    }

    public int getBoardCellHeight() {
        return getHeight() / 3;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Determine the width and height of the View
        int boardWidth = getWidth();
        int boardHeight = getHeight();
        
        // Make thick, light gray lines
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(GRID_WIDTH);
        
        int cellWidth = boardWidth / 3;
        int cellHeight = boardHeight / 3;
        
        // Draw the two vertical board lines
        canvas.drawLine(cellWidth, 0, cellWidth, boardHeight, mPaint);
        canvas.drawLine(cellWidth * 2, 0, cellWidth * 2, boardHeight, mPaint);
        
        // Draw the two horizontal board lines
        canvas.drawLine(0, cellHeight, boardWidth, cellHeight, mPaint);
        canvas.drawLine(0, cellHeight * 2, boardWidth, cellHeight * 2, mPaint);
        
        // Draw all the X and O images
        for (int i = 0; i < BoardGame.BOARD_SIZE; i++) {
            int col = i % 3;
            int row = i / 3;
            
            // Define the boundaries of a destination rectangle for the image
            // Agregamos un poco de padding (20px) para que la imagen no toque las líneas
            int left = col * cellWidth + 20;
            int top = row * cellHeight + 20;
            int right = left + cellWidth - 40;
            int bottom = top + cellHeight - 40;
            
            if (mGame != null && mGame.getBoardOccupant(i) == BoardGame.HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap,
                        null, // src
                        new Rect(left, top, right, bottom), // dest
                        null);
            }
            else if (mGame != null && mGame.getBoardOccupant(i) == BoardGame.COMPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap,
                        null, // src
                        new Rect(left, top, right, bottom), // dest
                        null);
            }
        }
    }
}