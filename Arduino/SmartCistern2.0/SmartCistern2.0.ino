#include <ESP8266WiFi.h>

/*
  Diciembre del 2016

  Sketch para el Robodyn D1 R2 con mÃ³dulo WiFi ESP8266
  Dedicado a leer dos entradas analÃ³gicas enviando una
  seÃ±al digital a un relÃ© y asÃ­ poder alternar la Ãºnica
  entrada anÃ¡logica que contiene, para enviarlos por medio
  de un servidor con direcciÃ³n ip y un puerto.

  Autor: Angelo Loza
*/
/*
  Nombre y contraseÃ±a de la red a conectar
*/


const char* ssid = "CISTERN_AP";          //  your network SSID (name)
const char* pass = "bugatti123";   // your network password
/*
  Nombre y contraseÃ±a del WiFi Robodyn (Arduino)
*/
const char* ssid_ap = "SMART_CISTERN";
const char* pass_ap = "bugatti123";
/*
  Puerto para acceder al Robodyn
*/
const int port = 10001;
/*
  Crea un servidor con el puerto
*/
WiFiServer server(port);
/*Direccion ip estÃ¡tica (cambiar si es necesario)*/
IPAddress ip(192, 168, 3, 100);
/*MÃ¡scara de red (24, cambiar si es necesario)*/
IPAddress mask(255, 255, 255, 0);
/*
  Puerta de enlace (Default Gateway, cambiar si es necesario): Para obtener la puerta de enlace en Windows
  presiona la tecla"Windows" Y la tecla "R" al mismo tiempo y escribe cmd, enseguida en aceptar,
  en la linea de comandos escribe "ipconfig" y luego enter, despuÃ©s buscar la LAN InalÃ¡mbrica y la
  puerta de enlace prederterminada y sobreescribir esta:
*/
IPAddress defaultGateway(192, 168, 3, 1);

const int trigPin = 0;
const int echoPin = 2;

const int channel = 1;

boolean configure = false;

void setup() {
  Serial.begin(115200);
}
void loop() {
  if (!configure) {
    /*
      Establece el nombre y contraseÃ±a del mÃ³dulo ESP8266
      ademÃ¡s del canal (default 1) y si quiere esconder
      el nombre de la red.
    */
    WiFi.softAP(ssid_ap, pass_ap, channel, 0);
    /*Conectar a red WiFi*/
    WiFi.config(ip, defaultGateway, mask);
    Serial.print("Conectando a ");
    Serial.println(ssid);
    /*Trata de conectar a la red establecida*/
    WiFi.begin(ssid, pass);
    boolean isConnected;

    while (WiFi.status() != WL_CONNECTED) {
      if (millis() >= 15000) {
        Serial.println("Tiempo fuera de conexiÃ³n");
        isConnected = false;
        break;
      }
      isConnected = true;
      delay(500);
      Serial.print(".");
    }
    if (isConnected) {
      Serial.println("Se ha conectado exitosamente.");
      Serial.println("WiFi conectado");

      /*Comienza el servidor*/
      server.begin();
      Serial.println("Servidor iniciado");

      /*Imprime la direcciÃ³n y el puerto*/
      Serial.print("Para acceder a los datos ingresa esta direcciÃ³n: ");
      Serial.print("http://");
      Serial.print(WiFi.localIP());
      Serial.println(":" + String(port, DEC));
    } else {
      Serial.println("No se ha podido conectar, tiempo fuera, puede acceder al servidor localmente");
      Serial.print("Para acceder a los datos conectese e ingrese esta direcciÃ³n: ");
      Serial.print("http://");
      Serial.print(WiFi.localIP());
      Serial.println(":" + String(port, DEC));
    }
    configure = true;
  }
  /*Â¿Hay algÃºn cliente que requiera los datos?*/
  WiFiClient client = server.available();
  if (client) {
    /*Â¿El cliente estÃ¡ conectado?*/
    if (client.connected()) {
      Serial.println("connectado");
      String distance = "#" +
                        String(getDistance(), DEC) + "+" +
                        String(WiFi.RSSI(), DEC) + "+" +
                        String(channel, DEC)
                        + "+*";
      client.print(distance);
      delay(200);
    }
    /*Cierra la conexiÃ³n*/
    client.stop();
  }
}

float getDistance() {
  pinMode(trigPin, OUTPUT);
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  pinMode(echoPin, INPUT);
  float duration = pulseIn(echoPin, HIGH);
  return microsecondsToCentimeters(duration);
}

float microsecondsToCentimeters(float microseconds) {
  return microseconds / 29 / 2;
}



