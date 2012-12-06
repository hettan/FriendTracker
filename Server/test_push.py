from gcm import GCM

API_KEY = "AIzaSyDkH-XZw5aGxnFw-sHHKeMr2Iy4Ht--O4U"

gcm = GCM(API_KEY)

data = {'param1': 'value1', 'param2': 'value2'}

# JSON request
reg_ids = ['APA91bEnZRIjFNRDnVs4yka2_lQ1d-1L4c5DaE5oLy8AvE2CriWavJpOrL445GPDedStWd7FN_qYrE64tjCI0m8EkEyDYCNP8ZwnOBGkhWPHwH9Ph3uBEyPfJq_oC8DjvfLSfbagB_j5W-KE6d4TORCRH7wp3swENw']

data = {'type': '0'}
response = gcm.json_request(registration_ids=reg_ids, data=data)

data = {'type': '1', 'user': 'derp'}
response = gcm.json_request(registration_ids=reg_ids, data=data)

data = {'type': '2', 'user': 'derp', 'group': 'theDerps'}
response = gcm.json_request(registration_ids=reg_ids, data=data)

data = {'type': '3', 'message': 'You are FUGLY!'}
response = gcm.json_request(registration_ids=reg_ids, data=data)
