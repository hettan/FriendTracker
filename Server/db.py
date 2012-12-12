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
        data = decode_dict(data)
    print "OK - Data="+str(data)
    return b"1"+str(data)+b"\n"

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
        users.update({b"username":username},{"$set":{b"active":True}})
        users.update({b"username":username},{"$set":{b"pushID":pushID}})
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
        return (src in target[b"friends"]) or ({b"requester":src, b"type":b"friend"} in target[b"requests"])  

def addFriendReq(src, target):
    if not isFriend(src,target):
        requests = users.find_one({b"username":target})[b"requests"]
        for req in requests:
            if req[b"requester"] == src and req[b"type"] == b"friend":
                return error(b"You've alreade made this request!")
        
        time = datetime.datetime.now()
        req = {b"requester":src, b"time":time.strftime("%m-%d-%H-%M"), b"type":b"friend"} #Month-Day-Hour-Minute
        users.update({b"username":target}, {"$push": {b"requests": req}})
        pushReq(target,{b"type":b"1", b"user":src})
        return ok(b"")
    
    else:
        return error("User is already friend with you or you've aldready made a friend request!")

def accepReq(src, requester, reqType):
    print "src="+ src + "req="+ requester
        
    if reqType == "group":
    #Get groupID
        requests = users.find_one({b"username":src})["requests"]
        for req in requests:
            if req["requester"] == requester and req["type"] == "group":
                groups.update({b"groupID":req[b"groupID"]}, {"$push":{b"members":src}})
                users.update({b"username":src}, {"$push":{b"groups":req[b"groupID"]}})

    elif reqType == "friend":
        #Update friends
        users.update({b"username":src},{"$push":{b"friends":requester}})
        users.update({b"username":requester},{"$push":{b"friends":src}})
                     
    #Remove from requests
    users.update({b"username":src},{"$pop":{b"requests":{b"username":requester, b"type":reqType}}})  
    
    #Update modified
    #users.update({b"username":src},{"$set":{b"friendsMod":time.time()}})
    #users.update({b"username":requester},{"$set":{b"friendsMod":time.time()}})
    return ok(b"")

def getFriendReq(username):
    user = users.find_one({b"username":username})
    return ok(filter(lambda req: req[b"type"]==b"friend", user[b"requests"]))

def getRequests(username):
    user = users.find_one({b"username":username})
    return ok(user[b"requests"])

def clearRequests(username):
    users.update({b"username":username},{"$set":{b"requests":[]}})
    return ok("")

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
    #out = decode_dict({b"friends":friends, b"requests":requests})
    return ok({b"friends":friends, b"requests":requests})

#Returns all friends not in group
def getFriendsNotGroup(username, groupID):
    user = users.find_one({b"username":username})
    groupMembers = groups.find_one({b"groupID":groupID})["members"]

    friends = []
    for friend in user[b"friends"]:
        if friend not in groupMembers:
            friends.append(friend)
    return ok(friends)


def getFriendsIfMod(username, ts):
    user = users.find_one({b"username":username})
    if user[b"friendsMod"] != ts:
        return ok({b"friends":user[b"friends"], b"timestamp": user[b"friendsMod"]})
    
    else:
        return error(b"No change")

def userSearch(username, query):
      regexp = "(?i).*(" + query + ")+.*"; #Gets all users that contains the phrase in their username
      search_res = users.find({b"username":{"$regex":regexp}})
      print "query = "+query
      res = []
      for user in search_res:
          if user["username"] != username:
              res.append(user[b"username"])
          
      if len(res) > 0:
          res = sorted(res) 
          return ok(res)
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

        time = datetime.datetime.now()
        req = {"groupID": groupID, b"requester":username, b"time":time.strftime("%m-%d-%H-%M"), b"type":b"group"} #Month-Day-Hour-Minute
        users.update({b"username":newUser}, {"$push": {b"requests": req}})
        groupName = groups.find_one({b"groupID":groupID})["name"]
        pushReq(newUser,{b"type":b"2", b"user":username, b"group":groupName})
        
        #groups.update({b"groupID":groupID}, {"$push":{b"members":newUser}})
        #users.update({b"username":newUser}, {"$push":{b"groups":groupID}})
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
    

def delGroup(username, groupID):
    if isGroupAdmin(username, groupID):
        group = groups.find_one({b"groupID":groupID})
        for member in group["members"]:
            users.update({b"username":member}, {b"$pop":{b"groups":groupID}})
        groups.remove({b"groupID":groupID})
        return ok(b"")
    else:
        return error(b"You must be admin to remove group")
    
def getGroups(username):
    res = users.find_one({b"username":username})
    if res != None:
        #Sort
        #res = sorted(res["groups"], key=lambda k: k["username"])
        #out = decode_list(res[b"groups"])
        return ok(res[b"groups"])
    
    else:
        return error(b"Can't find user in db")

def getGroupInfo(username, groupID):
    group = groups.find_one({b"groupID":groupID})
    if group == None:
        return error(b"Group not found!")
    
    elif username not in group[b"members"]:
        return error(b"You're not a member of this group!")
    
    else:
        data = {b"name":group[b"name"], b"admin":group[b"admin"], b"members":group[b"members"]}
        return ok(data)
    
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
        return ok(group[b"rallypoints"])

    
#### Position ####
    
def setPos(username, lat, lon):
    pos = {b"lat":lat,b"lon":lon}
    users.update({b"username":username}, {"$set":{b"pos":pos}})
    return ok(b"Dinmamma")

def getPos(username):
    user = users.find_one({b"username":username})
    if user != None:
        return ok(user[b"pos"])
    
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
            
        return ok(positions)

def getFriendsPos(username):
    user = users.find_one({b"username":username})
    if user == None:
        return error(b"User not in db!")
    
    else:
        positions = []
        for friend in user[b"friends"]:
            res = users.find_one({b"username":friend})
            positions.append({b"username": friend, b"pos":res[b"pos"], b"status":res[b"status"]})
        return ok(positions)


def getPositions(username, showFriends, groupID):
    user = users.find_one({b"username":username})
    data = []
    if showFriends:
        for friend in user["friend"]:
            entry = {b"type": b"friend", b"username":friend}
            entry[b"pos"] = users.find_one({b"username":friend})[b"pos"]
            data.append(entry)
    if groupID != "":
        group = groups.find_one({b"groupID":groupID})
        for member in group[b"members"]:
            if member != username:
                 entry = {b"type": b"group", b"groupName":group[b"name"], b"username":member}
                 data.append(entry)
    return ok(data)
    
#### Push ####

def registerPush(username, pushID):
    users.update({b"username": username}, {"$set": {b"pushID": pushID}})
    return ok(b"")

def removePush(username):
    users.update({b"username": username}, {"$set": {b"pushID": b""}})
    return ok(b"")

def pushReq(target,data):
    user = users.find_one({b"username":target})
    print user
    pushID = user[b"pushID"]
    print "pushID="+pushID
    if pushID != b"":
        response = gcm.json_request(registration_ids=[pushID], data=data)
    else:
        print "pushID missing"

#### Other ####

def pri(selected_db):
    for entry in selected_db.find():
        print entry

def randomString():
    return str(hex(int(time.time()*1000))[2:] + '-' + hex(random.randint(0, 0x7FFFFFFF))[2:])
