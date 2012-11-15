import socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(('localhost', 8080))
sslSocket = socket.ssl(s)
print repr(sslSocket.server())
print repr(sslSocket.issuer())
sslSocket.write('5\n\njacob')
s.close()
