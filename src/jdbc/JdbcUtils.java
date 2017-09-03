package jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mysql jdbc实用类
 *
 */
public class JdbcUtils {
	//数据库用户名
	private static final String USERNAME = "root";
	//数据库密码
	private static final String PASSWORD = "root";
	//驱动信息 
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	//数据库地址
	private static final String URL = "jdbc:mysql://localhost:3306/News";
	
	private Connection connection;
	private PreparedStatement pstmt;
	private ResultSet resultSet;
	
	public JdbcUtils() {
		try{
			Class.forName(DRIVER);
			//System.out.println("数据库连接成功!");
			connection=getConnection();
		}catch(Exception e){

		}
	}

	/**
	 * getConnection获得数据库的连接，返回Connection连接对象
	 * @return
	 */
	public Connection getConnection(){
		try {
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * 增加、删除、修改，返回成功与否true与false
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public boolean updateByPreparedStatement(String sql, List<Object>params)throws SQLException
	{
		boolean flag = false;
		int result = -1;
		pstmt = connection.prepareStatement(sql);
		int index = 1;
		if(params != null && !params.isEmpty())
		{
			for(int i=0; i<params.size(); i++)
			{
				pstmt.setObject(index++, params.get(i));
			}
		}
		result = pstmt.executeUpdate();
		flag = result > 0 ? true : false;
		return flag;
	}

	
	/**
	 * 非反射查询单条记录
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	
//		不利用反射查询多个记录
//	    String sql2 = "select * from userinfo ";
//		List<Map<String, Object>> list = jdbcUtils.findModeResult(sql2, null);
//		System.out.println(list);*/
	public Map<String, Object> findSimpleResult(String sql, List<Object> params) throws SQLException{
		Map<String, Object> map = new HashMap<String, Object>();
		int index  = 1;
		pstmt = connection.prepareStatement(sql);
		if(params != null && !params.isEmpty()){
			for(int i=0; i<params.size(); i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();//返回查询结果
		ResultSetMetaData metaData = resultSet.getMetaData();
		int col_len = metaData.getColumnCount();
		while(resultSet.next()){
			for(int i=0; i<col_len; i++ ){
				String cols_name = metaData.getColumnName(i+1);
				Object cols_value = resultSet.getObject(cols_name);
				if(cols_value == null){
					cols_value = "";
				}
				map.put(cols_name, cols_value);
			}
		}
		return map;
	}

	/**非反射查询多条记录
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> findModeResult(String sql, List<Object> params) throws SQLException{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int index = 1;
		pstmt = connection.prepareStatement(sql);
		if(params != null && !params.isEmpty()){
			for(int i = 0; i<params.size(); i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();
		while(resultSet.next()){
			Map<String, Object> map = new HashMap<String, Object>();
			for(int i=0; i<cols_len; i++){
				String cols_name = metaData.getColumnName(i+1);
				Object cols_value = resultSet.getObject(cols_name);
				if(cols_value == null){
					cols_value = "";
				}
				map.put(cols_name, cols_value);
			}
			list.add(map);
		}

		return list;
	}

	/**通过反射机制查询单条记录
	 * @param sql
	 * @param params
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T> T findSimpleRefResult(String sql, List<Object> params,Class<T> cls )throws Exception{
		T resultObject = null;
		int index = 1;
		pstmt = connection.prepareStatement(sql);
		if(params != null && !params.isEmpty()){
			for(int i = 0; i<params.size(); i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();
		ResultSetMetaData metaData  = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();
		while(resultSet.next()){
			//通过反射机制创建一个实例
			resultObject = cls.newInstance();
			for(int i = 0; i<cols_len; i++){
				String cols_name = metaData.getColumnName(i+1);
				Object cols_value = resultSet.getObject(cols_name);
				if(cols_value == null){
					cols_value = "";
				}
				Field field = cls.getDeclaredField(cols_name);
				field.setAccessible(true); //打开javabean的访问权限
				field.set(resultObject, cols_value);
			}
		}
		return resultObject;

	}

	/**通过反射机制查询多条记录
	 * @param sql 
	 * @param params
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> findMoreRefResult(String sql, List<Object> params,Class<T> cls )throws Exception {
		List<T> list = new ArrayList<T>();
		int index = 1;
		pstmt = connection.prepareStatement(sql);
		if(params != null && !params.isEmpty()){
			for(int i = 0; i<params.size(); i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();
		ResultSetMetaData metaData  = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();
		while(resultSet.next()){
			//通过反射机制创建一个实例
			T resultObject = cls.newInstance();
			for(int i = 0; i<cols_len; i++){
				String cols_name = metaData.getColumnName(i+1);
				Object cols_value = resultSet.getObject(cols_name);
				if(cols_value == null){
					cols_value = "";
				}
				Field field = cls.getDeclaredField(cols_name);
				field.setAccessible(true); //打开javabean的访问权限
				field.set(resultObject, cols_value);
			}
			list.add(resultObject);
		}
		return list;
	}

	/**
	 * 释放数据库连接
	 */
	public void releaseConn(){
		if(resultSet != null){
			try{
				resultSet.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
		/*******************增*********************/
//		String sql = "insert into people (id, name,age) values (?, ?, ?),(?,?,?)";
//		List<Object> params = new ArrayList<Object>();
//		params.add(9);
//		params.add("123");
//		params.add(12);
//		params.add("10");
//		params.add("sdg");
//		params.add("23");
//		try {
//			boolean flag = jdbcUtils.updateByPreparedStatement(sql, params);
//			System.out.println(flag);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		/*******************删*********************/
//		String sql = "delete from people where id = ?";
//		List<Object> params = new ArrayList<Object>();
//		params.add("10");
//		boolean flag = jdbcUtils.updateByPreparedStatement(sql, params);

		/*******************改*********************/
//		String sql = "update people set age = ? where id = ? ";
//		List<Object> params = new ArrayList<Object>();
//		params.add("100");
//		params.add("4");
//		boolean flag = jdbcUtils.updateByPreparedStatement(sql, params);
//		System.out.println(flag);

		/*******************查*********************/
		//不利用反射查询多个记录
//		String sql2 = "select * from people where id=2 ";
//		List<Map<String, Object>> list = jdbcUtils.findModeResult(sql2, null);
//		for(int i=0;i<list.size();i++)
//		{
//			System.out.println("     "+list.get(i).get("id")+"  "+list.get(i).get("name")+"  "+list.get(i).get("age")+"  ");
//		}
//	public static void Main(String args)
//	{
//		/**
//         * 采集到各项数据
//         */
//		 String user_id=String.valueOf(new Date().getTime());
//		String openid ;
//		String wechat_num;
//		String nickname;
//		String head_img_url;
//		
//		String user_real_name ;
//		String sex ;
//		String user_birthday ;
//		String user_idcard;
//		String user_phonenum ;
//		
//		openid="ewegagdzgae";
//		wechat_num="15010051958";
//		nickname="houyi";
//		head_img_url="asfgeg";
//		user_real_name="侯沂";
//		sex="0";
//		user_birthday="1994-12-30";
//		user_idcard="210623199412301879";
//		user_phonenum="15010051958";
//		/**
//		 * 验证码，不写入数据库
//		 */
//		String cherifynum ;
//		
//        if(true)
//        {
//        	JdbcUtils db=new JdbcUtils();
//        	String sql="insert into user ("+
//        			  "user_id,"+
//        			  "openid,"+
////        			  "wechat_num,"+
////        			  "nickname,"+
////        			  "sex,"+
////        			  "user_phonenum,"+
////        			  "user_real_name,"+
////        			  "head_img_url,"+
////        			  "user_idcard,"+
////        			  "user_birthday,"+
//        			") values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        	System.out.println(123);
//    		List<Object> params = new ArrayList<Object>();
//    		params.add(user_id);
//    		params.add(openid);
////    		params.add(wechat_num);
////    		params.add(nickname);
////    		params.add(sex);
////    		params.add(user_phonenum);
////    		params.add(user_real_name);
////    		params.add(head_img_url);
////    		params.add(user_idcard);
////    		params.add(user_birthday);
//    		try {
//    			boolean flag = db.updateByPreparedStatement(sql, params);
//    			System.out.println(flag);
//    			
//    		} catch (SQLException e) {
//    			e.printStackTrace();
//    		}
//        		
//        }
//	}
		
}

