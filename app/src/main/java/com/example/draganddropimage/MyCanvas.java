package com.example.draganddropimage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class MyCanvas extends View {

    private Paint paint;
    private Rect[] rects = new Rect[4];
    private int i = 0;
    private Rect actRect;

    // Constructor for creating the view in code
    public MyCanvas(Context context) {
        super(context);
        this.setMinimumWidth(context.getResources().getDisplayMetrics().widthPixels);
        this.setMinimumHeight(context.getResources().getDisplayMetrics().heightPixels);
        init(null);
    }

    // Constructor for inflating the view from XML
    public MyCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    // Constructor for inflating the view from XML with style attributes
    public MyCanvas(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        paint = new Paint();
    }

    public void setRect(Rect rect) {
        this.rects[i] = rect;
        i += 1;
        invalidate(); // Trigger a redraw to update the view
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if(actRect != null){
            paint.setColor(Color.YELLOW);
            paint.setAlpha(200);
            canvas.drawRect(actRect,paint);
        }
        for(Rect rect:rects) {
            if (rect != null && rect != actRect) {
                paint.setColor(Color.LTGRAY);
                paint.setAlpha(180);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
            }

        }



    }

    public void setActiveRect(Rect r) {
        this.actRect = r;
        invalidate();

    }

    public void dropRect() {
        this.actRect = null;
        invalidate();
    }
}
