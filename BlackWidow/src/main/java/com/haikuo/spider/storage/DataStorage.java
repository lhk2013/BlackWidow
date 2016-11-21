package com.haikuo.spider.storage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haikuo.spider.model.DataImg;
import com.haikuo.spider.model.DataUrl;
import com.haikuo.spider.queue.ImgUrlDeque;
import com.haikuo.spider.spider.SpiderStarter;
import com.haikuo.spider.utils.DBConn;
import com.haikuo.spider.utils.RegexUtils;
import com.sun.corba.se.spi.orbutil.fsm.State;


/**
 * 将Parser解析出的目标数据存入本地存储，可以是MySQL传统数据库，也可以Redis等KV存储
 * @author Administrator
 */
public class DataStorage {
	private static final Logger logger = LoggerFactory.getLogger(DataStorage.class);
	
	public static Connection conn = DBConn.getConnection();
	

	
	public static void imgStore(Object imgData) throws Exception {
		if(null!=imgData) {
			DataImg img = (DataImg)imgData;
			
			String firstDir = RegexUtils.getFirstDir(img.getSrcPth());
			String secondDir = RegexUtils.getSecondDir(img.getSrcPth());
			String fileName = img.getSrcPth().substring(img.getSrcPth().lastIndexOf("/")+1);
			String newPath = "mzitu/thumbs/"+firstDir+"/"+secondDir+"/"+fileName;
			
			img.setNewPath(newPath);
			
			saveImg(img);
			logger.info("index存储目录为：=== "+newPath);
		
		}
	}
	
	
    /**
     * 判断该方法是否存在
     * @param methods
     * @param met
     * @return
     */
    public static boolean checkMethod(Method methods[],String met){
        if(null != methods ){
            for(Method method:methods){
                if(met.equals(method.getName())){
                    return true;
                }
            }
        }        
        return false;
    }
	
    /**
     * 拼接某属性set 方法
     * @param fieldname
     * @return
     */
    public static String pareSetName(String fieldname){
        if(null == fieldname || "".equals(fieldname)){
            return null;
        }
        String pro = "set"+fieldname.substring(0,1).toUpperCase()+fieldname.substring(1);
        return pro;
    }
    /**
     * 拼接某属性get 方法
     * @param fieldname
     * @return
     */
    public static String pareGetName(String fieldname){
        if(null == fieldname || "".equals(fieldname)){
            return null;
        }
        String pro = "get"+fieldname.substring(0,1).toUpperCase()+fieldname.substring(1);
        return pro;
    }
	
    
	public static void setPreparedStatementParams(Object obj,PreparedStatement ps) throws Exception{
        Class<?> cls = obj.getClass();
        Method methods[] = cls.getDeclaredMethods();
        Field fields[] = cls.getDeclaredFields();
        int index = 1;
        for(Field field:fields){
            String fieldtype = field.getType().getSimpleName();
            String fieldName = field.getName();
            String setMethod = pareSetName(fieldName);
            String getMethod = pareGetName(fieldName);
            if(!checkMethod(methods, setMethod)){
                continue;
            }
           
            Method method = cls.getMethod(getMethod, null);
            Object fieldValue = method.invoke(obj,new Object[]{});
            if(null != fieldValue){
                if("String".equals(fieldtype)){
                    ps.setString(index, (String) fieldValue);
                }else if("Double".equals(fieldtype)){
                    ps.setDouble(index, (Double)fieldValue);
                }else if("int".equals(fieldtype)||"Integer".equals(fieldtype)){
                    int val = (Integer)fieldValue;
                    ps.setInt(index, val);
                }else if("Long".equals(fieldtype)){
                	ps.setLong(index, (long) fieldValue);
                }else if("Date".equals(fieldtype)){
                	java.util.Date d = (java.util.Date) fieldValue;
                	ps.setTimestamp(index, new Timestamp(d.getTime()));
                }
            }else{
            	if("String".equals(fieldtype)){
                    ps.setString(index, null);
                }else if("Double".equals(fieldtype)){
                    ps.setDouble(index, 0);
                }else if("int".equals(fieldtype)||"Integer".equals(fieldtype)){
                    ps.setInt(index, 0);
                }else if("Long".equals(fieldtype)){
                	ps.setLong(index, 0);
                }else if("Date".equals(fieldtype)){
                	ps.setDate(index, null);
                }
            }
            index++;
        }    
	}
	

