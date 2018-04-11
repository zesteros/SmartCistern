#define DEBUG false  // turn debug message on or off in serial
#include <SoftwareSerial.h>
SoftwareSerial esp(3, 2); // RX | TX
void setup()
{
  esp.begin(115200);  // using serial 1 if you are using arduino LEO
  Serial.begin(9600);
  delay(2000);

  pinMode(4, OUTPUT);
  digitalWrite(4, HIGH);

  sendData("AT+RST\r\n", 2000, DEBUG); // reset module
  sendData("AT+CWMODE=2\r\n", 1000, DEBUG); // configure as access point
  sendData("AT+CIFSR\r\n", 1000, DEBUG); // get ip address //192.168.4.1
  sendData("AT+CIPMUX=1\r\n", 1000, DEBUG); // configure for multiple connections
  sendData("AT+CIPSERVER=1,80\r\n", 1000, DEBUG); // turn on server on port 80
}

void loop(){
}

/*
  Name: sendData
  Description: Function used to send data to ESP8266.
  Params: command - the data/command to send; timeout - the time to wait for a response; debug - print to Serial window?(true = yes, false = no)
  Returns: The response from the esp8266 (if there is a reponse)
*/
String sendData(String command, const int timeout, boolean debug)
{
  String response = "";

  esp.print(command); // send the read character to the esp8266

  long int time = millis();

  while ( (time + timeout) > millis())
  {
    while (esp.available())
    {

      // The esp has data so display its output to the serial window
      char c = esp.read(); // read the next character.
      response += c;
      debug = true;
    }
  }

  if (debug)
  {
    Serial.print(response);
  }

  return response;
}
