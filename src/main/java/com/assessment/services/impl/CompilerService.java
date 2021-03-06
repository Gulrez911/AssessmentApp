package com.assessment.services.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Service;

import com.assessment.common.CompileData;
import com.assessment.common.CompileOutput;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CompilerService {

	
	public CompileOutput compile(CompileData compileData){
		 try {
			URL url = new URL("http://13.233.2.169:8088/compile"); 
			    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
			    connection.setDoOutput(true); 
			    connection.setInstanceFollowRedirects(false); 
			    connection.setRequestMethod("POST"); 
			    connection.setRequestProperty("Content-Type", "application/json"); 

			    OutputStream os = connection.getOutputStream(); 
			   
				ObjectMapper mapper = new ObjectMapper();
				String str = mapper.writeValueAsString(compileData);
				os.write(str.getBytes());
				os.flush();
				InputStream is = connection.getInputStream();
				byte bat[] = new byte[is.available()];
				is.read(bat);
				String op = new String(bat);
				CompileOutput compileOutput = mapper.readValue(op.getBytes(), CompileOutput.class);
			    int resCode = connection.getResponseCode(); 
			    compileOutput.setCode(""+resCode);
			    connection.disconnect();
			    return compileOutput;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CompileOutput compileOutput = new CompileOutput();
			compileOutput.setResponseCode("Unreachable");
			return compileOutput;
		} 
	}
}
