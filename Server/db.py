#!/usr/bin/python

from pymongo import Connection
import time,random

connection = Connection('localhost', 27017)
db = connection.test
users = db.users
sessions = db.sessions
groups = db.groups


#### Account Management #####

def register(username, password):
    if users.find_one({"username": username}) == None:
        new_user = {"username": username, "password": password,
                    "friends":[], "active":False, "groups":[]}
        users.insert(new_user)
        pri(users)
        return True
    else:
        return False

def login(username, password):
    user = users.find_one({"username": username})
    if user != None and user["password"] == password:
        users.update({"username":user},{"$set":{"active":True}})
        return setSession(username)
    else:
        return None

def logoff(session):
    user = remSession(session)
    if user != None:
        users.update({"username":user},{"$set":{"active":False}})
        return True
    else:
        return False

    
##### Session #####

def setSession(user):
    session = randomString()
    sessions.insert({"session":session,"user":user})
    return session

def remSession(session):
    print session
    res = sessions.find_one({"session":session})
    print res
    if res != None:
        sessions.remove({"session":session})
        return res["user"]
    else:
        return None

def getSession(session):
    return sessions.find_one({"session":session})
                       
    
##### Friend #####
    
def isFriend(target, src):
    target = user_db.find_one({"username": target})
    print target["friends"]
    return (src in target["friends"]) or (src in target["requests"])  

def addFriendReq(target, src):
    if not isFriend(target, src):
        users.update({"username":target}, {"$push": {"requests": src}})
        return True
    else:
        return False

def acceptFriendReq(src, requester):
    user = users.findOne({"username":src})
    if requester in user["requests"]:
        #Remove from requests
        users.update({"username":src},{"$pop":{"requests":requester}})
        #Update friends
        users.update({"username":src},{"$push":{"friends":requester}})
        users.update({"username":requester},{"$push":{"friends":src}})
        return True
    else:
        return False
    

###### Groups ######

def createGroup(admin, name):
    groupID = randomString()
    newGroup = {"groupID":groupID, "name":name, "admin":admin, "members": [admin]}
    groups.insert(newGroup)
    users.update({"username":admin}, {"$push":{"groups":groupID}})
    return groupID

def isGroupAdmin(user, groupID):
    group = groups.find_one({"groupID":groupID})
    return group != None and group["admin"] == user
    
def addGroupMember(admin, groupID, newUser):
    if isGroupAdmin(admin, groupID):
        groups.update({"groupID":groupID}, {"$push":{"members":newUser}})
        users.update({"username":newUser}, {"$push":{"groups":groupID}})
        return True
    else:
        return False
    
def remFromGroup(admin, groupID, user):
    if admin != user and isGroupAdmin(admin, groupID):
        groups.update({"groupID":groupID}, {"$pop":{"members":newUser}})
        users.update({"username":newUser}, {"$pop":{"groups":groupID}})
        return True
    else:
        return False
    
def getGroups(user):
    res = users.find_one({"username":user})
    if res != None:
        res = res["groups"]
    return res

def changeGroupOwner(admin, groupID, newAdmin):
    if isGroupAdmin(admin, groupID):
        groups.update({"groupID":groupID}, {"$set":{"owner":newOwner}})
        return True
    else:
        return False
    

#### Other ####

def pri(selected_db):
    for entry in selected_db.find():
        print entry


def randomString():
    return str(hex(int(time.time()*1000))[2:] + '-' + hex(random.randint(0, 0x7FFFFFFF))[2:])
