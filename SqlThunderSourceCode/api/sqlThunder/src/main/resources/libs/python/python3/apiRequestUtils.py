import os
import uuid
import requests
#pip install requests
class ApiRequestUtils(object):

    @staticmethod
    def postRequest(apiPath, queryParam, attachmentPath, attachmentFileName):
        attachment = {attachmentPath: open(attachmentFileName,'rb')}
        response = requests.post(apiPath, files=attachment, params=queryParam)
        print(response.json())


    @staticmethod
    def postRequest(apiPath, queryParam):
        response = requests.post(apiPath, params=queryParam)
        print(response.json())
        
    @staticmethod
    def getRequest(apiPath, queryParam):
        response = requests.post(apiPath, params=queryParam)    
        print(response.json())