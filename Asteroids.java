import java.awt.*;

public class Asteroids {
    public int xPos, yPos;
    public double angle;
    public Polygon asteroidPolygon;
    public double scale;
    public double orientation;
    private boolean spawned = false;
    private int[]xcoord, ycoord;
    public int styleNum;
    
    public Asteroids(int x, int y, double dir, double size, double rot, int style){
        xPos =  x;
        yPos =  y;
        angle = dir;
        orientation = rot;
        asteroidPolygon = new Polygon();
        scale = size;
        styleNum = style;

    } 

    public void move(){

        //Move the asteroid

        xPos += Math.round(5 *(scale+0.3)* Math.cos(Math.toRadians(angle)));
        yPos -= Math.round(5 *(scale+0.3)* Math.sin(Math.toRadians(angle)));

        //If the asteroid enters the screen, set spawned = true
        if (xPos > -120 * scale && xPos < 1000 + 120 * scale && yPos > -150 * scale && yPos < 700 + 150 * scale) {
            spawned = true;
        }

        //If spawned, wrap around the screen
        if (spawned){
            if (xPos < -120 * scale) {
                xPos = (int) (1000 + 120 * scale);
            } else if (xPos > 1000 + 120 * scale) {
                xPos = (int) (-120 * scale);
            }
            
            if (yPos < -150 * scale) {
                yPos = (int) (700 + 150 * scale);
            } else if (yPos > 700 + 150 * scale) {
                yPos = (int) (-150 * scale);
            }
        }
        
    }

    private void coordinateInit(int style){

        //Select the style of the asteroid
        if (style == 1){
            xcoord = new int[]{
            xPos, 
            xPos, 
            xPos + 60, 
            xPos + 120, 
            xPos + 120,
            xPos + 30,
            xPos - 75,
            xPos - 100,
            xPos -20,
            xPos - 100,
            xPos -60
            };
        
            ycoord = new int[]{
            yPos, 
            yPos - 100, 
            yPos - 100, 
            yPos - 50, 
            yPos + 40,
            yPos + 110,
            yPos + 100,
            yPos + 50,
            yPos + 20,
            yPos,
            yPos -80

            };
        }

        else if (style == 2){
            xcoord = new int[]{
            xPos, 
            xPos + 100, 
            xPos + 110, 
            xPos + 30, 
            xPos - 50,
            xPos - 30,
            xPos - 100,
            xPos - 100,
            xPos - 70,
            xPos + 20,
            xPos + 50,
            xPos + 90
            };

        
            ycoord = new int[]{
            yPos, 
            yPos + 30, 
            yPos + 50, 
            yPos + 100, 
            yPos + 100,
            yPos + 50,
            yPos + 50,
            yPos - 30,
            yPos - 100,
            yPos - 80,
            yPos - 100,
            yPos - 40

            };
        }

        else if (style == 3){
            xcoord = new int[]{
                xPos+ 60, 
                xPos + 100, 
                xPos + 40, 
                xPos, 
                xPos - 40,
                xPos - 100,
                xPos -70,
                xPos - 100,
                xPos - 40,
                xPos,
                xPos + 60,
                xPos + 100
            };
            
            ycoord = new int[]{
                yPos + 30, 
                yPos + 60, 
                yPos + 100, 
                yPos + 80, 
                yPos + 100,
                yPos + 40,
                yPos,
                yPos - 40,
                yPos - 100,
                yPos - 80,
                yPos - 100,
                yPos -20
            };
        }
    }

    public void draw(Graphics g){

        //Get coords of the selected style
        coordinateInit(styleNum);

        //Rotate the coords
        for (int i = 0; i < xcoord.length; i++) {

            // Translate vertices to be relative to the center
            int x = (int) ((xcoord[i] - xPos)*scale);
            int y =(int) ((ycoord[i] - yPos)*scale);

            // Apply rotation
            xcoord[i] = (int) (x * Math.cos(Math.toRadians(90-orientation)) - y * Math.sin(Math.toRadians(90-orientation))) + xPos;
            ycoord[i] = (int) (x * Math.sin(Math.toRadians(90-orientation)) + y * Math.cos(Math.toRadians(90-orientation))) + yPos;
        }

        asteroidPolygon = new Polygon(xcoord, ycoord, xcoord.length);

        //Draw the asteroid

        g.setColor(Color.WHITE);

        g.drawPolygon(xcoord,ycoord,xcoord.length);

    }

}
