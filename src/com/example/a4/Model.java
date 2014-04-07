/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery & Michael Terry
 */
package com.example.a4;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/*
 * Class the contains a list of fruit to display.
 * Follows MVC pattern, with methods to add observers,
 * and notify them when the fruit list changes.
 */
public class Model extends Observable {
    // List of fruit that we want to display
    private ArrayList<Fruit> shapes = new ArrayList<Fruit>();
    private Handler handler = new Handler();
    private Handler timeHandler = new Handler();
    private int interval = 1;
    
    public int score = 0;
    public int fails = 6;
    
    public int timeMS = 0;
    
    public boolean gameOver = false;

    // Constructor
    Model() {
        shapes.clear();
        
    }

    // Model methods
    // You may need to add more methods here, depending on required functionality.
    // For instance, this sample makes to effort to discard fruit from the list.
    public void add(Fruit s) {
        shapes.add(s);
        setChanged();
        notifyObservers();
    }

  /*  @Override
    public void notifyObservers(){
    	for (ModelListener v : views){
    		
    	}
    }*/
    
    public void remove(Fruit s) {
        shapes.remove(s);
        s = null;
    }

    public ArrayList<Fruit> getShapes() {
        return (ArrayList<Fruit>) shapes.clone();
    }

    // MVC methods
    // Basic MVC methods to bind view and model together.
    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    // a helper to make it easier to initialize all observers
    public void initObservers() {
        setChanged();
        notifyObservers();
    }

    @Override
    public synchronized void deleteObserver(Observer observer) {
        super.deleteObserver(observer);
        setChanged();
        notifyObservers();
    }

    @Override
    public synchronized void deleteObservers() {
        super.deleteObservers();
        setChanged();
        notifyObservers();
    }
    
    Runnable updateAll = new Runnable(){
		@Override
		public void run() {
			//System.out.println("RUNNABLE CALLED!!!");
			
			setChanged();
			notifyObservers();
			//timeMS = timeMS+interval;
        	handler.postDelayed(updateAll, interval);
		}
    };
    
    public void startFruits(){
    	updateAll.run();
    }
    
    public void stopFruits(){
    	handler.removeCallbacks(updateAll);
    }
    
    Runnable updateTimer = new Runnable(){
    	@Override
    	public void run(){
    		timeMS = timeMS +1;
    		timeHandler.postDelayed(updateTimer, 1000);
    	}
    };
    
    public void startTimer(){
    	updateTimer.run();
    }
    
    public void stopTimer(){
    	timeHandler.removeCallbacks(updateTimer);
    }
    
    public void restart(){
    	this.gameOver = false;
    	shapes.clear();
    	this.score = 0;
    	this.fails = 6;
    	this.timeMS = 0;
    	startFruits();
    	startTimer();
    }
    
}
