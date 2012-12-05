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
def register(data):
    return db.register(data["username"], data["password"], data["mail"])

def changePassword(data):
    return db.changePassword(data["username"], data["old"], data["new"])

def login(data):
    return db.login(data["username"], data["password"], data["pushID"])

def logoff(data):
    return db.logoff(data["session"])

def setStatus(data):
    return db.setStatus(data["username"],data["status"])
    
#### Requests/Friends ####        
def addReq(data):
    return db.addFriendReq(data["src"], data["target"])
    
def acceptReq(data):
    return db.acceptFriendReq(data["src"], data["target"])

def getFriendReq(data):
    return db.getFriendReq(data["username"])

def getFriends(data):
    return db.getFriends(data["username"])

def getFriendsIfMod(data):
    return db.getFriends(data["username"], data["ts"])

def userSearch(data):
    return db.getFriends(data["query"])

#### Groups ####
def createGroup(data):
    return db.createGroup(data["username"], data["name"])

def addGroupMember(data):
    return db.addGroupMember(data["admin"], data["groupID"], data["username"])

def remFromGroup(data):
    return db.remFromGroup(data["admin"], data["groupID"], data["username"])

def leaveGroup(data):
    return db.leaveGroup(data["username"], data["groupID"])

def getGroups(data):
    return db.getGroups(data["username"])

def getGroupInfo(data):
    return db.getGroupInfo(data["username"], data["groupID"])

def changeGroupOwner(data):
    return db.changeGroupOwner(data["owner"],data["groupID"],data["newUser"])

def addRallyPoint(data):
    return db.addRallyPoint(data["username"], data["groupID"], data["pos"], data["text"])

def remRallyPoint(data):
    return db.remRallyPoint(data["username"], data["groupID"])

def getRallyPoints(data):
    return db.getGroups(data["username"], data["groupID"])

#### Position ####
def setPos(data):
    return db.setPos(data["username"], data["pos"])

def getPos(data):
    return db.getPos(data["username"])

def getGroupPos(data):
    return db.getGroupPos(data["username"], data["groupID"])

def getFriendsPos(data):
    return db.getFriendsPos(data["username"])

#### Push ####
def registerPush(data):
    return db.registerPush(data["username"], data["pushID"])

def removePush(data):
    return db.removePush(data["username"])



handler["register"] = register
handler["changePw"] = changePassword
handler["login"] = login
handler["logoff"] = logoff
handler["setStatus"] = setStatus

handler["request"] = addReq
handler["acceptReq"] = acceptReq
handler["getFriendReq"] = getFriendReq
handler["getFriends"] = getFriends
handler["getFriendsIfMod"] = getFriendsIfMod
handler["userSearch"] = userSearch

handler["createGroup"] = createGroup
handler["addGroupMember"] = addGroupMember
handler["remFromGroup"] = remFromGroup
handler["leaveGroup"] = leaveGroup
handler["getGroups"] = getGroups
handler["getGroupInfo"] = getGroupInfo
handler["changeGroupOwner"] = changeGroupOwner

handler["setPos"] = setPos
handler["getPos"] = getPos
handler["getGroupPos"] = getGroupPos
handler["getFriendsPos"] = getFriendsPos

handler["registerPush"] = registerPush
handler["removePush"] = removePush

