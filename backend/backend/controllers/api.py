from google.appengine.ext import ndb
import json
from random import shuffle

#This file is the source code for the backend server, which utilizes web2Py and Google App Engine

#Class is designed to help store and parse the ingredients info passed by the front-end request 
class Ingredient(ndb.Model):
    qty = ndb.StringProperty()
    measure = ndb.StringProperty()
    name = ndb.StringProperty()

#Class is designed to store the sandwich info passed by the front-end request. Contains an instance of the Ingredients class	
class SandwichStorage(ndb.Model):
    name = ndb.StringProperty()
    ingredients = ndb.JsonProperty()
    msg = ndb.TextProperty()
    userCreated = ndb.BooleanProperty()
    ingredientsObj = ndb.StructuredProperty(Ingredient, repeated=True)
    name_lower = ndb.StringProperty(repeated=True)
    ingredients_searchable_names = ndb.StringProperty(repeated=True) 
	#imgId = ndb.StringProperty()

#Clears the saved user recipes from the database	
def clear_user_recipes():
    pw = request.vars.pw
    if pw != "imsure":
        return response.json("bad clear")
    userSandwiches = SandwichStorage.query(SandwichStorage.userCreated == True)
    for sandwich in userSandwiches:
        sandwich.key.delete() #key is a database thing

    return response.json("cleared")

#Returns a list of sandwich recipes given a query. This is called from search() and get_user_recipes()
def get_result_list(qry):
    rslts = []
    for sandwich in qry:
        rslts.append(dict(
            name=sandwich.name,
            msg=sandwich.msg,
            ingredients=sandwich.ingredients,
            userCreated=sandwich.userCreated,
			#imgId=sandwich.imgId,
            ingredients_searchable_names = sandwich.ingredients_searchable_names
        ))
		
    return response.json(dict(results=rslts))

#Adds/stores a sandwich recipe passed from the front-end using the SandwichStorage class	
def add_sandwich():
    name = request.vars.name
    if name == "":
        return response.json("bad name")
    msg = request.vars.msg
    ingredients = json.loads(request.vars.ingredients)
	#imgId = request.vars.imgId

    ss = SandwichStorage()

    ingredients_searchable_names = []
    for ingredient in ingredients: #because variable length
        ss.ingredientsObj.append(Ingredient(qty=ingredient["qty"].encode("utf-8"),
                                            name=ingredient["name"],
                                            measure=ingredient["measure"],
                                         ))
        for word in ingredient["name"].split(" "):
            ingredients_searchable_names.append(word.lower())
        if len(ingredient["name"].split(" ")) > 1:
            ingredients_searchable_names.append(ingredient["name"].lower())
			
	ss.userCreated = True
    ss.name = name
    ss.name_lower = [word.lower() for word in name.split(" ")] #parse the name by spaces, lower case them for search, and index via an array
    ss.name_lower.append(name.lower()) #this is for phrases
    ss.ingredients_searchable_names = ingredients_searchable_names
    ss.msg = msg
	#ss.imgId = imgId
    ss.ingredients = ingredients

    ss.put()

    return response.json("added sandwich")

#Returns 15 random user recipes. This way, old and new sandwiches have equal chance of showing up in library
#and old sandwiches will not be buried
def get_user_recipes():
    userQry = SandwichStorage.query(SandwichStorage.userCreated == True).fetch() #fetch(15) would have got 15 most recent
    shuffle(userQry) #this is imported (python thing)
    userQry = userQry[:16] #getting the first 15

    return get_result_list(userQry)

#Searches for sandwiches given a query string based on sandwich name and ingredient names
def search():
    input = request.vars.q.strip().lower()
    limit = input[:-1] + chr(ord(input[-1]) + 1)
    searchQry = SandwichStorage.query(ndb.OR(
        ndb.AND(SandwichStorage.name_lower >= input, SandwichStorage.name_lower < limit),
        ndb.AND(SandwichStorage.ingredients_searchable_names >= input, SandwichStorage.ingredients_searchable_names < limit)
                              ))

    return get_result_list(searchQry)