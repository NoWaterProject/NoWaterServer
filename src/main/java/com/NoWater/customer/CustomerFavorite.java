package com.NoWater.customer;

import com.NoWater.model.Favorite;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.lang.Object;

/**
 * Created by qingpeng on 2016/12/8.
 */
@RestController
public class CustomerFavorite {

    private Jedis jedis;

    @RequestMapping("customer/favorite/adding")
    public JSONObject operateFavorite(
            @RequestParam(value = "type", defaultValue = "/") int type,
            @RequestParam(value = "id", defaultValue = "/") int id,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String uuid = CookieUtil.getCookieValueByName(request, "token");
        JSONObject jsonObject = new JSONObject();
        if (uuid != null) {
            jedis = new Jedis("127.0.0.1", 6379);
            String user_id = jedis.get(uuid);
            if (user_id == null) {
                jsonObject.put("status", 300);
            } else if (uuid.equals(jedis.get(user_id))) {
                List<Object> addFavo = new ArrayList<Object>();
                DBUtil db = new DBUtil();
                String queryInfo="select * from favorite where user_id = ? and type = ? and id = ?";
                addFavo.add(user_id);
                addFavo.add(type);
                addFavo.add(id);
                List<Favorite> getShopIdList = db.queryInfo(queryInfo, addFavo, Favorite.class);

                if(getShopIdList.size() == 0) {
                    String favoAdding="insert into favorite values('?','?','?')";
                    addFavo.add(user_id);
                    addFavo.add(type);
                    addFavo.add(id);
                    db.insertUpdateDeleteExute(favoAdding, addFavo);
                } else {
                    String favoDelete="delete from favorite where user_id = ? and type = ? and id = ?";
                    addFavo.add(user_id);
                    addFavo.add(type);
                    addFavo.add(id);
                    db.insertUpdateDeleteExute(favoDelete, addFavo);
                }
                jsonObject.put("status", 200);
            } else {
                jsonObject.put("status", 300);
            }
        } else
            jsonObject.put("status", 300);
        return jsonObject;
    }
}
