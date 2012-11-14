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
        return "1" + session + "\n"
    else:
        return "0Wrong username or password!\n"

def logoff(data):
    if db.logoff(data["session"]):
        return "1\n"
    else:
        return "0\n"

def register(data):
    if db.register(data["username"], data["password"], data["mail"]):
        return "1\n"
    else:
        return "0Username already in use!\n"

    
#### Requests ####        
    
def addReq(data):
    if db.addFriendReq(data["src"], data["target"]):
        return "1\n"
    else:
        return "0User is already friend with you or you've aldready made a friend request!\n"
    
def acceptReq(data):
    if db.acceptFriendReq(data["src"], data["requester"]):
        return "1\n"
    else:
        return "0Could not find request\n"


#### Groups ####
    
def createGroup(data):
    if db.createGroup(data["user"], data["name"]):
        return "1\n"
    else:
        return "0Session timeout\n"

def addGroupMember(data):
    if db.addGroupMember(data["admin"], data["groupID"], data["user"]):
        return "1\n"
    else:
        return "0\n"

def getGroups(data):
    res = db.getGroups(data["user"])
    if res != None:
        return "1" + str(res) + "\n"
    else:
        return "0Cant find user in db\n"

def changeGroupOwner(data):
    if db.changeGroupOwner(data["owner"],data["groupID"],data["newUser"]):
        return "1\n"
    else:
        return "0\n"

def addRallyPoint(data):
    if db.addRallyPoint(data["user"], data["groupID"], data["pos"], data["text"]):
        return "1\n"
    else:
        return "0\n"

def remRallyPoint(data):
    if db.remRallyPoint(data["user"], data["groupID"]):
        return "1\n"
    else:
        return "0\n"

def getRallyPoints(data):
    res = db.getGroups(data["user"], data["groupID"])
    if res != False:
        return "1" + str(res) + "\n"
    else:
        return "0Cant find user in db\n"

#### Position ####

def setPos(data):
    if db.setPos(data["user"], data["pos"]):
        return "1\n"
    else:
        return "0\n"

def getPos(data):
    res = db.getPos(data["user"])
    if res == False:
        return "0\n"
    else:
        return "1" + str(res) + "\n"

def getGroupPos(data):
    res = db.getGroupPos(data["user"], data["groupID"])
    if res == False:
        return "0\n"
    else:
        return "1" + str(res) + "\n"
    

    
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

