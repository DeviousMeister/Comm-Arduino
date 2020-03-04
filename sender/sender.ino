/* 
 *  
 *  CSE 132 - Assignment 6
 *  
 *  Fill this out so we know whose assignment this is.
 *  
 *  Name: Moises Daboin 
 *  WUSTL Key: 473091
 *  
 *  and if two are partnered together
 *  
 *  Name:
 *  WUSTL Key:
 */
const int potPin = A0;
const int tempPin = A1;
unsigned long next = 0;
unsigned long hertz = 1000;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(potPin, INPUT);
  pinMode(tempPin, INPUT);
  analogReference(DEFAULT);
}

void loop() {
  // put your main code here, to run repeatedly:
  unsigned long tiempo = millis();
  if(tiempo>next){
    next+=hertz;
    sendInfo("Hello");
    sendTime(tiempo);
    int potRead = analogRead(potPin);
    if(potRead<550){
      sendPot(potRead); 
    }
    else{
      sendError("High alarm");
    }
    int tempRead = analogRead(tempPin);
    sendTemp(tempRead);
  }
  
}

void sendInfo(char* n){
  int words = (int)strlen(n);
  int newSize = words >> 8;
  Serial.write('!');
  Serial.write(0x30);
  Serial.write(newSize);
  Serial.write(words);
  for(int i = 0; i<words; ++i){
    Serial.write(n[i]);
  }
}

void sendTime(unsigned long nLength){
  byte firstLength = (nLength >> 24) & 0xff;
  byte secLength = (nLength >> 16) & 0xff;
  byte thirdLength = (nLength >> 8) & 0xff;
  byte fourthLength = nLength & 0xff;
  Serial.write('!');
  Serial.write(0x32);
  Serial.write(firstLength);
  Serial.write(secLength);
  Serial.write(thirdLength);
  Serial.write(fourthLength);
}

void sendPot(int kms){
  byte first = (kms >> 8) & 0xff;
  byte second = kms & 0xff;
  Serial.write('!');
  Serial.write(0x33);
  Serial.write(first);
  Serial.write(second);
}

void sendError(char* n){
  int words = (int)strlen(n);
  int newSize = words >> 8;
  Serial.write('!');
  Serial.write(0x31);
  Serial.write(newSize);
  Serial.write(words);
  for(int i = 0; i<words; ++i){
    Serial.write(n[i]);
  }
}

void sendTemp(int ugh){
  byte tempLength = ugh;
  byte first = (tempLength >> 8) & 0xff;
  byte second = tempLength & 0xff;
  Serial.write('!');
  Serial.write(0x34);
  Serial.write(first);
  Serial.write(second);
}
