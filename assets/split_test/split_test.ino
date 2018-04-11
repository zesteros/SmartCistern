void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  char sz[] = "10.23,18.42";
  float number1;
  float number2;
  char *p = sz;
  char *str;
  int i = 0;
  Serial.begin(9600);
  while ((str = strtok_r(p, ",", &p)) != NULL) {// delimiter is the semicolon
    if(i == 0) number1 = atof(str);
    else number2 = atof(str);
    i++;
  }
  Serial.println(number1);
  Serial.println(number2);
  delay(5000);
}
