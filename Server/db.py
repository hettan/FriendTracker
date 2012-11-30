#!/usr/bin/python

from pymongo import Connection
import time,random,datetime

from gcm import GCM

connection = Connection('localhost', 27017) #DB connection
db = connection.test #Main db
users = db.users #SubDB - Userdata
sessions = db.sessions #SubDB - Current sessions
groups = db.groups #SubDB Current groups

API_KEY = "AIzaSyDkH-XZw5aGxnFw-sHHKeMr2Iy4Ht--O4U" #Application key for google API
gcm = GCM(API_KEY)

#### Send formats ####

#Error-response begins with 0
def error(msg):
    return "0"+msg+"\n"

def ok():
    return "1\n"

#OK-response begins with 1
def ok(data):
    return "1"+data+"\n"

#### Account Management #####

def register(username, password, mail):
    if users.find_one({"username": username}) == None:
        new_user = {"username": username, "password": password, "mail": mail,
                    "friends":[], "active":False, "requests":[], "groups":[],
                    "pos":{"lat":0,"lon":0}, "pushID": "", "friendsMod":0}
        users.insert(new_user)
        return ok("User was successfully registred!")
    
    else:
        return error("Username is already in use!")

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

def login(username, password, pushID):
    user = users.find_one({"username": username})
    if user != None and user["password"] == password:
        users.update({"username":user},{"$set":{"active":True}})
        users.update({"username":user},{"$set":{"pushID":pushID}})
        return ok(setSession(username))
    
    else:
        return error("Wrong username or password!")

def logoff(session):
    user = remSession(session)
    if user != None:
        users.update({"username":user},{"$set":{"active":False}})
        return ok("")
    
    else:
        return error("Session timeout")
    
##### Session #####

def setSession(username):
    session = randomString()
    sessions.insert({"session":session,"user":username})
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
    
def isFriend(src,target):
    target = users.find_one({"username": target})
    return (src in target["friends"]) or (src in target["requests"])  

def addFriendReq(src, target):
    if not isFriend(src,target):
        time = datetime.datetime.now()
        req = {"requester":src, time:time.strftime("%m-%d-%H-%M")} #Month-Day-Hour-Minute
        users.update({"username":target}, {"$push": {"requests": req}})
        pushReq(target,{"type":"friend","data":src})
        return ok("")
    
    else:
        return error("User is already friend with you or you've aldready made a friend request!")

def acceptFriendReq(src, requester):
    #Remove from requests
    users.update({"username":src},{"$pop":{"requests":{"username":requester}}})
    
    #Update friends
    users.update({"username":src},{"$push":{"friends":requester}})
    users.update({"username":requester},{"$push":{"friends":src}})
    
    #Update modified
    users.update({"username":src},{"$set":{"friendsMod":time.time()}})
    users.update({"username":requester},{"$set":{"friendsMod":time.time()}})

def getFriendReq(username):
    user = users.find_one({"username":username})
    return ok(str(user["requests"]))

#Returns both friends and requests
def getFriends(username):
    user = users.find_one({"username":username})

    #Flag active friends
    friends = []
    for friend in user["friends"]:
        active = users.find_one({"username":friend})["active"]
        friends.append({"username":friend, "active":active})

    #Sort
    friends = sorted(friends, key=lambda k: k["username"])
    requests = sorted(user["requests"], key=lambda k: k["requester"]) 
    return ok(str({"friends":friends, "requests":requests}))

def getFriendsIfMod(username, ts):
    user = users.find_one({"username":username})
    if user["friendsMod"] != ts:
        return ok(str({"friends":user["friends"], "timestamp": user["friendsMod"]}))
    
    else:
        return error("No change")

def userSearch(query):
      regexp = "(?i).*(" + query + ")+.*"; #Gets all users that contains the phrase in their username
      search_res = users.find({"username":{"$regexp":regexp}})
      
      res = []
      for user in search_res:
          res.push(user["username"])

          
      if len(res) > 0:
          res = sorted(res) 
          return ok(str(res))
      else:
          return error("No user found")

###### Groups ######

def createGroup(admin, name):
    groupID = randomString()
    newGroup = {"groupID":groupID, "name":name, "admin":admin, "members": [admin], "rallypoints": []}
    groups.insert(newGroup)
    users.update({"username":admin}, {"$push":{"groups":groupID}})
    return ok("")

def isGroupAdmin(username, groupID):
    group = groups.find_one({"groupID":groupID})
    return group != None and group["admin"] == username
    
def addGroupMember(username, groupID, newUser):
    if isGroupAdmin(username, groupID):
        groups.update({"groupID":groupID}, {"$push":{"members":newUser}})
        users.update({"username":newUser}, {"$push":{"groups":groupID}})
        return ok("")
    
    else:
        return error("You're not the group admin!")
    
