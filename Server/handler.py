#!/usr/bin/python

import json
import db

handler = {}

def request_handler(jsonStr):
    jData = json.loads(jsonStr)
    print jData

    func = handler[jData["type"]]
    return func(jData["data"])


#### Account Management ####

def login(data):
    session = db.login(data["username"], data["password"])
    if session != None:
        return "1"+session
    else:
        return "0" + "Wrong username or password!"

def logoff(data):
    if db.logoff(data["session"]):
        return "1"
    else:
        return "0"

def register(data):
    if db.register(data["username"], data["password"]):
        return "1"
    else:
        return "0" + "Username already in use!"

    
#### Requests ####        
    
def addReq(data):
    if db.addFriendReq(data["src"], data["target"]):
        return "1"
    else:
        return "0" + "User is already friend with you or you've aldready made a friend request!"
    
def acceptReq(data):
    if db.acceptFriendReq(data["src"], data["requester"]):
        return "1"
    else:
        return "0" + "Could not find request"


#### Groups ####
    
def createGroup(data):
    if db.createGroup(data["user"], data["name"]):
        return "1"
    else:
        return "0" + "Session timeout"

def addGroupMember(data):
    if db.addGroupMember(data["admin"], data["groupID"], data["user"]):
        return "1"
    else:
        return "0"

def getGroups(data):
    res = db.getGroups(data["user"])
    if res != None:
        return "1" + str(res)
    else:
        return "0" + "Cant find user in db"

def changeGroupOwner(data):
    if db.changeGroupOwner(data["owner"],data["groupID"],data["newUser"]):
        return "1"
    else:
        return "0"


#### Position ####

def setPos(data):
    if db.setPos(data["user"], data["pos"]):
        return "1"
    else:
        return "0"

def getPos(data):
    res = db.getPos(data["user"])
    if res == False:
        return "0"
    else:
        return "1"+res

def getGroupPos(data):
    res = db.getGroupPos(data["user"], data["groupID"])
    if res == False:
        return "0"
    else:
        return "1"+res
    

    
handler["login"] = login
handler["logoff"] = logoff
handler["register"] = register
handler["request"] = addReq
handler["acceptReq"] = acceptReq
handler["createGroup"] = createGroup
handler["addGroupMember"] = addGroupMember
handler["getGroups"] = getGroups
handler["changeGroupOwner"] = changeGroupOwner
handler["setPos"] = setPos
handler["getPos"] = getPos
handler["getGroupPos"] = getGroupPos

