import java.awt.*;
import java.util.ArrayList;


public class Player {
    public int xPos, yPos, upKey, rightKey, leftKey, shootkey;
    public double angle;
    private double velocityX, velocityY;
    private final double acceleration =0.8;
    private final double deceleration = 0.1;
    private final double maxSpeed = 10.0;
    public ArrayList<Bullet> bullets;
    public Polygon playerPolygon;
    public Polygon flamePolygon;
    private int thrustTimer = 0;
    public int spawnTimer;
    public int deathTimer;
    private boolean accelerating;
    public boolean dead = false;

    public Music thrustSound;
    public Music fireSound;

    private int[] xcoord;
    private int[] ycoord;
    private int[] flame_xcoords;
    private int[] flame_ycoords;
    public Point[] playerhitbox;

    public Player(int x, int y, int up, int right, int left, int shoot, ArrayList<Bullet> bulletList, Music fire, Music thrust){
        xPos = x;
        yPos = y;
        upKey = up;
        rightKey = right;
        leftKey = left;
        shootkey = shoot;
        angle = 90;
        velocityX = 0;
        velocityY = 0;
        bullets = bulletList;
        playerPolygon = new Polygon();
        flamePolygon = new Polygon();
        xcoord = new int[5];
        ycoord = new int[5];
        flame_xcoords = new int[3];
        flame_ycoords = new int[3];
        playerhitbox = new Point[5];
        spawnTimer = 0;
        deathTimer = 0;
        fireSound = fire;
        thrustSound = thrust;



    }

    
    public void update(boolean []keys){

        //Update the timers
        spawnTimer = spawnTimer<75 ? spawnTimer+1 : spawnTimer;
        thrustTimer ++;
        thrustTimer = thrustTimer > 4 ? 0 : thrustTimer;

        //If player isn't dead, update the input        
        if(spawnTimer>20&&!dead){

            if (keys[upKey]){
                thrust();
                thrustSound.play();
                accelerating = true;
            }

            else {
                coast();
                accelerating = false;
            }

            if (keys[rightKey]) angle-=5;
            if (keys[leftKey]) angle+=5;
            if (keys[shootkey]) shootBullet();
        }

        //if the player is dead increment the death animation timer
        else{
            deathTimer+=1;  
        }

        //Update player hitbox
        playerhitbox[0] = new Point(xcoord[0],ycoord[0]);
        playerhitbox[1] = new Point(xcoord[1],ycoord[1]);
        playerhitbox[2] = new Point(xcoord[3],ycoord[3]);
        playerhitbox[3] = new Point((xcoord[0]+xcoord[1])/2,(ycoord[0]+ycoord[1])/2);
        playerhitbox[4] = new Point((xcoord[0]+xcoord[3])/2,(ycoord[0]+ycoord[3])/2);
        
        //Wrap around the screen
        yPos = (yPos + 700) % 700;
        xPos = (xPos + 1000) % 1000;
        
        //Update player position
        xPos += Math.round(velocityX);
        yPos -= Math.round(velocityY);
    }
    

    private boolean thrust(){

        //Update the velocities while accelerating
        velocityX += acceleration * Math.cos(Math.toRadians(angle));
        velocityY += acceleration * Math.sin(Math.toRadians(angle));
        
        //Make sure player doesn't exceed max speed
        double speed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (speed > maxSpeed) {
            double ratio = maxSpeed / speed;
            velocityX *= ratio;
            velocityY *= ratio;
        }

        return true;
    }

    private boolean coast() {

        double speed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        
        //Decelerate while speed is greater than the min speed
        if (speed > deceleration) {
            double ratio = (speed - deceleration) / speed;
            velocityX *= ratio;
            velocityY *= ratio;
        } 
        //Cap the min speed to zero
        else {
            velocityX = 0;
            velocityY = 0;
        }
        return false;
    }
    

    private void shootBullet() {
        //Shoot bullet if one was shot 4 counts before
        if ((int)Bullet.time%4==0){
            bullets.add(new Bullet(xcoord[0], ycoord[0], angle, "player"));
            fireSound.play();
        }

    }

    private int[][] rotate(int[] Xcoord, int[] Ycoord){

        //Rotate the player coords
         for (int i = 0; i < Xcoord.length; i++) {

            // Translate vertices to be relative to the center
             int x = Xcoord[i] - xPos;
             int y = Ycoord[i] - yPos;

            
            // Apply rotation
     
             Xcoord[i] = (int) (x * Math.cos(Math.toRadians(90-angle)) - y * Math.sin(Math.toRadians(90-angle))) + xPos;
             Ycoord[i] = (int) (x * Math.sin(Math.toRadians(90-angle)) + y * Math.cos(Math.toRadians(90-angle))) + yPos;
         }
        
        return new int [][] {Xcoord,Ycoord};
    }

    
    public void draw(Graphics g){
        
        g.setColor(Color.WHITE);

        //If player is alive, display
        if(!dead){
            
            //player coords
            xcoord = new int[] {xPos, xPos - 12, xPos, xPos + 12, xPos}; 
            ycoord = new int[] {yPos-20, yPos+20, yPos+10, yPos +20, yPos-20}; 
            int [][] rotatedMatrix = rotate(xcoord, ycoord);

            //flame coords
            flame_xcoords = new int[] {xPos - 6, xPos, xPos+6};
            flame_ycoords = new int[] {yPos + 15, yPos+25, yPos +15};
            int [][] flamerotatedMatrix = rotate(flame_xcoords,flame_ycoords);

            playerPolygon = new Polygon(rotatedMatrix[0], rotatedMatrix[1], xcoord.length); 
            
            //add the flickering flame animation
            if (accelerating&&(thrustTimer%4)>2){
                flamePolygon = new Polygon(flamerotatedMatrix[0], flamerotatedMatrix[1], flame_xcoords.length);
                g.drawPolygon(flamePolygon);
            }

            //if the player was just spawned, flicker
            if ((spawnTimer==75||(thrustTimer%4)>2)){
                g.drawPolygon(playerPolygon);
            }
        }

        else{

            //Animate the death 
            
            g.drawLine(xPos+deathTimer/4, yPos-20+deathTimer/4, xPos-12+deathTimer/4, yPos+20+deathTimer/4);
            g.drawLine(xPos-12, yPos+20+deathTimer/-4, xPos+12, yPos+12+deathTimer/-4);
            g.drawLine(xPos+deathTimer/-4, yPos-20+deathTimer/4, xPos+12+deathTimer/-4, yPos+20+deathTimer/4);
        }
    


        
	}


   
    
}
