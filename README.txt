=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README
PennKey: palmerpa (24904824)
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. Network I/O
  
  My game is an online multiplayer. Players connect to a server that handles connections,
  disconnections, the and nearly all game interactions. Particularly, the tick events
  happen on the server and the server determines and notifies the clients of any changes
  to the model by sending commands. The client receives these commands and updates its
  copy of the model accordingly. Additionally, the client sends the server three kinds of
  requests over the course of the game, also in the form of commands. Therefore, I created
  a client-server architecture that is capable of managing multiple client connections
  and allows for live two way communication between the client and server.
  
  
  2. Appropriately modeling state using collections.
  
  The snake itself is internally stored as a Deque<Point>. Using a Deque allows me to
  very quickly and efficiently move the snake on each tick and makes it easy to grow
  the snake when it eats food. The implementation that I use is a ConcurrentLinkedDeque
  because it is potentially possible for two threads to attempt to access or modify
  the snake simultaneously.
  
  Additionally, the GameModel class (which stores the state of the game) uses two
  ConcurrentHashMaps, one for storing IDs (Strings) to Snakes and the other for
  storing IDs to Food(s). Using Maps allow me to succinctly refer to a particular
  object in a command and quickly access that object when I am parsing that command.
  This is very important as, for instance, when a snake eats food all of the clients
  must know exactly which snake ate which food in order to keep their version of the
  game model synchronized with the server's version. I used the ConcurrentHashMap
  implementation to prevent ConcurrentModificationExceptions, for the same reason as
  why I used a ConcurrentLinkedDeque above.


  3. Using inheritance/subtyping for dynamic dispatch.
  
  I use inheritance and subtyping extensively in order to share as much code as possible
  between the client and the server. The first of these classes is Command, which is
  subtyped by both the client and the server to create the commands that each one is
  capable of sending. Essentially Commands accept various parameters and build a valid
  String of that command that is ready to be sent over a SocketConnection. The commands
  that the server sends are Create, Destroy, Grow, and Turn. A CreateCommand is used to
  notify the clients that they should create a new snake or new food and add it to their
  version of the model. A DestroyCommand is used to notify the clients that they should
  remove a particular snake or food from their model. This is used when a snake eats a
  food (and therefore the food must be destroyed so that no one else can eat it) and when
  a snake dies. A GrowCommand is used to notify the client that it should add a point to
  a particular snake's body (after it ate a piece of food). Finally, a turn command is
  used to notify the clients that a particular snake changed its movement direction.
  A client can send three commands, a StartCommand, KeyCommand, and WhoAmICommand. A
  StartCommand is used whenever the client starts a new game. It results in two actions.
  Firstly, it notifies the client who sent the command of all existing snakes and foods.
  Secondly, it causes the server to send a CreateCommand to all clients telling them to
  add a new snake to their model for that client. A KeyCommand is sent whenever a client
  presses a (valid) key. It notifies the server that a particular client wants to change
  the movement direction of their snake. Lastly, a WhoAmICommand requests that the server
  notify the client who sent the command of the ID that represents them in the model. This
  command is probably the simplest yet also the subtlest and easiest to misunderstand
  command out of all of the commands. It essentially tells the client what their own
  identity is and is sent almost immediately after a connection is established.
  
  The Protocol is used to process incoming messages. It is subclassed by ServerProtocol
  and ClientProtocol in the Server and Client packages respectively. All SocketConnections
  need to have a protocol. It is used to determine how to react and respond to incoming
  commands and requests.
  
  A SocketConnection wraps a socket object to make I/O with it easier. On a low-level,
  it makes a PrintWriter from the socket's output stream, a BufferedReader from the
  socket's input stream, and starts up a thread that waits for incoming messages. All of
  that functionality is needed by both the client and server. This class is subtyped by
  com.palmerpaul.Server.ClientConnection and com.palmerpaul.Client.ServerConnection (no,
  I did not accidentally switch Client and Server there). It is necessary for the server
  and client to each have their own Protocol subclass because each one has its own
  additional requirements for establishing a connection. The ServerConnection must be
  capable of creating a connection using just a hostname and port (rather than an
  already established Socket object) and also adds useful logging messages. The
  ClientConnection overrides the killConnection method to also remove the client
  from the servers map of user ID's to ClientConnections and it adds a private field
  for the user ID to the connection object, which is used throughout the server-side
  part of the application.

  4. Using I/O to parse a novel file format.
  (see https://piazza.com/class/irxmrhiejtb485?cid=2103 for permission)
  
  I am using I/O (a PrintWriter and a BufferedReader) to send communication between
  the server and clients. These communications come in many different formats depending
  on what kind of information needs to be communicated. Most of the parsing of the
  commands occur in the ClientProtocol and ServerProtocol classes. Additionally, classes
  like Point and Direction provide static methods for parsing String representations of
  objects of those types. The following is an enumeration of the formats / grammars of
  all of the messages that can be sent.
  
  CREATE FOOD <ID> <Point>
  CREATE SNAKE <ID> <Direction> {<Point>;<Point>;<Point>...} 
  DESTROY FOOD <ID>
  DESTROY SNAKE <ID>
  TICK
  TURN <ID> <Direction>
  GROW <ID>
  YOU <ID>
  START
  KEY <Direction>
  WHOAMI
  PING
  
  <ID> is an 8 character alphanumeric identifier,
  <Direction> is one of LEFT, UP, RIGHT, or DOWN
  <Point> is of the format (<x-coord>,<y-coord>) (x-coord and y-coord are ints)
  


=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
  
  CLIENT
  
  Canvas - The main display. All graphics are drawn here.
  
  ClientCommands - Commands that the client can send. Contains the KeyCommand,
  StartCommand, and WhoAmICommand classes, which all inherit from Command.
  
  ClientProtocol - Processes messages sent by the server and performs necessary actions.
  
  Game - Entry point for the client. Prompts the user to input a server address,
  displays the instructions window, sets up the root JFrame that holds the canvas,
  and calls the canvas.setup() method, which starts the actual game.
  
  ServerConnection - Connection to the server. Accepts incoming messages from the server
  and passes them to the ClientProtocol for parsing and interpretation. Also provides a
  way to send messages back to the server.
  
  
  
  ROBOT
  
  Robot - A dumb non-human player that pretty much just moves in a square. It interacts
  with the server in the same way that a client would. In fact, it actually just
  initializes a client and sends a KeyCommand every couple of hundred milliseconds to
  simulate a human player pressing an arrow key. Mainly used for testing purposes. 
  
  
  
  SERVER
  
  ClientConnection - Connection to a client. Provides a way to send messages to the
  client. Also, accepts incoming messages from the client and passes them to the
  ServerProtocol for parsing and interpretation. A ClientConnection object is created
  on the server for each client who connects and is removed when that client disconnects.
  
  Response - These are the things that server uses to send messages. Each response has
  a message field and a recipients field. Responses can be created using Commands or
  Strings.
  
  Server - This is the real "meat" of the server. Handles client connections and
  disconnections. Manages a collection of open socket connections to clients. Routes
  Responses to the appropriate clients. Controls the "tick" of the game.
  
  ServerCommands - Commands that the server can send. Contains the CreateCommand,
  DestroyCommand, GrowCommand and TurnCommand classes, which all inherit from Command.
  
  ServerMain - Entry point for the server. Prompts the user to input a port to host
  the server from. Creates the Server object. Displays a small JFrame with a message
  to let the user know that the server is running.
  
  ServerProtocol - Processes messages sent by a client and performs necessary actions.
  Each client gets their own ServerProtocol object so that individualized state can be
  preserved throughout the lifetime of the client.
  
  
  
  SHARED
  
  Command - Parent class of all commands sent by the server and client. Each command
  has its own associated format / grammar. Aids in the creation of commands with
  potentially complicated structures.
  
  Direction - Enumeration of the directions that a snake can move in.
  
  Food - A food. Stores it location and a color to use to display it. The
  color that is used is determined individually by each client (i.e. two clients
  most likely will not see a particular food object with the same color).
  
  GameConstants - All the "magic numbers" used in the game. Allows for easy
  modification of many of the game settings, such as the size of the screen,
  the snake speed, the amount of food that exists at any given time, the size
  of one "grid unit" (see the Javadocs for a detailed explanation of this), etc.
  
  GameModel - The game model used by the client and server. Stores a map of
  IDs to snakes and another map of IDs to food. The model is 100% thread safe.
  
  Point - The abstract (not in the technical sense) concept of a point.
  
  Protocol - Processes incoming messages. Subclasses must implement the process(String)
  method. ServerProtocol and ClientProtocol are both children of this class. All 
  SocketConnection objects must have a protocol.
  
  Snake - A snake. Stores the points that make up its body as a Deque<Point>
  and the direction that it is moving in. Provides methods for moving a snake,
  growing a snake, getting the length of a snake, etc.
  
  SocketConnection - A connection with a socket. On a high-level, it wraps a socket
  object to make I/O easier. On a low-level it makes a PrintWriter from the socket's
  output stream, a BufferedReader from the socket's input stream, and starts up a
  thread that waits for incoming messages. ClientConnection and ServerConnection
  both inherit from this class.
  


- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
  
  This was the first time that I had written my own server-client architecture. In the
  beginning I just kind of messed around with Socket objects until I figured out what
  to do. Writing the server and client code involved quite a bit of trial and error.
  
  When I first tested my game there were numerous issues with concurrency. I realized
  that I needed to make my collections thread safe which itself took some time to do
  correctly, as there are quite a few collections in the application and many ways of
  interacting with (and potentially breaking) those collections.
  
  Lastly, there was a very strange bug that involved the snake being drawn a significant
  number of pixels away from where it actually was. This ended up being an issue with how
  the client and server were syncing their models and a (now removed) alternate
  constructor in my Snake class. It took several hours of debugging to properly identify
  and rectify the issue due to its seemingly random behavior and the bug only appearing
  under very specific circumstances.


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?
  
  I think that I separated the functionality of the game pretty well. Splitting up
  my code into packages definitely helped with my organization. Before, I had all the
  classes in the default package and I was pretty much writing spaghetti code. Dividing
  the code into packages forced me to consider which parts of my code depended on which
  other parts and allowed me to share a considerable portion of the code between the
  client and server (see the com.palmerpaul.Shared package).



========================
=: External Resources :=
========================

- Cite any external resources (libraries, images, tutorials, etc.) that you may
  have used while implementing your game.
  
  Lots and lots of Javadocs.
  
==========================
=: How to Compile / Run :=
==========================

Compiling
1. unzip [DIR_NAME].zip
2. cd [DIR_NAME]
3. mkdir bin   (if bin directory does not already exist)
4. javac src/com/palmerpaul/*/*.java -d bin/
5. cd bin

Running the Server
1. Get the public IP number of the hosts (needed to connect clients)
    - Search through the output of ifconfig
    - curl ifconfig.me
2. java com.palmerpaul.Server.ServerMain
3. Enter port number in pop-up dialog   (21212 works well in my tests)

Running the Client
1. Start the server
2. java com.palmerpaul.Client.Game     (runs the client)

Running the Robot
1. Start the server
2. java com.palmerpaul.Robot.Robot     (runs the robot)
