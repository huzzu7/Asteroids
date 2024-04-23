import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import java.util.Random;
import java.util.ArrayList;

//Main class to handle the game logic

    class Main extends JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener{

        //Create the variables for the class
        private Timer timer;
        private Random rand;
        private boolean []keys;
        private int width = 1000;
        private int height = 700;
        private Player player;
        private Font fnt;	
        private Font logo;
        private String screen = "intro";
        private int lives;
        private final Music fireSound;
        private final Music thrustSound;
        public ArrayList<Bullet> bullets;
        public ArrayList <Asteroids> asteroids;
        public ArrayList <SpaceDust> dustParticles;
        public Asteroids deadAsteroid;
        public UFO ufo;
        public int bigAsteroids;
        public int score;

        //Initialize the objects
        public Main(){

            setPreferredSize(new Dimension(width, height));

            //Create Sound Effects objects
            fireSound = new Music("Sounds/fire.wav");
            thrustSound = new Music("Sounds/thrust.wav");

            //Load the font
            try {
                Font fnt = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts/Proxy 1.ttf"));
                this.fnt = fnt.deriveFont(Font.BOLD, 36);
                Font logo = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts/Bauhaus Demi.ttf"));
                this.logo = logo.deriveFont(Font.PLAIN, 130);
            }catch (FontFormatException | IOException exception) {}

            
            keys = new boolean[1000];
            bullets = new ArrayList<>();
            asteroids = new ArrayList<>();
            dustParticles = new ArrayList<>();
            player = new Player(width/2,height/2, KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_SPACE, bullets, fireSound, thrustSound);
            rand = new Random();
            bigAsteroids = 0;
            score = 0;
            lives =3;
            timer = new Timer(20, this);
            timer.start();

            //Initialize eventlisteners
            setFocusable(true);
            requestFocus();
            addKeyListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
            
        }

        //Update every frame
        @Override
        public void actionPerformed(ActionEvent e){
            update();
            repaint();
        }
        
        //Get keyboard input
        @Override
        public void keyPressed(KeyEvent e){
            keys[e.getKeyCode()] = true;
        }
        @Override
        public void keyReleased(KeyEvent e){
            keys[e.getKeyCode()] = false;

        }
        @Override
        public void keyTyped(KeyEvent e){}
        @Override
        public void	mouseClicked(MouseEvent e){}

        @Override
        public void	mouseEntered(MouseEvent e){ }

        @Override
        public void	mouseExited(MouseEvent e){}
        
        //Get mouse input
        @Override
        public void	mousePressed(MouseEvent e){
            int x = e.getX();
            int y = e.getY();
            Rectangle rec = new Rectangle(408, 430, 185, 40);
            if(rec.contains(x,y)){
                resetGame();
                screen = "game";
            } 
            
        }

        //Add hover effect to cursor
        @Override 
        public void mouseMoved(MouseEvent e){
            int x = e.getX();
            int y = e.getY();
            Rectangle rec = new Rectangle(408, 430, 185, 40);
            if(rec.contains(x,y)&&(screen.equals("intro")||screen.equals("end"))){ 
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } 
            else{
                setCursor(Cursor.getDefaultCursor());
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }
        
        

        @Override
        public void	mouseReleased(MouseEvent e){}

        //Paint the graphics
        @Override
        public void paint(Graphics g){

            //Set background
            
            g.setColor(Color.BLACK);
            g.fillRect(0,0,getWidth(),getHeight());

            //Paint the objects corresponding to the correct screen

            if(screen.equals("game")){
                g.setColor(Color.WHITE);
                g.setFont(fnt);
                g.drawString(""+score, 60, 70);
                g.drawString("A".repeat(lives), 60,120);  
                player.draw(g);

                
            }
            
            if(screen == "intro"){
                g.setColor(Color.WHITE);
                g.setFont(fnt);
                g.drawString("CONTINUE",408,460);
                g.setFont(logo);
                g.drawString("ASTEROIDS", 200,200);
                
            }

            if(screen == "end"){
                asteroids.clear();
                g.setColor(Color.WHITE);
                g.setFont(fnt);
                g.drawString("CONTINUE",408,460);
                Font endFont = fnt.deriveFont(Font.BOLD, 56);
                g.setFont(endFont);
                g.drawString("GAME OVER", 328, 200);
            }

            //Paint the game objects

            for (Bullet bullet: bullets){
                bullet.draw(g);
            }
            for (SpaceDust dustParticle : dustParticles)
                dustParticle.draw(g);

            for (Asteroids asteroid: asteroids)
                asteroid.draw(g);

            if (ufo!=null)
                ufo.draw(g);

        }

        private void update(){

            //End the game if no lives remain

            if (lives == 0) screen = "end";

            //Update player life
            if (player.dead && player.deathTimer>100){
                lives-=1;
                player = new Player(width/2,height/2, KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_SPACE, bullets, fireSound, thrustSound);
            }

            //Update player input
            player.update(keys);

            //Spawn space entities
            spawnEntities();
            
            
            //Update the timers
            Bullet.time = Bullet.time > 0 ? Bullet.time-1 : 12;
            UFO.time = UFO.time>100 ? 0: UFO.time+1;

            //Move objects
            move();

            //check collisions
            collisions();

            
            //Shoot UFO
            enemyAttack();


        }

        private void collisions(){
            //Handle bullet collision logic

            bullets.removeIf(bullet -> {
                if (playerCollide(bullet)) return true;
                if (ufoCollide(bullet)) return true;
                if (asteroids.removeIf(asteroid -> asteroidCollide(asteroid, bullet))){   
                    if (deadAsteroid.scale > 0.3)
                        spawnAsteroid(deadAsteroid.xPos, deadAsteroid.yPos, bullet.angle, deadAsteroid.scale * 0.8, deadAsteroid.styleNum);
                    dustParticleAnimation(bullet.xPos, bullet.yPos);
                    return true;
                } 
                return false;     
            });
            
            //Handle asteroid collisions logic (excluding player)
            if (asteroids.removeIf(asteroid -> {
                deadAsteroid = asteroid;
                if (ufo != null)
                    return asteroid.asteroidPolygon.intersects(ufo.UFOPolygon.getBounds2D());
                return false;
            })){
                if (deadAsteroid.scale > 0.3){
                        spawnAsteroid(deadAsteroid.xPos, deadAsteroid.yPos, ufo.angle, deadAsteroid.scale * 0.8, deadAsteroid.styleNum);
                        dustParticleAnimation(deadAsteroid.xPos, deadAsteroid.yPos);
                        dustParticleAnimation(ufo.xPos, ufo.yPos);
                        ufo = null;
                }
            };
            
            //Handle player and ufo collisions
            for (Point pt : player.playerhitbox){
                for (Asteroids asteroid : asteroids){
                    if (pt != null) {
                    if (asteroid.asteroidPolygon.contains(pt)){
                        playerDeath();
                    }
                }
                }
                if (ufo!=null && pt != null){
                    if (ufo.UFOPolygon.getBounds2D().contains(pt)){
                        dustParticleAnimation(ufo.xPos, ufo.yPos);
                        ufo = null;
                        playerDeath();
                    }
                }
            }

        }

        private void enemyAttack(){
            //Attack other entities
            if (ufo != null){
                ufo.move(rand.nextInt(), rand.nextInt(2));
                int ufoTarget = rand.nextInt(asteroids.size()+2);
                if(ufo.spawned){
                    if (ufoTarget>asteroids.size()-1){
                        double targetAngle = Math.toDegrees(Math.atan2((-player.yPos+ufo.yPos),(player.xPos-ufo.xPos)));
                        ufo.shoot(targetAngle);
                    }
                    else{
                        double targetAngle = Math.toDegrees(Math.atan2((-asteroids.get(ufoTarget).yPos+ufo.yPos),(asteroids.get(ufoTarget).xPos-ufo.xPos)));
                        ufo.shoot(targetAngle);
                    }
                }
            }
        }

        private void move(){
            
            asteroids.forEach(Asteroids::move);
            bullets.forEach(Bullet::move);
            bullets.removeIf(bullet -> (bullet.distanceTraveled>500&&bullet.owner.equals("player"))||(bullet.distanceTraveled>300&&bullet.owner.equals("ufo")));
            dustParticles.forEach(SpaceDust::move);
            dustParticles.removeIf(dustParticle -> dustParticle.distanceTraveled>300);
        }


        private void spawnEntities(){
            
            //Generate random coords

            int xVal, yVal;
            xVal = rand.nextInt(1001) - 500;  
            yVal = rand.nextInt(701) - 350;   
            
            //Place the coords outside the screen
            xVal = xVal<0? xVal-1000:xVal+1000;
            yVal = yVal<0? yVal-700:yVal+700;
        
            //Get the path to enter the screen
            double angleToCenter = Math.toDegrees(Math.atan2((yVal-350),(500-xVal)));
            double angleSkew = rand.nextInt(11)-5;
            angleToCenter+=angleSkew;

            //Spawn enemy if player survives past a certain threshold
            if (ufo == null&&UFO.time==100&&score>2000){
                ufo = new UFO(xVal,yVal, -angleToCenter, bullets);
            }

            //Spawn asteroids
            if (bigAsteroids<4){
                asteroids.add(new Asteroids(xVal, yVal,angleToCenter, 1.0, rand.nextDouble()*360, rand.nextInt(2)+1));
            }

            //Kill enemy if it exits the screen
            if (ufo != null && ufo.kill){
                ufo = null;
            }

            //Manage the amount of asteroids by limiting
            bigAsteroids = (int) asteroids.stream().filter(asteroid -> asteroid.scale == 1).count();

            if (asteroids.size()>10){
                asteroids.remove(0);
            }
        }


        private void dustParticleAnimation(int xpos, int ypos){
            //Random motion for dust particles from the site of collison
            for (int i = 0; i < 5; i++) {
                double particleAngle = rand.nextDouble() * 360;
                dustParticles.add(new SpaceDust(xpos, ypos, particleAngle));
            }

        }

        private boolean playerCollide(Bullet bullet){
            //Check player-bullet collision
            if (player.playerPolygon.getBounds2D().contains(bullet.xPos, bullet.yPos) && bullet.owner.equals("ufo")) {
                playerDeath();
                return true; 
            } 
            return false;
        }

        private boolean asteroidCollide( Asteroids asteroid, Bullet bullet){
            //check bullet-asteroid collisions
            if(asteroid.asteroidPolygon.contains(bullet.xPos, bullet.yPos)){
                deadAsteroid = asteroid;
                if (bullet.owner.equals("player")){
                    if(asteroid.scale == 1) score+=200;
                    if(asteroid.scale == 0.5) score+=500;
                    else score+=1000;
                }
                return true;
            }
            return false;
        }

        private boolean ufoCollide(Bullet bullet){
            //check enemy-bullet collisions
            if (ufo!=null){
                if(ufo.UFOPolygon.getBounds2D().contains(bullet.xPos, bullet.yPos) && bullet.owner.equals("player")){
                score+=500;
                ufo = null;
                dustParticleAnimation(bullet.xPos, bullet.yPos);
                return true;
                }
            }
            return false;

        }

        private void playerDeath(){
            //Kill the player
            if(player.spawnTimer==75 && screen == "game"){
                player.dead = true;
        }
        }

        //Spawn asteoroids after a collision
        private void spawnAsteroid(int x, int y, double angle, double size , int style){
            asteroids.add(new Asteroids(x, y, angle+70, size*0.5, rand.nextDouble()*360, style));
            asteroids.add(new Asteroids(x, y, angle-70, size*0.5, rand.nextDouble()*360, style));
        }

        //Reset the game objects 
        private void resetGame() {
            lives = 3;
            score = 0;
            player = new Player(width/2,height/2, KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_SPACE, bullets, fireSound, thrustSound);
            bullets.clear();
            ufo = null;
            dustParticles.clear();
        }
        
        public static void main(String[] arguments) {
            new Game();
        }


    }

