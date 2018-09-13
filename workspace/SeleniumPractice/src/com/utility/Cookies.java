package com.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.StringTokenizer;
import org.openqa.selenium.WebDriver;

import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;

public class Cookies {

	WebDriver driver;
	Logger Add_Log;
	
	public Cookies(WebDriver drive, Logger add_Log){
		driver = drive;
		Add_Log = add_Log;
	}
	
	public void Storingcookie()
	{
		File file = new File("Cookies.data");		
		Add_Log.info("New file is created");
		try		
		{		
			// Delete old file if exists
			file.delete();		
			file.createNewFile();			
			FileWriter fileWrite = new FileWriter(file);							
			BufferedWriter Bwrite = new BufferedWriter(fileWrite);							
			// loop for getting the cookie information 	
			
			if(driver == null){
				System.out.println("DRIVER IS NULL");
			}else{
				System.out.println("DRIVER IS NOT NULL");
			}
			
			for(Cookie ck : driver.manage().getCookies())							
			{		
				Bwrite.write((ck.getName()+";"+ck.getValue()+";"+ck.getDomain()+";"+ck.getPath()+";"+ck.getExpiry()+";"+ck.isSecure()));																									
				Bwrite.newLine();	
			}		
			Bwrite.flush();			
			Bwrite.close();			
			fileWrite.close();	
			Add_Log.info(" file is closed");
		}catch(Exception ex)					
		{		
			ex.printStackTrace();			
		}		
	}
	public void Loadcookies(WebDriver drive)
	{
		try{
			File file = new File("Cookies.data");							
			FileReader fileReader = new FileReader(file);							
			BufferedReader Buffreader = new BufferedReader(fileReader);							
			String strline;			
			while((strline=Buffreader.readLine())!=null){									
				StringTokenizer token = new StringTokenizer(strline,";");									
				while(token.hasMoreTokens()){					
					String name = token.nextToken();					
					String value = token.nextToken();					
					String domain = token.nextToken();					
					String path = token.nextToken();					
					Date expiry = null;					

					String val;			
					if(!(val=token.nextToken()).equals("null"))
					{		
						expiry = new Date(val);					
					}		
					Boolean isSecure = new Boolean(token.nextToken()).								
							booleanValue();		
					Cookie ck = new Cookie(name,value,domain,path,expiry,isSecure);																	
					drive.manage().addCookie(ck); // This will add the stored cookie to your current session					
				}		
			}
		}
		catch(Exception ex){					
			ex.printStackTrace();			
		}		
	}
	
}
