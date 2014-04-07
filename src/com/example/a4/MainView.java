/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery & Michael Terry
 */
package com.example.a4;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

/*
 * View of the main game area.
 * Displays pieces of fruit, and allows players to slice them.
 */
public class MainView extends View implements Observer {
    private final Model model;
    private final MouseDrag drag = new MouseDrag();
    public int heightMax = 600;
    public int widthMax = 500;
    
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    

    // Constructor
    MainView(Context context, Model m) {
        super(context);

        // register this view with the model
        model = m;
        model.addObserver(this);

        //create fruits
        createFruits();

        // add controller
        // capture touch movement, and determine if we intersect a shape
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Log.d(getResources().getString(R.string.app_name), "Touch down");
                        drag.start(event.getX(), event.getY());
                        break;

                    case MotionEvent.ACTION_UP:
                        // Log.d(getResources().getString(R.string.app_name), "Touch release");
                        drag.stop(event.getX(), event.getY());

                        if (!model.gameOver){
                        	// find intersected shapes
                            Iterator<Fruit> i = model.getShapes().iterator();
                            while(i.hasNext()) {
                                Fruit s = i.next();
                                if (s.intersects(drag.getStart(), drag.getEnd())) {
                                	model.score = model.score + 1;
                                    try {
                                        Fruit[] newFruits = s.split(drag.getStart(), drag.getEnd());
                                        
                                        for (Fruit f : newFruits){
                                        	model.add(f);
                                        }

                                    } catch (Exception ex) {
                                        Log.e("fruit_ninja", "Error: " + ex.getMessage());
                                    }
                                } else {
                                    s.setFillColor(Color.BLUE);
                                }
                                invalidate();
                            }
                        }
                        
                        else{
                        	PointF p1 = drag.getStart();
                        	PointF p2 = drag.getEnd();
                        	
                        	RectF tryAgain = new RectF();
                        	//canvas.drawRect(left, top, right, bottom, paint);
                        	//canvas.drawRect(widthMax/2-(widthMax/4),  heightMax/2-(heightMax/4), (widthMax/2-(widthMax/4))+240, (heightMax/2-(heightMax/4))+75, paint);
                        	tryAgain.left = widthMax/2-(widthMax/4);
                        	tryAgain.top = heightMax/2-(heightMax/4);
                        	tryAgain.right = (widthMax/2-(widthMax/4))+240;
                        	tryAgain.bottom = (heightMax/2-(heightMax/4))+75;
                        	
                        	if (tryAgain.contains(p1.x, p1.y) && tryAgain.contains(p2.x,p2.y)){
                        		model.restart();
                        	}
                        	
                        }
                        break;
                }
                return true;
            }
        }); 
    }
    
    
    
    private void createFruits(){
    	int max = widthMax/2;
    	float x = (float) (Math.random() * max);
    	
    	//to add fruits appearing from the right side 
    	if (Math.random() >= 0.5){
        	x = x + max;
        }
    	
    	/*if (x <= MainActivity.displaySize.x/2 + 75 
    			&& x >= MainActivity.displaySize.x/2 -75)
    		x = 0;*/
    	
    	float y = 600;
    	Fruit f = new Fruit();
    	if (x < 0){
    		x = 0;
    		f.start = 0;
    	}
    	else if (x >=0 && x < 200){
    		f.start = 0;
    	}
    	else if (x >= 200 && x<= 250){
    		x = 200;
    		f. start = 0;
    	}
    	else if (x > 250 && x <= 375){
    		x = 375;
    		f.start = 1;
    	}
    	else if (x > 375 && x <= 500){
    		f.start = 1;
    	}
    	else if (x > 500){
    		x = 500;
    		f.start = 1;
    	}

    	f.x = x;
    	f.y = y;
    	
    	Random generator = new Random();
    	int i = generator.nextInt(6);
    	if (i == 0)
    		f.fruitType = Fruit.Type.RED;
    	else if (i == 1)
    		f.fruitType = Fruit.Type.BLACK;
    	else if (i == 2)
    		f.fruitType = Fruit.Type.GREEN;
    	else if (i == 3)
    		f.fruitType = Fruit.Type.BLUE;
    	else if (i == 4)
    		f.fruitType = Fruit.Type.YELLOW;
    	else
    		f.fruitType = Fruit.Type.MAGENTA;
    	
    	model.add(f);
    	
    }

    // inner class to track mouse drag
    // a better solution *might* be to dynamically track touch movement
    // in the controller above
    class MouseDrag {
        private float startx, starty;
        private float endx, endy;

        protected PointF getStart() { return new PointF(startx, starty); }
        protected PointF getEnd() { return new PointF(endx, endy); }

        protected void start(float x, float y) {
            this.startx = x;
            this.starty = y;
        }

        protected void stop(float x, float y) {
            this.endx = x;
            this.endy = y;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

      /*  if (model.restart){
        	model.startFruits();
        }*/
        // draw background
        setBackgroundColor(Color.WHITE);

        // draw all pieces of fruit
        if (model.fails >= 0){
        	for (Fruit s : model.getShapes()) {
        		if (s.sliced == 1){
        			model.remove(s);
        		}
        		/*if (s.piece == 1){
        			s.y = s.y+3;
        		}*/
        		
        		//going up
            	if (s.direction == 1){
            		s.y = s.y-3;
            		// starting from the left
            		if (s.start == 0){
            			s.x = s.x+1;
            			if (s.x >= widthMax/2 || s.y <= 50){
            				s.direction = 0;
            			}
            		}
            		// starting from the right
            		else{
            			s.x = s.x-1;
            			if (s.x <= widthMax/2 || s.y <= 50){
            				s.direction = 0;
            			}
            		}
            	}
            	// going down
            	else{
            		s.y = s.y+3;
            		// starting from the left
            		if (s.start == 0){
            			s.x = s.x+1;
            		}
            		// started from the right
            		else{
            			s.x = s.x-1;
            		}
            	}
            	// going off screen
            	if (s.y > heightMax && s.piece == 0){
            		if (s.piece == 0){
            			model.fails = model.fails-1;
            		}
        			model.remove(s);
        		}
            	else
            		s.draw(canvas);
            		
            	/*float[] points = s.points;
            	for (int i = 0; i < points.length; i++){
            		if (points[i] != -1){
            			s.x = points[i];
            			s.y = points[i+1];
            			break;
            		}
            	}*/

            }
            if (Math.random() * 1000 > 985){
            	this.createFruits();
            }
        }
        else{
        	model.gameOver = true;
        	model.stopFruits();
        	for (Fruit s : model.getShapes()){
        		model.remove(s);
        	}
        }
        
        if (model.gameOver){
        	paint.setColor(Color.BLACK);
        	//canvas.drawRect(left, top, right, bottom, paint);
        	canvas.drawRect(widthMax/2-(widthMax/4),  heightMax/2-(heightMax/4), (widthMax/2-(widthMax/4))+240, (heightMax/2-(heightMax/4))+75, paint);
        	paint.setColor(Color.WHITE);
        	paint.setTextSize(40);
        	canvas.drawText("TRY AGAIN?", (widthMax/2-(widthMax/4)+10), (heightMax/2-(heightMax/4)+35), paint);
        	
        	
        }
        
    }

    @Override
    public void update(Observable observable, Object data) {
        invalidate();
    }
}