def remFromGroup(username, groupID, target):
    if not isGroupAdmin(username, groupID):
        return error("You're not the admin of this group!")
    
    elif username == target:
        return error("Can't remove yourself from group while admin!")
    
    else:
        groups.update({"groupID":groupID}, {"$pop":{"members":target}})
        users.update({"username":target}, {"$pop":{"groups":groupID}})
        return ok("")

def leaveGroup(username, groupID):
    if not isGroupAdmin(username, groupID):
        groups.update({"groupID":groupID}, {"$pop":{"members":user}})
        users.update({"username":username}, {"$pop":{"groups":groupID}})
        return ok("")
    
    else:
        return error("You can't leave group while admin, assign another user as group admin first.")
    
    
def getGroups(username):
    res = users.find_one({"username":username})
    if res != None:
        #Sort
        res = sorted(res["groups"], key=lambda k: k["username"]) 
        return ok(str(res))
    
    else:
        return error("Can't find user in db")

def getGroupInfo(username, groupID):
    group = groups.find_one({"groupID":groupID})
    if group == None:
        return error("Group not found!")
    
    elif user not in group["members"]:
        return error("You're not a member of this group!")
    
    else:
        data = {"name":group["name"], "isAdmin":(group["admin"] == username), "members":group["members"]}
        return ok(str(data))
    
def changeGroupOwner(username, groupID, newAdmin):
    if isGroupAdmin(username, groupID):
        groups.update({"groupID":groupID}, {"$set":{"admin":newAdmin}})
        return ok("")
    
    else:
        return error("You're not the owner of this group!")

def addRallyPoint(username, groupID, pos, text):
    group = groups.find_one({"groupID":groupID})
    if group == None:
        return error("Group not found!")
    
    elif username not in group["members"]:
        return error("You're not a member of this group!")
    
    else:
        #Dont know if this works, only one rallypoint per user in each group is allowed
        groups.update({"groupID":groupID}, {"$pop", {"rallypoints": {"created_by":username}}})
        
        rallyPoint = {"created_by":username, "pos":pos, "text":text}
        groups.update({"groupID":groupID},{"$push", {"rallypoints":rallyPoint}})
        return ok("")

def remRallyPoint(username, groupID):
    group = groups.find_one({"groupID":groupID})
    if group == None:
        return error("Group not found!")
    
    elif username not in group["members"]:
        return error("You're not a member of this group!")
    
    else:
        #Dont know if this works
        groups.update({"groupID":groupID}, {"$pop", {"rallypoints": {"created_by":username}}})
        return ok("")

def getRallyPoints(username, groupID):
    group = groups.find_one({"groupID":groupID})
    if group == None:
        return error("Group not found!")
    
    elif username not in group["members"]:
        return error("You're not a member of this group!")
    
    else:
        return ok(str(group["rallypoints"]))

    
#### Position ####
    
def setPos(username, lat, lon):
    pos = {"lat":lat,"lon":lon}
    users.update({"username":username}, {"$set":{"pos":pos}})
    return ok("")

def getPos(username):
    user = users.find_one({"username":username})
    if user != None:
        return ok(str(user["pos"]))
    
    else:
        return error("User not found")

def getGroupPos(username, groupID):
    group = groups.find_one({"groupID":groupID})
    if group == None:
        return error("Group not found!")
    
    elif username not in group["members"]:
        return error("You're not a member of this group!")
    
    else:
        positions = []
        for member in group["members"]:
            pos = user.find_one({"username":member})
            positions.append({"username": member, "pos":pos})
            
        return ok(str(positions))

def getFriendsPos(username):
    user = user.find_one({"username":username})
    if user == None:
        return error("User not in db!")
    
    else:
        positions = []
        for friend in user["friends"]:
            pos = user.find_one({"username":friend})
            positions.append({"username": friend, "pos":pos})
            
        return ok(str(positions))

    
#### Push ####

def registerPush(username, pushID):
    users.update({"username": username}, {"$set": {"pushID": pushID}})
    return ok("")

def removePush(username):
    users.update({"username": username}, {"$set": {"pushID": ""}})
    return ok("")

def pushReq(target,data):
    pushID = users.find_one({"username":target})["pushID"]
    if pushID != "":
        response = gcm.json_request(registration_ids=pushID, data=data)
        print "push sent to "+ pushID +"\n" + response
    
#### Other ####

def pri(selected_db):
    for entry in selected_db.find():
        print entry

def randomString():
    return str(hex(int(time.time()*1000))[2:] + '-' + hex(random.randint(0, 0x7FFFFFFF))[2:])
