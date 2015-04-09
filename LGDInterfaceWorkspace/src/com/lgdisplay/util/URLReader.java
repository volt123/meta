package com.lgdisplay.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import com.itplus.mm.common.util.StringUtils;

public class URLReader {
	
	public boolean sending(String urls) {
		boolean issending = false;
		try {
			System.out.println("urls....:::"+urls);
			URL url                  = new URL(urls);
			URLConnection urlconn    = url.openConnection();
			urlconn.connect();
			BufferedInputStream bis  = new BufferedInputStream(urlconn.getInputStream());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buff = new byte[2048];
			int bytesRead;
			while ( -1 != (bytesRead = bis.read(buff, 0, buff.length)) ) {
				baos.write(buff, 0, bytesRead);
			}
			bis.close();
			baos.close();
			String s = baos.toString();
			System.out.println("....:::"+s);
			if ( s.trim().endsWith("0") ) {
				issending = true;
				System.out.println(".....SEND OK.....");
			}
			else {
				issending = false;
				System.out.println(".....SEND FAIL.....");
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			issending = false;
		} catch (IOException e) {
			e.printStackTrace();
			issending = false;
		} catch(Exception e){
			e.printStackTrace();
			issending = false;
		}
		return issending;
	}
	public HashMap<String, String> getParam(String sUrls) {
//		String sUrls="http://jyansmin:9093/metaminer/mail/getMailContent.jsp?INST_ID=14";
		HashMap<String, String> oMap = new HashMap<String, String>();
		String[] saS = StringUtils.split(sUrls,"?");
		String[] saS2 = StringUtils.split(saS[1],"&");
		for (int i=0;i<saS2.length;i++){
			String[] saS3 = StringUtils.split(saS2[i],"=");
			oMap.put(saS3[0], saS3[1]);
//			System.out.println("...:"+saS3[0]);
//			System.out.println("...:"+saS3[1]);
		}
		return oMap;
	}
	
	public String getUrlRead(String urls) {
		String s = "";
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		try {
			System.out.println("urls::::...."+urls);
			URL url                  = new URL(urls);
			URLConnection urlconn    = url.openConnection();
			urlconn.connect();
			bis  = new BufferedInputStream(urlconn.getInputStream());
			baos = new ByteArrayOutputStream();
			byte[] buff = new byte[2048];
			int bytesRead;
			while ( -1 != (bytesRead = bis.read(buff, 0, buff.length)) ) {
				baos.write(buff, 0, bytesRead);
			}
			s = baos.toString();
			bis.close();
			baos.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if ( null != bis )
				try {
					bis.close();
				} catch (IOException e) {
				}
			if ( null != baos )
				try {
					baos.close();
				} catch (IOException e) {
				}
		}
		return s;
	}

}
