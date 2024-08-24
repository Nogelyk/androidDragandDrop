package com.example.draganddropimage;

import static java.lang.Math.abs;

import android.app.Application;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.transition.Scene;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ImageView[] pictures = new ImageView[4];
    private ImageView[] spaces = new ImageView[4];
    private Rect[] rects = new Rect[4];
    private MyCanvas myCanvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        myCanvas = findViewById(R.id.myCanvas);



        pictures[0] = (ImageView) findViewById(R.id.imageView1);
        pictures[1] = (ImageView) findViewById(R.id.imageView2);
        pictures[2] = (ImageView) findViewById(R.id.imageView3);
        pictures[3] = (ImageView) findViewById(R.id.imageView4);

        spaces[0] = (ImageView) findViewById(R.id.imageView10);
        spaces[1] = (ImageView) findViewById(R.id.imageView11);
        spaces[2] = (ImageView) findViewById(R.id.imageView12);
        spaces[3] = (ImageView) findViewById(R.id.imageView13);

        spaces[3].getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        rects[0] = new Rect(0,(int)spaces[0].getY(),getResources().getDisplayMetrics().widthPixels,(int)(spaces[0].getY()+spaces[0].getHeight()));
                        rects[1] = new Rect(0,(int)spaces[1].getY(),getResources().getDisplayMetrics().widthPixels,(int)(spaces[1].getY()+spaces[0].getHeight()));
                        rects[2] = new Rect(0,(int)spaces[2].getY(),getResources().getDisplayMetrics().widthPixels,(int)(spaces[2].getY()+spaces[0].getHeight()));
                        rects[3] = new Rect(0,(int)spaces[3].getY(),getResources().getDisplayMetrics().widthPixels,(int)(spaces[3].getY()+spaces[0].getHeight()));

                        for(Rect r:rects){
                            myCanvas.setRect(r);
                        }
                        spaces[3].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }

        );
    }

    private float x, y, orig_x, orig_y;
    private float dx, dy;
    private ImageView selected = null;
    private ImageView swapImage = null;
    private boolean invalidSelection = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if(event.getAction() == MotionEvent.ACTION_DOWN && selected == null){
            invalidSelection = true;
            x = event.getX();
            y = event.getY();


            for(ImageView picture: pictures){
                if(x >= picture.getX() && x<= picture.getX()+picture.getWidth() && y >= picture.getY() && y <= picture.getY()+picture.getHeight()){
                    selected = picture;
                    orig_x = selected.getX();
                    orig_y = selected.getY();
                    for(Rect r:rects){
                        if(r.contains((int)x,(int)y)){
                            myCanvas.setActiveRect(r);
                        }
                    }
                    break;
                }
            }
        }





        if(event.getAction() == MotionEvent.ACTION_MOVE && selected != null){
            dx = event.getX() - x;
            dy = event.getY() - y;

            outerLoop:
            for(Rect r:rects){
                for(ImageView space:spaces){
                    if((r.contains((int)x,(int)y) && abs(selected.getX()-space.getX()) <= (float)selected.getWidth()/2 && abs(selected.getY()-space.getY()) <= (float)selected.getHeight()/2) ||
                            (r.contains((int)x,(int)y) && abs(selected.getX()-space.getX()) <= (float)selected.getWidth()*3/4 && abs(selected.getY()-space.getY()) <= (float)selected.getHeight()*1/4) ||
                            (r.contains((int)x,(int)y) && abs(selected.getX()-space.getX()) <= (float)selected.getWidth()*1/4 && abs(selected.getY()-space.getY()) <= (float)selected.getHeight()*3/4)){
                        myCanvas.setActiveRect(r);
                        break outerLoop;
                    }
                    else{
                        myCanvas.dropRect();
                    }
                }
            }

            selected.setX(selected.getX() + dx);
            if(selected.getX() < 0) {
                selected.setX(0);
            }
            if(selected.getX()+selected.getWidth() > getResources().getDisplayMetrics().widthPixels){
                selected.setX(getResources().getDisplayMetrics().widthPixels - selected.getWidth());
            }
            selected.setY(selected.getY() + dy);
            if(selected.getY() < 0) {
                selected.setY(0);
            }
            if(selected.getY() + selected.getHeight() > getResources().getDisplayMetrics().heightPixels){
                selected.setY(getResources().getDisplayMetrics().heightPixels - selected.getHeight());
            }

            x = event.getX();
            y = event.getY();
        }
        if (selected != null) {
            // Bring the selected ImageView to the front
            selected.bringToFront();
            //selected.invalidate(); // Request layout redraw
        }
        if(event.getAction() == MotionEvent.ACTION_UP && selected != null){

            for(ImageView space:spaces){
                if((abs(selected.getX()-space.getX()) <= (float)selected.getWidth()/2 && abs(selected.getY()-space.getY()) <= (float)selected.getHeight()/2) ||
                        (abs(selected.getX()-space.getX()) <= (float)selected.getWidth()*3/4 && abs(selected.getY()-space.getY()) <= (float)selected.getHeight()*1/4) ||
                        (abs(selected.getX()-space.getX()) <= (float)selected.getWidth()*1/4 && abs(selected.getY()-space.getY()) <= (float)selected.getHeight()*3/4)){
                    for(ImageView old:pictures){
                        if(space.getX() == old.getX() && space.getY() == old.getY() && old != selected){
                            swapImage = old;
                            break;
                        }
                    }
                    selected.setX(space.getX());
                    selected.setY(space.getY());
                    invalidSelection = false;
                }
            }
            if(invalidSelection){
                selected.setX(orig_x);
                selected.setY(orig_y);
            }
            else{
                swapImage.setX(orig_x);
                swapImage.setY(orig_y);
            }






            if (selected != null) {
                myCanvas.dropRect();
                ViewGroup parent = (ViewGroup) selected.getParent();
                if (parent != null) {
                    parent.removeView(selected);
                    parent.addView(selected, 1); // Add the view back at index 0, which is the back
                }
                selected = null;

            }
        }

        return super.onTouchEvent(event);
    }

}