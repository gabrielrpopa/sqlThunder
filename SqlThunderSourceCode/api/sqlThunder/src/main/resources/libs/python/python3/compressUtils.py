import gzip
import zlib
import shutil
import base64

"""
Example:
x = CompressUtils.compressGZIP("abc")
y = CompressUtils.decompressGZIP(x)
print(y)
"""

#pip install requests
class CompressUtils(object):

    @staticmethod
    def compressGZIP(content):
        return gzip.compress(bytes(content, 'utf-8'))
        
    @staticmethod
    def decompressGZIP(str):
        return gzip.decompress(bytes(str, 'utf-8'))
    
    @staticmethod    
    def decompressZLIB(str):
        """Example: decompressed_data = CompressUtils.decompressZLIB(sys.argv[1]) """
        """ 
        // Java
        public static String compressZlibToPython(String data) throws IOException {  
            byte[] input = data.getBytes("UTF-8");

            // Compress the bytes
            byte[] output = new byte[100];
            Deflater compresser = new Deflater();
            compresser.setInput(input);
            compresser.finish();
            compresser.deflate(output);
            compresser.end();
            String ret = Base64.getEncoder().encodeToString(output);
            return ret; 
            
          }  
        """
        deflate_bytes=base64.decodebytes(str.encode())
        return zlib.decompress(deflate_bytes).decode("utf-8") 
        
    
        
   