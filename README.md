# ErServer
ErServer is a Java library that provides a clean and simple API for efficient TCP (for now) client/server network communication. 
ErServer is ideal for any client/server application. Currently, the server handles the requests from clients by using a custom Command system. 

Basic request/response mechanism of ErServer works like this:
- Client sents request as string for ex: "commandX"
- Server looks for a registered command which has the matching commandTrigger to the "commandX"
- Server executes the command and sends response if necessary to the client

The main use of ErServer is the simplicity to add new commands to the server. For adding new custom functionalities to ErServer, all you need to do is to create a custom command either by creating a new class or simply using anonymous classes, and register the class you created to the server.
