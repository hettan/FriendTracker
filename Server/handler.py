#!/usr/bin/python

import json
import db

handler = {}

def request_handler(jsonStr):
    jData = json.loads(jsonStr, object_hook=decode_dict)
    func = handler[jData["type"]]
    if func == None:
        print "Invalid function type"
        return "Invalid function-type"
    else:
        return func(jData["data"])

#Python makes string to unicode by standard, need to decode this
def decode_dict(data):
    rv = {}
    for key, value in data.iteritems():
        if isinstance(key, unicode):
           key = key.encode('utf-8')
        if isinstance(value, unicode):
           value = value.encode('utf-8')
        elif isinstance(value, list):
           value = decode_list(value)
        elif isinstance(value, dict):
           value = decode_dict(value)
        rv[key] = value
    return rv


#### Account Management ####
def register(data):
    return db.register(data["username"], data["password"], data["mail"])

def changePassword(data):
    return db.changePassword(data["username"], data["old"], data["new"])

def login(data):
    return db.login(data["username"], data["password"])

def logoff(data):
    return db.logoff(data["session"])

def setStatus(data):
    return db.setStatus(data["username"],data["status"])
    
#### Requests/Friends ####        
def addReq(data):
    return db.addFriendReq(data["src"], data["target"])
    
def acceptReq(data):
    return db.acceptFriendReq(data["src"], data["requester"])

def getFriendReq(data):
    return db.getFriendReq(data["username"])

def getRequests(data):
    return db.getRequests(data["username"])

def clearRequests(data):
    return db.clearRequests(data["username"])

def remFriend(data):
    return db.remFriend(data["username"], data["target"])

def getFriends(data):
    return db.getFriends(data["username"])

def getFriendsIfMod(data):
    return db.getFriends(data["username"], data["ts"])

def userSearch(data):
    return db.userSearch(data["username"], data["query"])

#### Groups ####
def createGroup(data):
    return db.createGroup(data["username"], data["name"])

def addGroupMember(data):
    return db.addGroupMember(data["admin"], data["groupID"], data["username"])

def remFromGroup(data):
    return db.remFromGroup(data["admin"], data["groupID"], data["username"])

def leaveGroup(data):
    return db.leaveGroup(data["username"], data["groupID"])

def delGroup(data):
    return db.delGroup(data["username"], data["groupID"])

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
    return db.setPos(data["username"], data["lat"], data["lon"])

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
handler["getRequests"] = getRequests
handler["clearRequests"] = clearRequests
handler["remFriend"] = remFriend
handler["getFriends"] = getFriends
handler["getFriendsIfMod"] = getFriendsIfMod
handler["userSearch"] = userSearch

handler["createGroup"] = createGroup
handler["addGroupMember"] = addGroupMember
handler["remFromGroup"] = remFromGroup
handler["leaveGroup"] = leaveGroup
handler["delGroup"] = delGroup
handler["getGroups"] = getGroups
handler["getGroupInfo"] = getGroupInfo
handler["changeGroupOwner"] = changeGroupOwner
handler["addRallyPoint"] = addRallyPoint
handler["remRallyPoint"] = remRallyPoint
handler["getRallyPoints"] = getRallyPoints


handler["setPos"] = setPos
handler["getPos"] = getPos
handler["getGroupPos"] = getGroupPos
handler["getFriendsPos"] = getFriendsPos

handler["registerPush"] = registerPush
handler["removePush"] = removePush

