int analogPinA0 = A0;
int val = 0;
char data;
//float valFloat = 0;

void setup(){
  Serial.begin(9600);
}

void loop(){
  
  if(Serial.available()>0){
    data=Serial.read();
    if(data == 'r'){
      val = analogRead(analogPinA0);
      //val = val >> 0; //se recorre un bit
      Serial.println(val);
      //valFloat = ((float)val/1023)*5; //convertimos nuestro numero entero a decimales y despues normalizamos de 0 a 1 y amplificamos
      //Serial.println(valFloat); //imprime como caracteres
    }
  }

}
