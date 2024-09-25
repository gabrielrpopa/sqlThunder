
/**
 * 
 * 
The file app-config.json stores the initial SqlThunder nodes available in the cluster
sqlThunderHttp must be an array of SqlThunderHttp objects. Pls take a look at the object layout to add more machines 

{
    "baseUrl": "http://aliendesk:9094/sqlThunder",
    "webSocketsUrl": "ws://aliendesk:7071/chat",
    "sqlThunderHttp": []
}




Example
{
    "baseUrl": "http://aliendesk:9094/sqlThunder",
    "webSocketsUrl": "ws://aliendesk:7071/chat",
    "sqlThunderHttp": [
        {
            "baseUrl", "machine1:9094/sqlThunder",
            "urlWebsocket", "ws://machine1:7071/chat",
            "load", "0",
            "isActive", "Y"

        }, 
        {
            "baseUrl", "machine2:9094/sqlThunder",
            "urlWebsocket", "ws://machine2:7071/chat",
            "load", "0",
            "isActive", "Y"
        }

    ]
}

GOOGLE
{
    "baseUrl": "https://XXX.XX.XXX.XXX:9094/sqlThunder",
    "webSocketsUrl": "wss://XXX.XX.XXX.XXX:7071/streaming",
    "sqlThunderHttp": [],
    "security":"GOOGLE",
    "googleClientId":"ADD_YOUR_CLIENT_ID_HERE.apps.googleusercontent.com"
}


NATIVE
{
    "baseUrl": "http://localhost:9099/sqlThunder",
    "webSocketsUrl": "ws://localhost:9099/sqlThunder/websocket",
    "sqlThunderHttp": [],
    "security":"NATIVE",
    "googleClientId":""
}


*/






