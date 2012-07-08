package org.exobel.routerkeygen.thomsonGenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class FileOutputManager {
	 	Map<String, FileOutput> filesMap;
   	 	
   	 	public FileOutputManager(){
		   	filesMap = new HashMap<String, FileOutput>();
   	 	}
   	 	
   	 	public void initAllFiles(){
	   	 	FileOutput fos;
	        String file = "00.dat";
	        for(int a = 0; a < AlphabetCodes.charect.length; a++)
	        {
	            for(int b = 0; b < AlphabetCodes.charect.length; b++)
	            { 
	            	file = AlphabetCodes.charect[a] + AlphabetCodes.charect[b] + ".dat";
					try {
			            fos = new FileOutput( file);
			            filesMap.put(file, fos);
			        } catch (FileNotFoundException e) {
			            System.out.println("Error!" + e);
			            return;
			        }
	            }
	        }
	 	}
   	 
   	 	public void addFile(String file ){
   	 		try {
				FileOutput fos = new FileOutput( file);
				filesMap.put(file, fos);
			} catch (FileNotFoundException e) {
				System.out.println("Error!" + e);
	            return;
			}
   	 	}
	 	
	 	public void sendFile(String file , byte [] bytes  , int len){
   	 		if ( !filesMap.containsKey(file) )
   	 			return;
   	 		
   	 		try {
				filesMap.get(file).add(bytes , len);
			} catch (IOException e) {
				System.out.println("Error!" + e);
	            return;
			}
	 	}
	 	
	 	public void sendFile(String file , byte [] bytes ){
   	 		if ( !filesMap.containsKey(file) )
   	 			return;
   	 		
   	 		try {
				filesMap.get(file).add(bytes );
			} catch (IOException e) {
				System.out.println("Error!" + e);
	            return;
			}
	 	}
	 	
	 	public void sendFile(String file , byte  bytes ){
   	 		if ( !filesMap.containsKey(file) )
   	 			return;
   	 		try {
				filesMap.get(file).add(bytes);
			} catch (IOException e) {
				System.out.println("Error!" + e);
	            return;
			}
	 	}
		
		public void close(){
			Iterator<Entry<String, FileOutput>> it = filesMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, FileOutput> pairs = (Map.Entry<String, FileOutput>)it.next();
				try {
					pairs.getValue().close();
				} catch (IOException e) {
					System.out.println("Error!" + e);
		            return;
				}
			}
		}
		
		public void clearAll(){
			this.filesMap.clear();
		}
	 		

	 	private static class FileOutput{
	 		FileOutputStream fos;
	 		byte [] buffer;
	 		int offset;
	 		public FileOutput( String file ) throws FileNotFoundException{
	 			fos = new FileOutputStream(file);
	 			buffer = new byte[4096];
	 			offset = 0;
	 		}
	 		public void add ( byte [] bytes , int len ) throws IOException{
	 			for ( int i = 0 ; i < len ; ++i  )
	 			{
	 				if ( offset >= buffer.length )
	 				{
	 					fos.write(buffer);
	 					offset = 0;
	 				}
	 				buffer[offset] = bytes[i];
	 				offset++;
	 			}
	 		}
	 		public void add ( byte [] bytes ) throws IOException{
	 			for ( int i = 0 ; i < bytes.length ; ++i  )
	 			{
	 				if ( offset >= buffer.length )
	 				{
	 					fos.write(buffer);
	 					offset = 0;
	 				}
	 				buffer[offset] = bytes[i];
	 				offset++;
	 			}
	 		}
	 		
	 		public void add ( byte bytes ) throws IOException{
	 				if ( offset >= buffer.length )
	 				{
	 					fos.write(buffer);
	 					offset = 0;
	 				}
	 				buffer[offset] = bytes;
	 				offset++;
	 		}
	 		public void close() throws IOException{
	 			fos.write(buffer,  0 , offset);
	 			fos.close();
	 		}
	 	}
}
