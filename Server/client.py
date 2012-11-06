#!/usr/bin/python

import sys
from socket import *
serverHost = "130.236.187.198"            # servername is localhost
serverPort = 8080                   # use arbitrary port > 1024

s = socket(AF_INET, SOCK_STREAM)    # create a TCP socket


s.connect((serverHost, serverPort)) # connect to server on the port
#test = "{\"type\":\"register\",\"data\":{\"username\":\"1\",\"password\":\"1\"}}"
#test = "{\"type\":\"login\",\"data\":{\"username\":\"1\",\"password\":\"1\"}}"
test = "{\"type\":\"logoff\",\"data\":{\"session\":\"13ad14f79b6L-7b5aa949\"}}"
#test = "{\"type\":\"register\",\"data\":{\"username\":\"1\",\"password\":\"1\"}}"
#test = "{\"type\":\"request\",\"data\":{\"src\":\"1\",\"target\":\"2\"}}"
s.send(str(len(test)) + "\n\n" + test)               # send the data
data = s.recv(1024)                 # receive up to 1K bytes
print data
