/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery
 */
package com.example.a4;
import android.graphics.*;
import android.util.Log;

/**
 * Class that represents a Fruit. Can be split into two separate fruits.
 */
public class Fruit {
	public enum Type {
    	RED, BLACK, GREEN, BLUE, YELLOW, MAGENTA
    }
    private Path path = new Path();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Matrix transform = new Matrix();
    public float x;
    public float y;
    public float[] points;
    public int direction = 1; // 1 is up, 0 is down
    public int sliced = 0; // 0 is not sliced, 1 is sliced
    public int start = 0; // 0 if start on left, 1 if start on right
    public int piece = 0; // 0 if not sliced, 1 if left piece, 2 if right piece, 3 if top piece, 4 if bottom piece
    public Type fruitType = Type.RED;
    int distance = 60;

    /**
     * A fruit is represented as Path, typically populated 
     * by a series of points 
     */

	Fruit(){//(float[] points) {
        init();
      /*  this.points = points;
        this.path.reset();
        this.path.moveTo(points[0], points[1]);
        for (int i = 2; i < points.length; i += 2) {
        	this.x = points[i];
        	this.y = points[i+1];
            this.path.lineTo(points[i], points[i + 1]);
        }
        this.path.moveTo(points[0], points[1]);
        
        this.x = this.points[0];
        this.y = this.points[1];
        this.points[0] = -1;
        this.points[1] = -1;*/
    }

    Fruit(Region region) {
        init();
        this.path = region.getBoundaryPath();
    }

    Fruit(Path path) {
        init();
        this.path = path;
    }

    private void init() {
        this.paint.setColor(Color.BLUE);
        this.paint.setStrokeWidth(5);
    }

    /**
     * The color used to paint the interior of the Fruit.
     */
    public int getFillColor() { return paint.getColor(); }
    public void setFillColor(int color) { paint.setColor(color); }

    /**
     * The width of the outline stroke used when painting.
     */
    public double getOutlineWidth() { return paint.getStrokeWidth(); }
    public void setOutlineWidth(float newWidth) { paint.setStrokeWidth(newWidth); }

    /**
     * Concatenates transforms to the Fruit's affine transform
     */
    public void rotate(float theta) { transform.postRotate(theta); }
    public void scale(float x, float y) { transform.postScale(x, y); }
    public void translate(float tx, float ty) { transform.postTranslate(tx, ty); }

    /**
     * Returns the Fruit's affine transform that is used when painting
     */
    public Matrix getTransform() { return transform; }

    /**
     * The path used to describe the fruit shape.
     */
    public Path getTransformedPath() {
        Path originalPath = new Path(path);
        Path transformedPath = new Path();
        originalPath.transform(transform, transformedPath);	
        return transformedPath;
    }

    /**
     * Paints the Fruit to the screen using its current affine
     * transform and paint settings (fill, outline)
     */
    public void draw(Canvas canvas) {
    	if (this.fruitType == Type.RED){
    		paint.setColor(Color.RED);
    	}
    	else if (this.fruitType == Type.BLACK){
    		paint.setColor(Color.BLACK); 
    	}
    	else if (this.fruitType == Type.GREEN){
    		paint.setColor(Color.GREEN);
    	}
    	else if (this.fruitType == Type.YELLOW){
    		paint.setColor(Color.YELLOW);
    	}
    	else if (this.fruitType == Type.MAGENTA){
    		paint.setColor(Color.MAGENTA);
    	}
    	else{
    		paint.setColor(Color.BLUE);
    	}
    	
    	if (this.piece == 1)
    		canvas.drawRect(this.x, this.y, this.x+20, this.y+this.distance, paint);
    	else if (this.piece == 2)
    		canvas.drawRect(this.x, this.y, this.x+20, this.y+this.distance, paint);
    	else if (this.piece == 3)
    		canvas.drawRect(this.x, this.y, this.x+this.distance, this.y+20, paint);
    	else if (this.piece == 4)
    		canvas.drawRect(this.x, this.y, this.x+this.distance, this.y+20, paint);
    	else
    		canvas.drawRect(this.x, this.y, this.x+this.distance, this.y+this.distance, paint);
    	
        // TODO BEGIN CS349
        // tell the shape to draw itself using the matrix and paint parameters
        // TODO END CS349
    }

    /**
     * Tests whether the line represented by the two points intersects
     * this Fruit.
     */
    public boolean intersects(PointF p1, PointF p2) {
    	double xDiff = p2.x - p1.x;
    	double yDiff = p2.y - p1.y;
    	double x1 = p1.x;
    	double x2 = p2.x;
    	double y1 = p1.y;
    	double y2 = p2.y;
    	
    	if (this.piece >0  || (xDiff == 0 && yDiff == 0)){
    		return false;
    	}
    	if (xDiff < this.distance && yDiff < this.distance)
    		return false;
    	
    	if ((x1 >= (this.x + this.x+this.distance) && x2>= (this.x+this.x+this.distance)) || (x1 <= this.x && x2<= this.x) || (y1 >= (this.y + this.y+this.distance) && y2 >= (this.y + this.y+this.distance)) || (y1 <= this.y && y2 <= this.y)){
    		return false;
    	}
    	return true;
    }

    /**
     * Returns whether the given point is within the Fruit's shape.
     */
    public boolean contains(PointF p1) {
        Region region = new Region();
        boolean valid = region.setPath(getTransformedPath(), new Region());
        return valid && region.contains((int) p1.x, (int) p1.y);
    }

    /**
     * This method assumes that the line represented by the two points
     * intersects the fruit. If not, unpredictable results will occur.
     * Returns two new Fruits, split by the line represented by the
     * two points given.
     */
    public Fruit[] split(PointF p1, PointF p2) {    	
    	this.sliced = 1;
    	RectF topRect = null;
    	RectF bottomRect = null;
    	
    	if (topRect == null && bottomRect == null){
    		double xDiff = Math.abs(p2.x - p1.x);
        	double yDiff = Math.abs(p2.y - p1.y);
        	
    		Fruit f1 = new Fruit();
    		f1.x = this.x;
    		f1.y = this.y;
    		f1.fruitType = this.fruitType;
    		
    		Fruit f2 = new Fruit();
    		f2.fruitType = this.fruitType;
    		
    		int diff = this.distance/2 + 10;
    		
    		if (xDiff <= yDiff){
    			f2.x = this.x+diff;
    			f2.y = this.y;
    			f1.piece = 1;
    			f2.piece = 2;
    		}
    		else{
    			f2.x = this.x;
    			f2.y = this.y + diff;
    			f1.piece = 3;
    			f2.piece = 4;
    		}
    		f1.direction = 0;
    		f2.direction = 0;
    		
    		return new Fruit[] {f1, f2};
    	}
    	return null;
    }
}
