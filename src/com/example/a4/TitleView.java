/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery & Michael Terry
 */
package com.example.a4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.TextView;
import com.example.a4complete.R;

import java.util.Observable;
import java.util.Observer;

/*
 * View to display the Title, and Score
 * Score currently just increments every time we get an update
 * from the model (i.e. a new fruit is added).
 */
public class TitleView extends TextView implements Observer {
    private int count = 0;
    public int score = 0;
    public int fail = 6;
    public Model vModel;

    // Constructor requires model reference
    public TitleView(Context context, Model model) {
        super(context);

        // set width, height of this view
        this.setHeight(100);
        this.setWidth(MainActivity.displaySize.x);

        // register with model so that we get updates
        model.addObserver(this);
        vModel = model;
        model.startTimer();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

      /*  if (vModel.restart){
        	System.out.println("TIMER RESTARTTED");
        	vModel.startTimer();
        }*/
        setBackgroundColor(Color.BLACK);
        setTextSize(16);
        int min = 0;
        int sec = 0;
        
        sec = (int) vModel.timeMS;
        min = (int) sec/60;
        sec = sec-(60*min);
        
        
        if (vModel.fails <0){
        	if (sec <= 9){
        		setText(getResources().getString(R.string.app_title) + "\n" + "Score: " + vModel.score + "\n" + "Lives Remaining: 0" + "                    " + "Time: " +min+ ":0" +sec);
        	}
        	else
        		setText(getResources().getString(R.string.app_title) + "\n" + "Score: " + vModel.score + "\n" + "Lives Remaining: 0" + "                    " + "Time: " +min+ ":" +sec);
        }
        else{
        	if (sec <=9)
        		setText(getResources().getString(R.string.app_title) + "\n" + "Score: " + vModel.score + "\n" + "Lives Remaining: " + vModel.fails + "                    " + "Time: " +min+ ":0" +sec);
        	else
        		setText(getResources().getString(R.string.app_title) + "\n" + "Score: " + vModel.score + "\n" + "Lives Remaining: " + vModel.fails + "                    " + "Time: " +min+ ":" +sec);
        }
    }

    // Update from model
    // ONLY useful for testing that the view notifications work
    @Override
    public void update(Observable observable, Object data) {
        if (vModel.fails < 0){
        	vModel.stopTimer();
        }
        invalidate();
    }
}
