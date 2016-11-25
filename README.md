# NoWaterServer
## Usage
下载好IDEA（最好是专业版，社区版没测试过）之后，选择File->Open->NoWaterServer目录下的pom.xml文件。然后就慢慢等它构建完，最下面状态栏没有Process在执行之后，Build->Make Project。
装好最新版maven，配好环境变量。
* mvn clean package
* java -jar target/NoWaterServer-1.0-SNAPSHOT.jar
DBUtil Demo
public void insert(User user){
	    	List<Object> param=new ArrayList<Object>();
	    	StringBuffer sql=new StringBuffer();
	    	DBUtil db=new DBUtil();
	    	sql.append("insert into user(User_id) values(?)");
	        param.add(user.getUser_id);
	    	db.insertUpdateDeleteExute(sql.toString(),param);
}
public void update(User user){
	    	List<Object> param=new ArrayList<Object>();
	    	StringBuffer sql=new StringBuffer();
	    	DBUtil db=new DBUtil();
	    	sql.append("update user set User_id = ?");
	        param.add(user.getUser_id);
	    	db.insertUpdateDeleteExute(sql.toString(),param);
}
public void delete(User user){
		    List<Object> param=new ArrayList<Object>();
	    	StringBuffer sql=new StringBuffer();
	    	DBUtil db=new DBUtil();
	    	sql.append("delete from user where User_id = ?");
	        param.add(user.getUser_id);
	    	db.insertUpdateDeleteExute(sql.toString(),param);
}
public List<User> query(User user,Page page) {
	        List<Object> param = new ArrayList<Object>();
	        StringBuffer sql = new StringBuffer();
	    	sql.append(
					"select Comment_id,Student_name,comment.Student_id,Message_content,Message_time from student JOIN comment  on student.Student_id=comment.Student_id and student.Class_id= ? ");

			param.add(comment.getClass_id());

/*			sql.append(" limit ");
			sql.append(page.getPage() * 10 - 10);
			sql.append(" , ");
			sql.append(10);
            分页暂时不做
*/
	        System.out.println(sql);
	        DBUntil db = new DBUntil();
	        try {
	            List<User> list = db.quseryInfo(sql.toString(), param, User.class);
	            return list;
	        }
	        catch (InstantiationException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        catch (IllegalAccessException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        catch (NoSuchFieldException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        catch (SecurityException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }

	        return null;

	}