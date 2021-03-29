#include<SoftwareSerial.h>

SoftwareSerial bt(11,10); /* (Rx,Tx) */
//
//String flexADC1;
//String flexADC2;
//String flexADC3 ;
//String z ;
//String y;
//String x ;

int flexADC1;
int flexADC2;
int flexADC3 ;
int z ;
int y;
int x ;

void setup()
{

  bt.begin(9600); /* Define baud rate for software serial communication */
  Serial.begin(9600);
  pinMode(A0, INPUT);
  pinMode(A1, INPUT);
  pinMode(A2, INPUT);
  pinMode(A4, INPUT);
  pinMode(A5, INPUT);
  pinMode(A6, INPUT);

}

void loop()
{


  // Read the ADC, and calculate voltage and resistance from it
  flexADC1 = map(analogRead(A0),0,450,100,250);
  flexADC2 = map(analogRead(A1),0,450,100,250);
  flexADC3 = map(analogRead(A2),0,250,100,250);
  z = map(analogRead(A4),0,600,100,999);
  y = map(analogRead(A5),0,600,100,999);
  x = map(analogRead(A6),0,600,100,999);
// z = analogRead(A4);
//  y = analogRead(A5);
//  x = analogRead(A6);
    Serial.print(flexADC1);
    Serial.print("  ");
    Serial.print(flexADC2);
    Serial.print("  ");
    Serial.print(flexADC3);
    Serial.print("  ");
    Serial.print(x);
    Serial.print("  ");
    Serial.print(y);
    Serial.print("  ");
    Serial.println(z);



    bt.print(111);
    bt.print(" ");
    bt.print(flexADC2);
    bt.print(" ");
    bt.print(flexADC3);
    bt.print(" ");
    bt.print(x);
    bt.print(" ");
    bt.print(1111 );
    bt.print(" ");
    bt.print(z);
    bt.print("\n");

  //  char data = flexADC1.concat('&') + flexADC2.concat('&')  + flexADC3.concat('&') + flexADC3.concat('&') + x.concat('&') + y.concat('&') + z.concat("&\n") ;
//  String data = flexADC1 + ' ' + flexADC2 + ' ' + flexADC3 + ' ' + x + ' ' + y + ' ' + z + '\n' ;
//
//  Serial.print(data);
//  bt.print(data);
//  

  delay(250);


}
