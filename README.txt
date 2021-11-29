PRÀCTICA 1

El funcionamiento de nuestro proyecto es más bien sencillo.
Solamente debes iniciar una terminal y escribir lo siguiente:
	./start.sh

Si por lo que sea no funciona, puedes hacer lo siguiente:
  1. Tienes que dirigirte al directorio;
	/out/artifacts/IA_jar/
  2. Usar el comando 
	java -jar IA.jar

Si deseas cambiar parámetros que no se dejan escoger (como por ejemplo la seed), puedes
abrir el proyecto en cualquier IDE (Por ejemplo: IntellIJ, Eclipse, ...) y debes ir al 
Main.java, en el cual encontrarás una estructura así:

    	Integer mult         = 	             1;
    	Integer seed         = 		  4781;
    	Integer itMax     = 	        150000;
    	Integer itMaxT    = 		    10;
    	Integer k         =	    	     1;						
    	Double lambda     = 		0.0001;

Para cambiar cualquier valor puedes hacerlo manualmente.