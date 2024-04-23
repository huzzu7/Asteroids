import java.awt.*;
import javax.swing.*;

public class SpaceDust {

    public int xPos, yPos;
    public double angle;
    public int distanceTraveled;

    public SpaceDust(int x, int y, double direction){
        xPos =  x;
        yPos =  y;
        angle = direction;
        distanceTraveled = 0;
        
    }

    public void move(){

        //move the space dust
        xPos += 2 * Math.cos(Math.toRadians(angle));
        yPos -= 2 * Math.sin(Math.toRadians(angle));   
        distanceTraveled +=12;
         
    }

    public void draw(Graphics g){

        //draw the space dust
        
		g.setColor(Color.WHITE);
		g.fillOval(xPos-3,yPos-3,6,6);

	}
    
}
