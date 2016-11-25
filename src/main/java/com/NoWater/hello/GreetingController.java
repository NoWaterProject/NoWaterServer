package com.NoWater.hello;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.NoWater.util.LogHelper;


/**
 * Created by wukai on 16-11-18.
 */
@RestController
public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        LogHelper.info("test");
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

}
/*
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
			//sql.append(" limit ");
			//sql.append(page.getPage() * 10 - 10);
			//sql.append(" , ");
			//sql.append(10);
            //分页暂时不做

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
    */