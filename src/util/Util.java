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
	  * 字符串HASH加密，便于比较是否访问过
	  * @param key
	  * @return
	  */
	 public static long hashFunc(String key)
	 {  
		    int arraySize = 1000000007;          //数组大小一般取质数  
		    long hashCode = 0;  
		    for(int i=0;i<key.length();i++){        //从字符串的左边开始计算  
		        int letterValue = key.charAt(i) - 96;//将获取到的字符串转换成数字，比如a的码值是97，则97-96=1 就代表a的值，同理b=2；  
		        hashCode = ((hashCode << 5) + letterValue) % arraySize;//防止编码溢出，对每步结果都进行取模运算  
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
	    	System.out.println("时间转化异常");
	        //e.printStackTrace();
	    }
		finally{
			return date;
		}
		
	}
	
}
