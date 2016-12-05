package com.NoWater;

import com.NoWater.util.LogHelper;
import com.qcloud.cos.sign.Credentials;
import com.qcloud.cos.sign.Sign;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Koprvhdix on 2016/12/4.
 */
@RestController
public class FileUpload {

    @RequestMapping("/file/test")
    public String FileUpload(@RequestParam(value = "path", defaultValue = "/") String path) {
        int appId = 10038234;
        String secretId = "AKIDkTQGMuCeJvtTTlqg911BfF393ghumqHp";
        String secretKey = "ZE1uBa6jfbsB0vVyfbWhw5JuZKPwaEwh";
        String bucketName = "koprvhdix117";
        Credentials cred = new Credentials(appId, secretId, secretKey);
        long expired = System.currentTimeMillis() / 1000 + 600;
        try{

        } catch (Exception e) {
            return "";
        }
    }
}
