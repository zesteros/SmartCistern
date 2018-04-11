void setup() {
  size(1000, 1000);
  background(255);
}
float y =  0;
boolean full;
int marginTop = 400;
int marginBottom = -400;
int ellipseWidth = 600;
int ellipseHeight = 150;

void draw() {
  background(255);
  stroke(1);
  fill(255);
  /*SUPERIOR ELLIPSE (HALF SCREEN, -200 HEIGHT/2 SCREEN, 100 WIDTH, 50 HEIGHT)*/
  ellipse(width/2, dynamicHeight/2-marginTop, ellipseWidth, ellipseHeight);
  /*INFERIOR ELLIPSE (HALF SCREEN, -50 HEIGHT/2 SCREEN, 100 WIDTH, 50 HEIGHT)*/
  fill(150, 255, 255,180);
  //arc(width/2, dynamicHeight/2-50, 100, 50, 0, PI);
  ellipse(width/2, dynamicHeight/2-marginBottom, ellipseWidth, ellipseHeight);
  fill(255);
  /*LINES X: THE LEFT/right RADIUS OF ELLIPSE, Y: HEIGHT OF CYLINDER*/
  line(width/2-(ellipseWidth/2), dynamicHeight/2-marginTop, width/2-(ellipseWidth/2), width/2-marginBottom);
  line(width/2+(ellipseWidth/2), dynamicHeight/2-marginTop, width/2+(ellipseWidth/2), width/2-marginBottom);
  fillCistern();
  rect(100,400,200,200);
  fill(200);
  rect(400,400,200,200);
  
}

void mousePressed(){
  if(mouseX >= 100 && mouseX <= 300 && mouseY >= 400 && mouseY <= 600)if (y>=-(marginTop + (marginBottom*-1)))y-=10;
  if(mouseX >= 400 && mouseX <= 600 && mouseY >= 400 && mouseY <= 600)if (y<=-1)y+=10;
}

void fillCistern() {
  fill(150, 255, 255,180);
  for (int i = -1; i >= y; i--) {
    if (i-1 == y)stroke(3);
    else noStroke();
    ellipse(width/2, dynamicHeight/2-marginBottom+i, ellipseWidth, ellipseHeight);
  }
}

void keyPressed() {
  println(y);
  if (key == '+') {
    if (y>=-149)y--;
  } else if (key == '-') {
    if (y<= -1)y++;
  }
}