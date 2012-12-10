#!/usr/bin/python

from pymongo import Connection
import time,random,datetime

from gcm import GCM

connection = Connection('127.0.0.1', 27017) #DB connection
db = connection.test #Main db
users = db.users #SubDB - Userdata
sessions = db.sessions #SubDB - Current sessions
groups = db.groups #SubDB Current groups

API_KEY = "AIzaSyDkH-XZw5aGxnFw-sHHKeMr2Iy4Ht--O4U" #Application key for google API
gcm = GCM(API_KEY)

#### Send formats ####

#Error-response begins with 0
def error(msg):
    return b"0"+msg+"\n"

def ok():
    return b"1\n"

#OK-response begins with 1
def ok(data):
    if isinstance(data,list):
        data = decode_list(data)
    elif isinstance(data,dict):
        data = decode_list(data)
        
    return b"1"+data+b"\n"

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

def decode_list(data):
    rv = []
    for item in data:
        if isinstance(item, unicode):
            item = item.encode('utf-8')
        elif isinstance(item, list):
            item = decode_list(item)
        elif isinstance(item, dict):
            item = decode_dict(item)
        rv.append(item)
    return rv

#### Account Management #####

def register(username, password, mail):
    if users.find_one({b"username": username}) == None:
        new_user = {b"username": username, b"password": password, b"mail": mail,
                    b"friends":[], b"active":False, b"requests":[], b"groups":[],
                    b"pos":{b"lat":0,b"lon":0}, "pushID": "", b"friendsMod":0, b"status":b""}
        users.insert(new_user)
        return ok(b"User was successfully registred!")
    
    else:
        return error(b"Username is already in use!")

def changePassword(username, oldPassword, newPassword):
    user = users.find_one({b"username": username})
    if user != None:
        if user[b"password"] == oldPassword:
            users.update({b"username":username},{"$set":{b"password":newPassword}})
            return ok(b"Password changed!")
        
        else:
            return error(b"Wrong password!")
        
    else:
        return error(b"Session timeout")

def login(username, password, pushID):
    user = users.find_one({b"username": username})
    if user != None and user[b"password"] == password:
        users.update({b"username":user},{"$set":{b"active":True}})
        users.update({b"username":user},{"$set":{b"pushID":pushID}})
        return ok(setSession(username))
    
    else:
        return error(b"Wrong username or password!")

def logoff(session):
    user = remSession(session)
    if user != None:
        users.update({b"username":user},{"$set":{b"active":False}})
        return ok(b"")
    
    else:
        return error(b"Session timeout")

def setStatus(username, status):
    users.update({b"username": username},{"$set":{b"status":status}})
    return ok(b"")
    
    
##### Session #####

def setSession(username):
    session = randomString()
    sessions.insert({b"session":session,b"user":username})
    return session

def remSession(session):
    res = sessions.find_one({b"session":session})
    if res != None:
        sessions.remove({b"session":session})
        return res[b"user"]
    
    else:
        return None

def getSession(session):
    return sessions.find_one({b"session":session})
                       
    
##### Friend #####
    
def isFriend(src,target):
    print target
    target = users.find_one({b"username": target})
    if target == None:
        return True
    else:
        return (src in target[b"friends"]) or (src in target[b"requests"][b"requester"])  

def addFriendReq(src, target):
    if not isFriend(src,target):
        time = datetime.datetime.now()
        req = {b"requester":src, b"time":time.strftime("%m-%d-%H-%M")} #Month-Day-Hour-Minute
        users.update({b"username":target}, {"$push": {b"requests": req}})
        #pushReq(target,{"type":"friend","data":src})
        return ok(b"")
    
    else:
        return error("User is already friend with you or you've aldready made a friend request!")

def acceptFriendReq(src, requester):
    print "src="+ src + "req="+ requester
    #Remove from requests
    users.update({b"username":src},{"$pop":{b"requests":{b"username":requester}}})
    
    #Update friends
    users.update({b"username":src},{"$push":{b"friends":requester}})
    users.update({b"username":requester},{"$push":{b"friends":src}})
    
    #Update modified
    users.update({b"username":src},{"$set":{b"friendsMod":time.time()}})
    users.update({b"username":requester},{"$set":{b"friendsMod":time.time()}})
    return ok(b"")

def getFriendReq(username):
    user = users.find_one({b"username":username})
    return ok(str(user[b"requests"]))

#Returns both friends and requests
def getFriends(username):
    user = users.find_one({b"username":username})

    #Flag active friends
    friends = []
    for friend in user[b"friends"]:
        #active = users.find_one({"username":friend})["active"]
        active = True
        friends.append({b"username":friend, b"active":active})
    #friends.append({"username":"notOnline", "active":False})
    #Sort
    requests = user[b"requests"]
    #friends = sorted(friends, key=lambda k: k["username"])
    #requests = sorted(user["requests"], key=lambda k: k["requester"])
    out = decode_dict({b"friends":friends, b"requests":requests})
    return ok(str(out))

def getFriendsIfMod(username, ts):
    user = users.find_one({b"username":username})
    if user[b"friendsMod"] != ts:
        return ok(str({b"friends":user[b"friends"], b"timestamp": user[b"friendsMod"]}))
    
    else:
        return error(b"No change")

def userSearch(query):
      regexp = "(?i).*(" + query + ")+.*"; #Gets all users that contains the phrase in their username
      search_res = users.find({b"username":{"$regex":regexp}})
      print query
      print "d" + str(search_res)
      res = []
      for user in search_res:
          res.append(user[b"username"])

          
      if len(res) > 0:
          res = sorted(res) 
          return ok(str(res))
      else:
          return error(b"No user found")

###### Groups ######