	public static Long saveImg(DataImg img){
		long id = 0L;
		try {
			String sql = "INSERT INTO t_img "
					+ "(id,url_id,img_type,img_key,title,category,src_path,new_path,seq,status,init_time,download_time)"
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			setPreparedStatementParams(img,ps);
			ps.execute();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) { 
				id = rs.getLong(1); 
				img.setId(id);
			} 
			
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long saveDataUrl(DataUrl data){
		long id = 0L;
		try {
			String sql = "INSERT INTO t_data_url "
					+ "(id,pid,list_detail_url,detail_url,url,img_key,status,type,save_time) "
					+ "VALUES (?,?,?,?,?,?,?,?,?) ";
			PreparedStatement ps = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			setPreparedStatementParams(data,ps);
			ps.execute();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) { 
				id = rs.getLong(1); 
				System.out.println("数据主键：" + id); 
				data.setId(id);
			} 
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static void listDataUrlByPageSize(int page ,int size){
		List<DataImg> imgList = new ArrayList<DataImg>();
		try {
			String sql ="SELECT * FROM t_img LIMIT ?,?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, (page-1)*size);
			ps.setInt(2,size);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				DataImg img = new DataImg();
				setResults(img, rs);
				imgList.add(img);
				ImgUrlDeque.addElement(img);
			}
			System.out.println("列表大小是"+imgList.size());
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 传入的查询条件 imgType,status >=1才有效
	 * @param imgType
	 * @param status
	 * @param page
	 * @param size
	 * @return
	 */
	public static BlockingDeque<DataImg> listImgUrlByPageSize(int imgType,int status,int page ,int size){
		BlockingDeque<DataImg> imgList = new LinkedBlockingDeque<>();
		try {
			String sql ="SELECT * FROM t_img ";
			if(status>0&&imgType<=0){
				sql +="WHERE status =? LIMIT ?,? ";
			}else if(imgType>0&&status<=0){
				sql +="WHERE img_type =? LIMIT ?,? ";
			}else{
				sql +="WHERE img_type =? AND status=? LIMIT ?,? ";
			}
			PreparedStatement ps = conn.prepareStatement(sql);
			if(status>0&&imgType<=0){//此时只用状态查询
				ps.setInt(1, status);
				ps.setInt(2, (page-1)*size);
				ps.setInt(3,size);
			}else if(status<=0&&imgType>0){
				ps.setInt(1, imgType);
				ps.setInt(2, (page-1)*size);
				ps.setInt(3,size);
			}else{
				ps.setInt(1, imgType);
				ps.setInt(2, status);
				ps.setInt(3, (page-1)*size);
				ps.setInt(4,size);
			}
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				DataImg img = new DataImg();
				setResults(img, rs);
				imgList.add(img);
				ImgUrlDeque.addElement(img);
			}
			System.out.println("列表大小是"+imgList.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imgList;
	}
	
	/**
	 * 传入的查询条件 imgType,status >=1才有效
	 */
	public static void imgDownloadFinish(long id ,int status,String newPath){
		try {
			String sql ="UPDATE t_img  SET status = ? ,download_time=?,new_path = ? WHERE id =? ";
			PreparedStatement ps;
			ps = conn.prepareStatement(sql);
			ps.setInt(1, status);
			ps.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
			ps.setString(3, newPath);
			ps.setLong(4, id);
			ps.executeUpdate();
			
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void setResults(Object obj,ResultSet rs) throws Exception{
        Class<?> cls = obj.getClass();
        Method methods[] = cls.getDeclaredMethods();
        Field fields[] = cls.getDeclaredFields();
        int index = 1;
        for(Field field:fields){
            String fieldtype = field.getType().getSimpleName();
            String fieldName = field.getName();
            String setMethod = pareSetName(fieldName);
            if(!checkMethod(methods, setMethod)){
                continue;
            }
           
            
            Method method = null;
            if("String".equals(fieldtype)){
            	method = cls.getMethod(setMethod,java.lang.String.class);
            	method.invoke(obj, rs.getString(index)) ;
            }else if("Double".equals(fieldtype)){
            	method = cls.getMethod(setMethod,java.lang.Double.class);
                method.invoke(obj, rs.getDouble(index));
            }else if("int".equals(fieldtype)||"Integer".equals(fieldtype)){
            	method = cls.getMethod(setMethod,java.lang.Integer.class);
                method.invoke(obj, rs.getInt(index)) ;
            }else if("Long".equals(fieldtype)){
            	method = cls.getMethod(setMethod,java.lang.Long.class);
            	method.invoke(obj, rs.getLong(index));
            }else if("Date".equals(fieldtype)){
            	method = cls.getMethod(setMethod,java.util.Date.class);
            	java.util.Date d = rs.getTimestamp(index);
            	method.invoke(obj, d);
            }
            index++;
            System.out.println(index);
        }    
        
	}
	
	
	/**
	 * 查询
	 * @param detailUrl
	 * @return
	 */
	public static int queryCountByTypeAndStatus(String detailUrl,int urlType,int status){
		int count = 0;
		try {
			
			String sql = " SELECT COUNT(*) FROM t_data_url WHERE detail_url = ? AND type =? AND status =? ";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, detailUrl);
			ps.setInt(2, urlType);
			ps.setInt(3, status);
			ResultSet rs = ps.executeQuery();
			if(rs!=null&&rs.next()){
				count = rs.getInt(1);
			}
			ps.close();
		} catch (Exception e) {
		}

		return count;
	}
	/**
	 * 将一个详情套图 全部修改为完成
	 * @param detailUrl
	 * @param status
	 */
	public static void updateDataImg(String detailUrl,int status){
		
		try {
			String sql = "UPDATE t_data_url SET status = ? WHERE detail_url = ? ";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, status);
			ps.setString(2, detailUrl);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 删除 所有连接
	 * @param args
	 * @throws Exception
	 */
	public static int deleteByDetailUrl(String detailUrl){
		try {
			String sql = "DELETE  FROM  t_data_url WHERE detail_url =?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, detailUrl);
			boolean a = ps.execute();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		SpiderStarter.initializeParams();
		
		
		BasicDataSource bds = DBConn.getConnectionDS();
		org.apache.commons.dbcp2.BasicDataSource bds2 = DBConn.getConnectionDS2();

		long s1 = new Date().getTime();
		for (int i =0;i<100;i++) {
			 DBConn.getConnection();
			 bds.getConnection();
		}
		long e1 = new Date().getTime();
		System.err.println(e1 - s1);

		
//		int count = queryCountByTypeAndStatus(url,2,2);
//		System.out.println(count);
		//int count = deleteByDetailUrl(url);
		
	}
	
	
	
}
