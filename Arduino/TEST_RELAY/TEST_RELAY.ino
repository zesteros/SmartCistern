#include <SoftwareSerial.h>

SoftwareSerial wifi(4, 3);//rx,tx
void setup() {
  Serial.begin(9600);
  wifi.begin(9600);
  pinMode(2, OUTPUT);
}

void loop() {
  if (wifi.available()) {
    int c = wifi.read();
    Serial.println(c);
    if (c == 48)
      digitalWrite(2, LOW);
    else if (c == 49)
      digitalWrite(2, HIGH);
    delay(100);
  }
}
