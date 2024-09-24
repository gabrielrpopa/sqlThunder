import os
import uuid
from datetime import date, datetime

import tensorflow as tf
from tensorflow import keras

class FileUtils(object):

    UPLOAD_TEMP_FOLDER = 'C:\\Upload\\TempFiles'
    UPLOAD_PERM_FOLDER = 'C:\\Upload\\PermFiles'

    @staticmethod
    def createFolder(folderName):
        try:
            if not os.path.exists(folderName):
                os.mkdir(folderName)
                print("Directory " , folderName ,  " Created ")
            else:    
                print("Directory " , folderName ,  " already exists")
        except FileExistsError:
            print("Directory " , folderName ,  " already exists")

    @staticmethod
    def isFileAllowed(filename_, ALLOWED_EXTENSIONS):
        filename, file_extension = os.path.splitext(filename_)
        if file_extension in ALLOWED_EXTENSIONS:
            return str(uuid.uuid4()) +  file_extension
        else:
            return ''


    @staticmethod
    def isFileExtenssionAllowed(filename_, ALLOWED_EXTENSIONS):
        filename, file_extension = os.path.splitext(filename_)
        if file_extension in ALLOWED_EXTENSIONS:
            return True
        else:
            return False


    @staticmethod
    def returnExt(filename_):
        filename, file_extension = os.path.splitext(filename_)
        return file_extension

    @staticmethod
    def allowed_file_train(filename, ALLOWED_EXTENSIONS_TRAIN):
        return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS_TRAIN

    @staticmethod
    def delete_file(filename):
        if os.path.exists(filename):
            os.remove(filename)
            return True, ""
        else:
            err_str = "Can not delete the file as it doesn't exists"
            print(err_str)
            return False, err_str
 
    
    def save_model(model, folder, filename):
        print ('Saving model:')
        if not os.path.isdir(folder):
            os.mkdirs(folder)
        model.save(folder + '//' + filename)
 
        
        
        