from gcm import GCM

API_KEY = "AIzaSyDkH-XZw5aGxnFw-sHHKeMr2Iy4Ht--O4U"

gcm = GCM(API_KEY)

data = {'param1': 'value1', 'param2': 'value2'}

# JSON request
reg_ids = ['APA91bGQNK4-thosIo9AiAJOi5x4ewb3fSPbbT2wwbADPkmtUiPTDM-d9rnY-5CNnEWubn7xaa68N2YSs5JTV7iIRzo8v1FtMBysIy-w9uUdJmZDNMXjs8M0uTOAecOi2QNgfv_zaijvAZ-iP4el70TV418YrigkRw']
response = gcm.json_request(registration_ids=reg_ids, data=data)
