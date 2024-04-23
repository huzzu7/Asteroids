import java.awt.*;
import java.util.ArrayList;

public class UFO {

    public int xPos, yPos;
    public Polygon UFOPolygon;
    public ArrayList<Bullet> bullets;
    public double angle;
    public static int time = 0;
    public boolean kill;
    public boolean spawned;

    private int[] xcoord;
    private int[] ycoord;


    public UFO(int x, int y, double dir, ArrayList<Bullet> bulletList){
        xPos =  x;
        yPos =  y;
        bullets = bulletList;
        xcoord = new int[8];
        ycoord = new int[8];
        UFOPolygon = new Polygon();
        angle = dir;
        kill = false;
        spawned = false;

    }

    public void shoot(double targetAngle){
        //shoot at the target angle
        if (Bullet.time==12)
            bullets.add(new Bullet(xPos, yPos, targetAngle, "ufo"));
    }

    public void move(int chance, int chancing){

        //random movement for the enemy ship
        if (chance %64 == 0&&spawned){
            if (chancing==0)
                angle+=45;
            else
                angle-=45;
        }

        //update the position
        xPos += Math.round(7* Math.cos(Math.toRadians(angle)));
        yPos += Math.round(7* Math.sin(Math.toRadians(angle)));

        //Set the spawn and kill booleans acccording to logic
        if (xPos > 0 && xPos < 1000 && yPos > 0 && yPos < 700) {
            spawned = true;
        } else {
           if (spawned)
                kill = true;
        }
    }

    public void draw(Graphics g){

        //UFO coords
        xcoord = new int[] {xPos-20, xPos - 10, xPos+10, xPos + 20, xPos+10, xPos+7, xPos-7, xPos-10 }; 
        ycoord = new int[] {yPos,    yPos + 7,  yPos+7,  yPos,      yPos-7,  yPos-13, yPos-13, yPos-7};
        
        UFOPolygon = new Polygon(xcoord,ycoord,xcoord.length);

        //Draw the UFO
        g.setColor(Color.WHITE);
        g.drawPolygon(UFOPolygon); 
        g.drawLine(xPos-20, yPos, xPos+20, yPos);
        g.drawLine(xPos-10, yPos-7, xPos+10, yPos-7);

    }

}
