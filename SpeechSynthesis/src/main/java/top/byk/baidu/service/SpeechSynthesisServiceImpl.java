package top.byk.baidu.service;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.baidu.aip.util.Util;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

/**
 * @description:
 * @author: ykbian
 * @date 2019/11/14 10:35
 */
@Service
public class SpeechSynthesisServiceImpl {

    //设置APPID/AK/SK

    public static final String APP_ID = "17766717";
    public static final String API_KEY = "cKImWdASws6uavWmOfOjsROO";
    public static final String SECRET_KEY = "9k8syT5YBKpuTik4ADaLcr1tXGyPijVl";


    public int SpeechSynthesis(String text,String per,String path){
        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 设置可选参数
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put("spd", "5");  //语速，取值0-9，默认为5中语速
        options.put("pit", "5");  //音调，取值0-9，默认为5中语调
        options.put("per", per);  //发音人选择, 0为女声，1为男声， 3为情感合成-度逍遥，4为情感合成-度丫丫，默认为普通女
        options.put("vol","5");//音量，取值0-15，默认为5中音量
        // 调用接口
        TtsResponse res = client.synthesis(text, "zh", 1, options);
        byte[] data = res.getData();
        JSONObject res1 = res.getResult();
        if (data != null) {
            try {
                Util.writeBytesToFileSystem(data, path);
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (res1 != null) {
            System.out.println(res1.toString(2));
        }
        return 0;
    }
}
