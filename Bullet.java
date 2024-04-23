import java.awt.*;
import javax.swing.*;

public class Bullet {
    public int xPos, yPos;
    public double angle;
    public int distanceTraveled;
    public static int time = 12;
    public String owner;
    public double bulletSpeed;

    public Bullet(int x, int y, double direction, String origin){
        xPos =  x;
        yPos =  y;
        angle = direction;
        distanceTraveled = 0;
        owner  = origin;
        bulletSpeed = owner.equals("player")?18:9;
        
    }

    public void move(){
        
        // Update position
        xPos += Math.round(bulletSpeed * Math.cos(Math.toRadians(angle)));
        yPos -= Math.round(bulletSpeed * Math.sin(Math.toRadians(angle)));   

        //Update distance travelled
        distanceTraveled +=12;

        //Wrap around the screen
        yPos = (yPos + 700) % 700;
        xPos = (xPos + 1000) % 1000;
    }

    
    public void draw(Graphics g){

        //Draw the bullet
		g.setColor(Color.white);
		g.fillOval(xPos-2,yPos-2,4,4);


	}
}