def createGroup(admin, name):
    groupID = randomString()
    newGroup = {b"groupID":groupID, b"name":name, b"admin":admin, b"members": [admin], b"rallypoints": []}
    groups.insert(newGroup)
    users.update({b"username":admin}, {"$push":{b"groups":{b"name": name,b"groupID":groupID}}})
    return ok("")

def isGroupAdmin(username, groupID):
    group = groups.find_one({b"groupID":groupID})
    return group != None and group[b"admin"] == username
    
def addGroupMember(username, groupID, newUser):
    if isGroupAdmin(username, groupID):
        groups.update({b"groupID":groupID}, {"$push":{b"members":newUser}})
        users.update({b"username":newUser}, {"$push":{b"groups":groupID}})
        return ok(b"")
    
    else:
        return error(b"You're not the group admin!")
    
def remFromGroup(username, groupID, target):
    if not isGroupAdmin(username, groupID):
        return error(b"You're not the admin of this group!")
    
    elif username == target:
        return error(b"Can't remove yourself from group while admin!")
    
    else:
        groups.update({b"groupID":groupID}, {"$pop":{b"members":target}})
        users.update({b"username":target}, {"$pop":{b"groups":groupID}})
        return ok(b"")

def leaveGroup(username, groupID):
    if not isGroupAdmin(username, groupID):
        groups.update({b"groupID":groupID}, {"$pop":{b"members":user}})
        users.update({b"username":username}, {"$pop":{b"groups":groupID}})
        return ok(b"")
    
    else:
        return error(b"You can't leave group while admin, assign another user as group admin first.")
    
    
def getGroups(username):
    res = users.find_one({b"username":username})
    if res != None:
        #Sort
        #res = sorted(res["groups"], key=lambda k: k["username"])
        out = decode_list(res[b"groups"])
        return ok(str(out))
    
    else:
        return error(b"Can't find user in db")

def getGroupInfo(username, groupID):
    group = groups.find_one({b"groupID":groupID})
    if group == None:
        return error(b"Group not found!")
    
    elif user not in group[b"members"]:
        return error(b"You're not a member of this group!")
    
    else:
        data = {b"name":group[b"name"], b"isAdmin":(group[b"admin"] == username), b"members":group[b"members"]}
        return ok(str(data))
    
def changeGroupOwner(username, groupID, newAdmin):
    if isGroupAdmin(username, groupID):
        groups.update({b"groupID":groupID}, {"$set":{b"admin":newAdmin}})
        return ok(b"")
    
    else:
        return error(b"You're not the owner of this group!")

def addRallyPoint(username, groupID, pos, text):
    group = groups.find_one({b"groupID":groupID})
    if group == None:
        return error(b"Group not found!")
    
    elif username not in group[b"members"]:
        return error(b"You're not a member of this group!")
    
    else:
        #Dont know if this works, only one rallypoint per user in each group is allowed
        groups.update({b"groupID":groupID}, {"$pop": {b"rallypoints": {b"created_by":username}}})
        
        rallyPoint = {b"created_by":username, "pos":pos, b"text":text}
        groups.update({b"groupID":groupID},{"$push": {b"rallypoints":rallyPoint}})
        return ok(b"")

def remRallyPoint(username, groupID):
    group = groups.find_one({b"groupID":groupID})
    if group == None:
        return error(b"Group not found!")
    
    elif username not in group[b"members"]:
        return error(b"You're not a member of this group!")
    
    else:
        #Dont know if this works
        groups.update({b"groupID":groupID}, {"$pop": {b"rallypoints": {b"created_by":username}}})
        return ok(b"")

def getRallyPoints(username, groupID):
    group = groups.find_one({b"groupID":groupID})
    if group == None:
        return error(b"Group not found!")
    
    elif username not in group[b"members"]:
        return error(b"You're not a member of this group!")
    
    else:
        return ok(str(group[b"rallypoints"]))

    
#### Position ####
    
def setPos(username, lat, lon):
    pos = {b"lat":lat,b"lon":lon}
    users.update({b"username":username}, {"$set":{b"pos":pos}})
    return ok(b"Dinmamma")

def getPos(username):
    user = users.find_one({b"username":username})
    if user != None:
        return ok(str(user[b"pos"]))
    
    else:
        return error(b"User not found")

def getGroupPos(username, groupID):
    group = groups.find_one({b"groupID":groupID})
    if group == None:
        return error(b"Group not found!")
    
    elif username not in group[b"members"]:
        return error(b"You're not a member of this group!")
    
    else:
        positions = []
        for member in group[b"members"]:
            res = users.find_one({b"username":member})[b"pos"]
            positions.append({b"username": member, b"pos":res[b"pos"], b"status":res[b"status"]})
            
        return ok(str(positions))

def getFriendsPos(username):
    user = user.find_one({b"username":username})
    if user == None:
        return error(b"User not in db!")
    
    else:
        positions = []
        for friend in user[b"friends"]:
            res = users.find_one({b"username":friend})
            positions.append({b"username": friend, b"pos":res[b"pos"], b"status":res[b"status"]})
            
        return ok(str(positions))

    
#### Push ####

def registerPush(username, pushID):
    users.update({b"username": username}, {"$set": {b"pushID": pushID}})
    return ok(b"")

def removePush(username):
    users.update({b"username": username}, {"$set": {b"pushID": b""}})
    return ok(b"")

def pushReq(target,data):
    pushID = users.find_one({b"username":target})[b"pushID"]
    if pushID != b"":
        response = gcm.json_request(registration_ids=pushID, data=data)
        print b"push sent to "+ pushID +b"\n" + response
    
#### Other ####

def pri(selected_db):
    for entry in selected_db.find():
        print entry

def randomString():
    return str(hex(int(time.time()*1000))[2:] + '-' + hex(random.randint(0, 0x7FFFFFFF))[2:])
