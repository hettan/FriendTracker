handler = {}
handler["login"] = login

def request_handler(jsonStr):
    jData = json.loads(jsonStr)
    print jData

    func = handler(jsonStr.type)
    func(jsonStr.data)


def login(data):
    print data.username
    print data.password
    return True
