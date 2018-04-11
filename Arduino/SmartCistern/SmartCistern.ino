#include <SoftwareSerial.h>

SoftwareSerial wifi(3, 2);

void setup() {
  Serial.begin(115200);
  wifi.begin(115200);
  sendCommand("AT+CIPMUX=1");
  sendCommand("AT+CIPSERVER=1,10001");
}

void loop() {
  if (wifi.available()) {
    if (wifi.find("+IPD,")) {
      int connectionId = wifi.read() - 48;
      String data = "#4+*";
      sendResponse(connectionId, data);
    }
  }
}

void sendCommand(String command) {
  wifi.print(command + "\r\n");
  String response = "";
  long  timeStart = millis();
  while (timeStart + 10 > millis())
    while (wifi.available()) {
      char bytes = (char) wifi.read();
      response += bytes;
    }
  Serial.println(response);
}

void sendResponse(int connectionId, String values) {
  String response;
  
  response = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\n";
  response += values;
  
  int sizeOfResponse = response.length();
  String cipSend = "AT+CIPSEND=";
  cipSend += connectionId;
  cipSend += ",";
  cipSend += sizeOfResponse;
  
  sendCommand(cipSend);
  char data[sizeOfResponse];
  response.toCharArray(data, sizeOfResponse);
  wifi.write(data, sizeOfResponse);
}
