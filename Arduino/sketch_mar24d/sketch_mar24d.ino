#include <SoftwareSerial.h>

const String MODE = "3";
const String SSID_TO_CONNECT  = "LOZA_2";
const String PASS_TO_CONNECT = "bugatti123";
const String AP_SSID = "SMART_CISTERN";
const String AP_PASS = "bugatti123";
const String AP_CHANNEL = "5";
const String AP_SECURITY = "4";
const String IP_ADD_STATION = "192.168.1.100";
const String IP_ADD_AP = "192.168.1.99";
const String DEFAULT_GATEWAY = "192.168.1.254";
const String SUBNET_MASK = "255.255.255.0";
const String PORT = "10001";

const int trigPin = 4;
const int echoPin = 5;

const int timeout = 200;

String commands [][3] = {

  {"AT+CIPMUX=1",             "1000", "3000"},
  {"AT+CIPSERVER=1," + PORT,  "1000", "4000"},
};


SoftwareSerial esp8266(3, 2); // TX,RX

void setup() {
  Serial.begin(115200);
  esp8266.begin(115200);

  for (int i = 0; i < 2; i++) {
    sendCommand(commands[i][0] + "\r\n", commands[i][1].toInt());
    delay(commands[i][2].toInt());
  }

  Serial.println("Server Ready");
}

void loop() {
  if (esp8266.available()) { // check if the esp is sending a message
    if (esp8266.find("+IPD,")) {
      delay(timeout);
      int connectionId = esp8266.read() - 48; // subtract 48 because the read() function returns
      String data = "#";
      sendHTTPResponse(connectionId, data + String(getDistanceFromSensor(), DEC) + "+*");
    }
  }
}

long getDistanceFromSensor() {
  long duration;
  // The sensor is triggered by a HIGH pulse of 10 or more microseconds.
  // Give a short LOW pulse beforehand to ensure a clean HIGH pulse:
  pinMode(trigPin, OUTPUT);
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  // Read the signal from the sensor: a HIGH pulse whose
  // duration is the time (in microseconds) from the sending
  // of the ping to the reception of its echo off of an object.
  pinMode(echoPin, INPUT);
  duration = pulseIn(echoPin, HIGH);

  // convert the time into a distance
  return microsecondsToCentimeters(duration);
}

long microsecondsToCentimeters(long microseconds) {
  // The speed of sound is 340 m/s or 29 microseconds per centimeter.
  // The ping travels out and back, so to find the distance of the
  // object we take half of the distance travelled.
  return microseconds / 29 / 2;
}

String sendData(String command, const int timeout) {
  String response = "";
  int dataSize = command.length();
  char data[dataSize];
  command.toCharArray(data, dataSize);
  esp8266.write(data, dataSize); // send the read character to the esp8266
  return response;
}

void sendHTTPResponse(int connectionId, String content) {
  String httpResponse;
  String httpHeader;
  httpHeader = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\n";
  httpHeader += "Content-Length: ";
  httpHeader += content.length();
  httpHeader += "\r\n";
  httpHeader += "Connection: close\r\n\r\n";
  httpResponse = httpHeader + content + " "; // There is a bug in this code: the last character of "content" is not sent, I cheated by adding this extra space
  sendCIPData(connectionId, httpResponse);
}

void sendCIPData(int connectionId, String data) {
  String cipSend = "AT+CIPSEND=";
  cipSend += connectionId;
  cipSend += ",";
  cipSend += data.length();
  cipSend += "\r\n";
  sendCommand(cipSend, timeout);
  sendData(data, timeout);
}

void sendCommand(String command, const int timeout) {
  String response = "";
  esp8266.print(command); // send the read character to the esp8266
  long int time = millis();
  while ((time + timeout) > millis())
    while (esp8266.available()) {
      // The esp has data so display its output to the serial window
      char c = esp8266.read(); // read the next character.
      response += c;
    }
  Serial.print(response);
}



