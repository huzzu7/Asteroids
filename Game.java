//Asteroid window manager class

 import java.awt.*;
 import java.awt.event.*;
 import javax.swing.*;
 
 
 public class Game extends JFrame{
     Main main;
         
     public Game() {
         super("Asteroids");
         
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         main = new Main();
         add(main);
         pack();  
         setVisible(true);
     }    
     public static void main(String[] arguments) {
         new Game();		
     }
 }
 
