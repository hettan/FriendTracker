import json

handler = {}

def request_handler(jsonStr):
    jData = json.loads(jsonStr)
    print jData

    func = handler[jData["type"]]
    return func(jData["data"])
    

def login(data):
    print data["username"]
    print data["password"]
    return "1"

handler["login"] = login
