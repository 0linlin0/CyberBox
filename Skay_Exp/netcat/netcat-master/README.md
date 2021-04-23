# netcat

**Computer Networks and Telecommunications Programming Assignment 1**

This is a simple Java implementation of the popular netcat (nc) tool (http://netcat.sourceforge.net/), which provides a lightweight mechanism to transfer data across a network.

A UDP implementation of netcat is included. `NetcatUDPServer` and `NetcatUDPClient` function the same way but use UDP as the underlying communication mechanism.

## File Structure

    .
    ├── bin                              # binaries; run commands here
    |   ├── lorem.txt                    # large test file
    |   └── small.txt                    # small test file
    └── src                              # source root
        └── csci4311/nc
            ├── NetcatClient.java        # client implementation
            ├── NetcatServer.java        # server implementation
            ├── NetcatUDPClient.java     # client UDP implementation
            └── NetcatUDPServer.java     # server UDP implementation

## Client/Server Pair

***Netcat server*** binds to a specified port number on the local host and waits for a connection request from a client. Once a connection is established, it operates in one of two modes: download and upload.
* In download mode, it reads data from its standard input device and writes it to the socket.
* In upload mode, it reads data from the socket and writes it to standard output.

***Netcat client*** establishes a connection to the server on the given host name (or IP address) and port
number and operates in one of two modes, opposite the server.
* In download mode, it reads data from the socket and writes it to standard output.
* In upload mode, it reads data from its standard input device and writes it to the socket.

## Getting Started
From the `bin` directory, you can run the following commands:

Netcat server:
```
java csci4311.nc.NetcatServer [port] < [input-file]             # download mode
java csci4311.nc.NetcatServer [port] > [output-file]            # upload mode
```

Netcat client:
```
java csci4311.nc.NetcatClient [host] [port] > [output-file]     # download mode
java csci4311.nc.NetcatClient [host] [port] < [input-file]      # upload mode
```
The UDP programs will run until terminated with `Ctrl-C`.

Netcat UDP server:
```
java csci4311.nc.NetcatUDPServer [port] < [input-file]          # download mode
java csci4311.nc.NetcatUDPServer [port] > [output-file]         # upload mode
```

Netcat UDP client:
```
java csci4311.nc.NetcatUDPClient [host] [port] > [output-file]  # download mode
java csci4311.nc.NetcatUDPClient [host] [port] < [input-file]   # upload mode
```

## Example Usages

Download a file from server:
```
java csci4311.nc.NetcatServer 1234 < small.txt
java csci4311.nc.NetcatClient localhost 1234 > downloaded.txt
```

Upload a file to server:
```
java csci4311.nc.NetcatServer 4321 > uploaded.txt
java csci4311.nc.NetcatClient localhost 4321 < lorem.txt
```

Download a file from server via UDP:
```
java csci4311.nc.NetcatUDPServer 1234 < small.txt
java csci4311.nc.NetcatUDPClient localhost 1234 > downloaded.txt
```

Upload a file to server via UDP:
```
java csci4311.nc.NetcatUDPServer 4321 > uploaded.txt
java csci4311.nc.NetcatUDPClient localhost 4321 < lorem.txt
```
