#!/usr/bin/python

from pymongo import Connection
import time,random

connection = Connection('localhost', 27017)
db = connection.test
users = db.users
sessions = db.sessions
groups = db.groups


#### Send formats ####

def error(msg):
    return "0"+msg+"\n"

def ok():
    return "1\n"

def ok(data):
    return "1"+data+"\n"

#derpaderp
#### Account Management #####

def register(username, password, mail):
    if users.find_one({"username": username}) == None:
        new_user = {"username": username, "password": password, "mail": mail,
                    "friends":[], "active":False, "requests":[], "groups":[],
                    "pos":[0,0], "pushID": None}
        users.insert(new_user)
        #pri(users)
        return ok("User was successfully registred!")
    else:
        return error("Username is already in use!")

def login(username, password):
    user = users.find_one({"username": username})
    if user != None and user["password"] == password:
        users.update({"username":user},{"$set":{"active":True}})
        return ok(setSession(username))
    else:
        return error("Wrong username or password!")

def logoff(session):
    user = remSession(session)
    if user != None:
        users.update({"username":user},{"$set":{"active":False}})
        return ok()
    else:
        return error("Session timeout")

def changePassword(username, oldPassword, newPassword):
    user = users.find_one({"username": username});
    if user != None:
        if user["password"] == oldPassword:
            users.update({"username":username},{"$set":{"password":newPassword}})
            return ok("Password changed!")
        else:
            return error("Wrong password!")
    else:
        return error("Session timeout")

    
##### Session #####

def setSession(user):
    session = randomString()
    sessions.insert({"session":session,"user":user})
    return session

def remSession(session):
    res = sessions.find_one({"session":session})
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
    return (src in target["friends"]) or (src in target["requests"])  

def addFriendReq(target, src):
    if not isFriend(target, src):
        users.update({"username":target}, {"$push": {"requests": src}})
        return ok()
    else:
        return error("User is already friend with you or you've aldready made a friend request!")

def acceptFriendReq(src, requester):
    user = users.findOne({"username":src})
    if requester in user["requests"]:
        #Remove from requests
        users.update({"username":src},{"$pop":{"requests":requester}})
        #Update friends
        users.update({"username":src},{"$push":{"friends":requester}})
        users.update({"username":requester},{"$push":{"friends":src}})
        return ok()
    else:
        return error("Request not found!")
    

###### Groups ######

def createGroup(admin, name):
    groupID = randomString()
    newGroup = {"groupID":groupID, "name":name, "admin":admin, "members": [admin], "rallypoints": []}
    groups.insert(newGroup)
    users.update({"username":admin}, {"$push":{"groups":groupID}})
    return ok()

def isGroupAdmin(user, groupID):
    group = groups.find_one({"groupID":groupID})
    return group != None and group["admin"] == user
    
def addGroupMember(admin, groupID, newUser):
    if isGroupAdmin(admin, groupID):
        groups.update({"groupID":groupID}, {"$push":{"members":newUser}})
        users.update({"username":newUser}, {"$push":{"groups":groupID}})
        return ok()
    else:
        return error("You're not the group admin!")
    
def remFromGroup(admin, groupID, user):
    if not isGroupAdmin(admin, groupID):
        return error("You're not the admin of this group!")
    elif admin == user:
        return error("Can't remove yourself from group while admin!")
    else:
        groups.update({"groupID":groupID}, {"$pop":{"members":newUser}})
        users.update({"username":newUser}, {"$pop":{"groups":groupID}})
        return ok()

def leaveGroup(user, groupID):
    groups.update({"groupID":groupID}, {"$pop":{"members":user}})
    users.update({"username":user}, {"$pop":{"groups":groupID}})
    return ok()
    
def getGroups(user):
    res = users.find_one({"username":user})
    if res != None:
        res = res["groups"]
        return ok(str(res))
    else:
        return error("Can't find user in db")

def getGroupInfo(user, groupID):
    group = groups.find_one({"groupID":groupID})
    if group == None:
        return error("Group not found!")
    elif user not in group["member"]:
        return error("You're not a member of this group!")
    else:
        data = {"name":group["name"], "isAdmin":(group["admin"] == user), "members":group["members"]}
        return ok(str(data))
    
def changeGroupOwner(admin, groupID, newAdmin):
    if isGroupAdmin(admin, groupID):
        groups.update({"groupID":groupID}, {"$set":{"owner":newOwner}})
        return ok()
    else:
        return error("You're not the owner of this group!")

def addRallyPoint(user, groupID, pos, text):
    group = groups.find_one({"groupID":groupID})
    if group == None:
        return error("Group not found!")
    elif user not in group["member"]:
        return error("You're not a member of this group!")
    else:
        #Dont know if this works
        groups.update({"groupID":groupID}, {"$pop", {"rallypoints": {"created_by":user}}})
        
        rallyPoint = {"created_by":user, "pos":pos, "text":text}
        groups.update({"groupID":groupID},{"$push", {"rallypoints":rallyPoint}})
        return ok()

def remRallyPoint(user, groupID):
    group = groups.find_one({"groupID":groupID})
    if group == None:
        return error("Group not found!")
    elif user not in group["member"]:
        return error("You're not a member of this group!")
    else:
        #Dont know if this works
        groups.update({"groupID":groupID}, {"$pop", {"rallypoints": {"created_by":user}}})
        return ok()

def getRallyPoints(user, groupID):
    group = groups.find_one({"groupID":groupID})
    if group == None:
        return error("Group not found!")
    elif user not in group["member"]:
        return error("You're not a member of this group!")
    else:
        return ok(str(group["rallypoints"]))

    
#### Position ####
    
def setPos(user, pos):
    users.update({"username":user}, {"$set":{"pos":pos}})
    return ok()

def getPos(user):
    user = users.find_one({"username":user})
    if user != None:
        return ok(str(user["pos"]))
    else:
        return error("User not found")

def getGroupPos(user, groupID):
    group = groups.findOne({"groupID":groupID})
    if group == None:
        return error("Group not found!")
    elif user not in group["member"]:
        return error("You're not a member of this group!")
    else:
        positions = {}
        for member in group["members"]:
            positions[member] = getPos(member)
        return ok(str(positions))

    
#### Push ####

def registerPush(username, pushID):
    users.update({"username": username}, {"$set": {"pushID": pushID}})
    return ok()

def removePush(username):
    users.update({"username": username}, {"$set": {"pushID": None}})
    return ok()
   
    
#### Other ####

def pri(selected_db):
    for entry in selected_db.find():
        print entry

def randomString():
    return str(hex(int(time.time()*1000))[2:] + '-' + hex(random.randint(0, 0x7FFFFFFF))[2:])
