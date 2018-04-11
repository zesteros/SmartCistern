package processing.test.cistern_draw;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class cistern_draw extends PApplet {

public void setup() {
  
  background(255);
}
float y =  0;
boolean full;
int marginTop = 200;
int ellipseWidth = 100;
int ellipseHeight = 50;

public void draw() {
  background(255);
  stroke(1);
  fill(255);
  /*SUPERIOR ELLIPSE (HALF SCREEN, -200 HEIGHT/2 SCREEN, 100 WIDTH, 50 HEIGHT)*/
  ellipse(width/2, height/2-marginTop, ellipseWidth, ellipseHeight);
  /*INFERIOR ELLIPSE (HALF SCREEN, -50 HEIGHT/2 SCREEN, 100 WIDTH, 50 HEIGHT)*/
  fill(150, 255, 255,180);
  //arc(width/2, height/2-50, 100, 50, 0, PI);
  ellipse(width/2, height/2-50, ellipseWidth, ellipseHeight);
  fill(255);
  /*LINES X: THE LEFT/right RADIUS OF ELLIPSE, Y: HEIGHT OF CYLINDER*/
  line(width/2-ellipseHeight, height/2-marginTop, width/2-ellipseHeight, marginTop);
  line(width/2+ellipseHeight, height/2-marginTop, width/2+ellipseHeight, marginTop);
  fillCistern();
  rect(100,400,50,50);
  fill(200);
  rect(400,400,50,50);
  
}

public void mousePressed(){
  if(mouseX >= 100 && mouseX <= 150 && mouseY >= 400 && mouseY <= 450)if (y>=-149)y-=10;
  if(mouseX >= 400 && mouseX <= 450 && mouseY >= 400 && mouseY <= 450)if (y<=-1)y+=10;
}

public void fillCistern() {
  fill(150, 255, 255,180);
  for (int i = -1; i >= y; i--) {
    if (i-1 == y)stroke(3);
    else noStroke();
    ellipse(width/2, height/2-50+i, 100, 50);
  }
}

public void keyPressed() {
  println(y);
  if (key == '+') {
    if (y>=-149)y--;
  } else if (key == '-') {
    if (y<= -1)y++;
  }
}
  public void settings() {  size(500, 500); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "cistern_draw" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
