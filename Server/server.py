#!/usr/bin/python

import socket
#import asyncore
from multiprocessing import Process

def start_server(host, port):
    # Setup socket
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind((host, port))
    s.listen(1)

    print "Listening on " + host +":"+ str(port)

    while True:
        conn, addr = s.accept()
        p = Process(target = accept_handler, args = (conn, addr))
        try:
            
            p.start()
            print 'Connected by', addr
            
        except(e):
            print e
            
    s.close()

        

def accept_handler(conn, addr):
    data_buffer = ""
    
    while True:
        data = conn.recv(1024)
        if not data: break
        
        data_size, _, data_buffer = data.partition("\n\n")
        data_size = int(data_size)
            
        while len(data_buffer) < data_size:
            data = conn.recv(min(1024, data_size - len(data_buffer)))
            data_buffer += data
        
        
        print str(len(data_buffer)) + " bytes received"
        print "data = " + data_buffer
        
        data_buffer = ""

        conn.send("dinmamma");

    conn.close()
    print "conn closed"

    
start_server("127.0.0.1",8080)



###############OLD



class EchoHandler(asyncore.dispatcher_with_send):

    def handle_read(self):
        data = self.recv(8192)
        if data:
            self.send(data)

class EchoServer(asyncore.dispatcher):

    def __init__(self, host, port):
        asyncore.dispatcher.__init__(self)
        self.create_socket(socket.AF_INET, socket.SOCK_STREAM)
        self.set_reuse_addr()
        self.bind((host, port))
        self.listen(5)
        print "Listening on " + host + " port " + str(port)

    def handle_accept(self):
        pair = self.accept()
        if pair is None:
            pass
        else:
            sock, addr = pair
            print 'Incoming connection from %s' % repr(addr)
            handler = EchoHandler(sock)




#server = EchoServer('localhost', 8080)
#asyncore.loop()




