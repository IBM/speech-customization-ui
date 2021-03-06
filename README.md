# IBM Watson Speech Services Customization UI

This code is a user interface for IBM Watson Speech-To-Text and Text-To-Speech.

This will allow users to use the speech services' customization API features from a GUI.

Developer: Alexander Faisman - afaisma@us.ibm.com

Offering Manager: Marco Noel - marco.noel@ca.ibm.com   

How to install the STT UI and run it:

1. Install Maven - https://maven.apache.org/install.html
2. Install latest Java 8 JDK
3. Install NodeJS - https://nodejs.org/en/download/
3. Make sure the environment variable JAVA_HOME is set to your latest Java 8 JDK:
    - For Mac OSX: run  "/usr/libexec/java_home -V", find the path similar to "/Library/Java/JavaVirtualMachines/jdk1.8.xxxx.jdk/Contents/Home"
    - For Windows: the path should be similar to "C:\Progra~1\Java\jdk1.8.xxxx"
4. OPTIONAL - FIRST INSTALLATION ONLY: run the command "mvn clean install"
5. Launch the server: run the command "mvn spring-boot:run"
6. Open your browser, then go to "http://localhost:8080"

If the server is not starting, check if port 8080 is already used by another process (eg. Apache Web Server).
If you wish to use a different port than 8080, run the following command to launch the server
- mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=<port_number>


As a bonus, here is an STT Hands-On Lab with sample data:
- https://www.ibm.com/demos/live/content/watson/stt/lab/hands-on-lab-customization.pdf
- https://www.ibm.com/demos/live/content/watson/stt/lab/lab-materials.zip

You can also go to our great demo sites:
- STT demo: https://www.ibm.com/demos/live/speech-to-text/sales/home
- TTS demo: https://www.ibm.com/demos/live/tts-demo/sales/home
