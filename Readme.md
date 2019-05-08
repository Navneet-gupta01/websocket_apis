# Assingment

## Websocket Apis

### Run Project
```scala
	git clone git@github.com:Navneet-gupta01/websocket_apis.git
	cd websocket_apis
	sbt run
```

## Use Apis
ws://localhost:9090/ws

## As Admin
```scala
	{"$type": "login", "username": "admin", "password": "admin"}  
		// Response {"user_type":{"$type":"AdminT"},"$type":"login_successful"}  
	{"$type": "subscribe_tables"}   
		// Response {"tables":[],"$type":"table_list"}  
	{"$type": "add_table", "after_id": 1, "table" : {"name": "tsdsd", "participants": 4}}  
		// Response {"after_id":1,"table":{"id":1,"name":"tsdsd","participants":4},"$type":"table_added"}  
	{"$type": "update_table", "table" : {"id": 1, "name": "tsdsd", "participants": 6}}  
		// Response{"table":{"id":1,"name":"tsdsd","participants":6},"$type":"table_updated"}  
	{"$type": "add_table", "after_id": 2, "table" : {"name": "tsdsd2", "participants": 4}}  
		// Response {"after_id":2,"table":{"id":2,"name":"tsdsd2","participants":4},"$type":"table_added"}  

	{"$type": "subscribe_tables"}  
		// Response  {"tables":[{"id":2,"name":"tsdsd2","participants":4},{"id":1,"name":"tsdsd","participants":6}],"$type":"table_list"}
	{"$type": "remove_table", "id": 2}
		// Response {"id":2,"$type":"table_removed"}  
	{"$type": "subscribe_tables"}  
		// Response {"tables":[{"id":1,"name":"tsdsd","participants":6}],"$type":"table_list"}  
```

## As User
```scala
        {"$type": "login", "username": "user1234", "password": "password1234"}  
		// Response {"user_type":{"$type":"UserT"},"$type":"login_successful"}
        {"$type": "subscribe_tables"}  
		// Response {"tables":[],"$type":"table_list"}  
        {"$type": "add_table", "after_id": 1, "table" : {"name": "tsdsd", "participants": 4}}  
		// Response {"$type":"not_authorized"}  
        {"$type": "update_table", "table" : {"id": 1, "name": "tsdsd", "participants": 6}}  
		// Response {"$type":"not_authorized"}  
        {"$type": "add_table", "after_id": 2, "table" : {"name": "tsdsd2", "participants": 4}}  
		// Response {"$type":"not_authorized"}
        {"$type": "subscribe_tables"}  
		// Response {"tables":[{"id":1,"name":"tsdsd","participants":6}],"$type":"table_list"}
        {"$type": "remove_table", "id": 2}
        {"$type": "subscribe_tables"}
```
