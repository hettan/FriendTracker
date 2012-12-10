from gcm import GCM

API_KEY = "AIzaSyDkH-XZw5aGxnFw-sHHKeMr2Iy4Ht--O4U"

gcm = GCM(API_KEY)

data = {'param1': 'value1', 'param2': 'value2'}

# JSON request

#Jocke Emulator
# reg_ids = ['APA91bEnZRIjFNRDnVs4yka2_lQ1d-1L4c5DaE5oLy8AvE2CriWavJpOrL445GPDedStWd7FN_qYrE64tjCI0m8EkEyDYCNP8ZwnOBGkhWPHwH9Ph3uBEyPfJq_oC8DjvfLSfbagB_j5W-KE6d4TORCRH7wp3swENw']

# Marcus telefon
reg_ids = ['APA91bGc_ekzCTL8T6Y-wFRdI9WjRHCdpXQra4zGAIwQGWhhILUfuPXsfF8eUSHweJ0DjahI0TiVxqu7mjCo1mPmPJZ9g1S3tsXJUKeDStdBKgLC6oH_4YHzDh2Dx4eVT6iK3p2RwROoBPrCRrjnACvh4zCn0DbjfA']

#data = {'type': '0'}
#response = gcm.json_request(registration_ids=reg_ids, data=data)

#data = {'type': '1', 'user': 'derp'}
#response = gcm.json_request(registration_ids=reg_ids, data=data)

data = {'type': '1', 'user': 'Marcus'}
response = gcm.json_request(registration_ids=reg_ids, data=data)

data = {'type': '2', 'user': 'derp', 'group': 'theDerps'}
response = gcm.json_request(registration_ids=reg_ids, data=data)

data = {'type': '3', 'message': 'Din mamma!', 'user': 'Jocke'}
response = gcm.json_request(registration_ids=reg_ids, data=data)
