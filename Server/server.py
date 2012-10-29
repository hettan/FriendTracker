#!/usr/bin/python

import socket
import asyncore
#from multiprocessing import Process

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

server = EchoServer('localhost', 8080)
asyncore.loop()












###############OLD





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
		p.start()
		print 'Connected by', addr

	s.close()



        

def accept_handler(conn, addr):
        data_buffer = ""
        has_received_header = False
	header = None

        while True:
                if not has_received_header:
			data = conn.recv(1024)
			if not data: break
			data_buffer += data
                        
                        if data_buffer.find("\n\n") != -1:
				# We've received the header
				header_data, _, data_buffer = data_buffer.partition("\n\n")
                                data_size = int(header_data)
				has_received_header = True

		else:
			
			while len(data_buffer) < data_size:
				data = conn.recv(min(1024, header.byte_size - len(data_buffer)))
                                data_buffer += data

                        print len(data_buffer) + "bytes received"
                        print data_buffer

                        data_buffer = ""
                        has_received_header = False

        conn.close()

