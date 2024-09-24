class ScriptParam(object): 

	def __init__(self, scriptParamId, paramName, value):
        self.__scriptParamId = scriptParamId
        self.__paramName = paramName
		self.__value = value


    def get_scriptParamId(self):
        return self.__scriptParamId

    
    def set_scriptParamId(self, scriptParamId):
        self.__scriptParamId = scriptParamId
		
    def get_paramName(self):
        return self.__paramName
		
    def set_paramName(self, paramName):
        self.__paramName = paramName
		
    def get_value(self):
        return self.__value
		
    def set_value(self, value):
        self.value = value