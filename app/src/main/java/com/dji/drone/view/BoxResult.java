package com.dji.drone.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class BoxResult extends View {
    private Paint paintBox = new Paint();
    private Paint paintStroke = new Paint();
    private boolean showBox = false;
    private int[][] boxResult;

    public BoxResult(Context context) {
        super(context);
        init();
    }

    public BoxResult(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoxResult(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        boxResult = new int[][]{{1,0,1},{1,1,1},{0,1,0}};
        paintBox.setColor(Color.RED);
        paintBox.setStyle(Paint.Style.FILL);
        paintBox.setAlpha(10*255/100);

        paintStroke.setColor(Color.BLUE);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setAlpha(30*255/100);
        paintStroke.setStrokeWidth(5);

    }
    public void setBox(int[][] boxResult){
        this.boxResult = boxResult;
    }

    public void setShowBox(boolean showBox) {
        this.showBox = showBox;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int heightBox = getHeight() / boxResult.length;
        int widthBox = getWidth() / boxResult.length;

        for (int i = 0; i < boxResult.length; i++) {
            for (int j = 0; j < boxResult.length; j++) {
                if(boxResult[i][j] == 1){
                    int left = j * widthBox;
                    int top = i * heightBox;
                    int right = (j+1) * widthBox;
                    int bottom = (i+1) * heightBox;
                    canvas.drawRect(left, top, right, bottom, paintBox);
                    canvas.drawRect(left, top, right, bottom, paintStroke);
                }
            }
        }
    }

    public void setWH(int w, int h){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = w;
        params.height = h;
        setLayoutParams(params);
        invalidate();
    }
}