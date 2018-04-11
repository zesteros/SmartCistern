#include <ESP8266WiFi.h>
int cisternPin = 2;
float topLevel = 10;
float bottomLevel = 20;

//const char* ssid = "Galaxy s6 edge";          //  your network SSID (name)
//const char* pass = "vxop3375";   // your network password

const char* ssid = "CISTERN_AP";          //  your network SSID (name)
const char* pass = "bugatti123";   // your network password

const char* ssid_ap = "SMART_CISTERN_RECEIVER";
const char* pass_ap = "bugatti123";

const char* servername = "192.168.3.100"; // remote server we will connect to
const int portTran = 10001;

const int portRec = 10002;

WiFiServer server(portRec);

IPAddress ip(192, 168, 3, 101);
/*MÃ¡scara de red (24, cambiar si es necesario)*/
IPAddress mask(255, 255, 255, 0);
/*
  Puerta de enlace (Default Gateway, cambiar si es necesario): Para obtener la puerta de enlace en Windows
  presiona la tecla "Windows" Y la tecla "R" al mismo tiempo y escribe cmd, enseguida en aceptar,
  en la linea de comandos escribe "ipconfig" y luego enter, despuÃ©s buscar la LAN Inalambrica y la
  puerta de enlace prederterminada y sobreescribir esta:
*/
IPAddress defaultGateway(192, 168, 3, 1);

boolean configure = false;
boolean isConnected;
boolean empty;
boolean manual;
boolean onOffPump;

void setup() {
  Serial.begin(9600);
  pinMode(2, OUTPUT);
  digitalWrite(2, HIGH);
}

void loop() {
  if (!configure) {
    Serial.println("Attempting to connect to WPA network...");
    Serial.print("SSID: ");
    Serial.println(ssid);
    WiFi.softAP(ssid_ap, pass_ap, 1, 0);
    WiFi.config(ip, defaultGateway, mask);
    WiFi.begin(ssid, pass);
    while (WiFi.status() != WL_CONNECTED) {
      if (millis() >= 15000) {
        Serial.println("Tiempo fuera de conexionn");
        isConnected = false;
        break;
      }
      isConnected = true;
      delay(500);
      Serial.print(".");
    }

    if (isConnected) {
      server.begin();
      Serial.println("Se ha conectado exitosamente.");
      Serial.println("WiFi conectado");
    } else
      Serial.println("No se ha podido conectar, tiempo fuera.");
    configure = true;
  }
  if (isConnected) {
    receiveData();
    getData();
  }
}

void getData() {
  // if you get a connection, report back via serial:
  WiFiClient client;
  if (client.connect(servername, portTran)) {

    //Serial.println("Connected to server..");
    // Make a HTTP request:
    client.println("GET /? HTTP/1.1");
    client.println();
    String response = "";
    while (client.available()) {
      char c = client.read();
      response += c;
    }
    if (response.startsWith("#")) {
      //Serial.println("There is response");
      response = response.substring(1, 10);
      float distance = response.toFloat();
      Serial.print("Response: ");
      Serial.print(String(distance, DEC) + "\n");
      if (!manual) {
        if (distance <= topLevel)
          empty = false;
        else if (distance >= bottomLevel)
          empty = true;
        if (empty) digitalWrite(cisternPin, LOW);
        else digitalWrite(cisternPin, HIGH);
      } else {
        if (onOffPump)digitalWrite(cisternPin, LOW);
        else digitalWrite(cisternPin, HIGH);
      }
    }
  }
  if (!client.connected()) {
    //Serial.println();
    //Serial.println("disconnecting from server.");
    client.stop();
  }
  delay(1000);
}

void receiveData() {
  WiFiClient client = server.available();
  if (client) {
    /*Â¿El cliente estÃ¡ conectado?*/
    if (client.connected()) {
      client.print("1");
      if (client.find('?')) {
        String response = "";
        int i = 0;
        while (client.available()) {
          char c = client.read();
          if (c == 'H')break;
          response += c;
        }
        //Serial.println(response);
        for (int j = 0; j < 5; j++)
          setData(getValue(response, ',', j), j);
      }
      delay(200);
    }
    /*Cierra la conexiÃ³n*/
    client.stop();
  }
}

String getValue(String data, char separator, int index) {
  int found = 0;
  int strIndex[] = { 0, -1 };
  int maxIndex = data.length() - 1;

  for (int i = 0; i <= maxIndex && found <= index; i++) {
    if (data.charAt(i) == separator || i == maxIndex) {
      found++;
      strIndex[0] = strIndex[1] + 1;
      strIndex[1] = (i == maxIndex) ? i + 1 : i;
    }
  }
  return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}

void setData(String data, int index) {
  Serial.print("data:");
  Serial.println(data);
  switch (index) {
    case 0 : topLevel = data.toFloat();
      Serial.println(topLevel);
      break;
    case 1 : bottomLevel = data.toFloat();
      Serial.println(bottomLevel);
      break;
    case 2 : manual = data == "1" ? true : false;
      Serial.println(manual);
      break;
    case 3 : onOffPump = data.startsWith("1");
      Serial.println(onOffPump);
      break;
  }
}





