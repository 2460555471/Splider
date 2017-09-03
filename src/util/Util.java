package util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;  
import java.util.Date;  
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Crawling.News;

import java.io.IOException;

public class Util {
	 public static void main(String[] args) {
		 
		 
		 
	 }
	 
	 
	 
	 /**
	  * �ַ���HASH���ܣ����ڱȽ��Ƿ���ʹ�
	  * @param key
	  * @return
	  */
	 public static long hashFunc(String key)
	 {  
		    int arraySize = 1000000007;          //�����Сһ��ȡ����  
		    long hashCode = 0;  
		    for(int i=0;i<key.length();i++){        //���ַ�������߿�ʼ����  
		        int letterValue = key.charAt(i) - 96;//����ȡ�����ַ���ת�������֣�����a����ֵ��97����97-96=1 �ʹ���a��ֵ��ͬ��b=2��  
		        hashCode = ((hashCode << 5) + letterValue) % arraySize;//��ֹ�����������ÿ�����������ȡģ����  
		    }  
		    return hashCode;  
		} 
	      
	 
	public static Date DateConvert(String times)
	{
		Date date=null;
		try {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        date= sdf.parse(times);

	    } catch (ParseException e) {
	    	System.out.println("ʱ��ת���쳣");
	        //e.printStackTrace();
	    }
		finally{
			return date;
		}
		
	}
	
}
