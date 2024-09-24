
/**
 * 
 * 
The file app-config.json stores the initial SqlThunder nodes available in the cluster
sqlThunderHttp must be an array of SqlThunderHttp objects. Pls take a look at the object layout to add more machine 

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
    "baseUrl": "https://209.15.130.226:9099/sqlThunder",
    "webSocketsUrl": "wss://209.15.130.226:9099/sqlThunder/websocket",
    "sqlThunderHttp": [],
    "security":"GOOGLE",
    "googleClientId":"461543356180-tkf1ai1polfijgikh6c05nkdne3jn7pu.apps.googleusercontent.com"
}


NATIVE
{
    "baseUrl": "http://aliendesk:9099/sqlThunder",
    "webSocketsUrl": "ws://localhost:9099/sqlThunder/websocket",
    "sqlThunderHttp": [],
    "security":"NATIVE",
    "googleClientId":""
}


*/






