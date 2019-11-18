package top.byk.baidu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.byk.baidu.service.SpeechSynthesisServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @description:  语音合成
 * @author: ykbian
 * @date 2019/11/14 10:32
 */
@Controller
public class SpeechSynthesisController {


    @Autowired
    private SpeechSynthesisServiceImpl speechSynthesisService;

    @GetMapping("")
    public String toIndex(){
        System.out.println("================"+System.getProperty("user.dir"));
        return "index";
    }

    /**
     * @Description: 小于1000字的语音合成并下载。
     * @Author: ykbian
     * @Date: 2019/11/14 10:58
     * @Param:
     * @return:
     */
    @GetMapping("/SpeechSynthesis1")
    @ResponseBody
    public String SpeechSynthesis1(String text,String per, HttpServletResponse response, HttpServletRequest request){
        if (text.length()>1000){
            return "文本信息过长";
        }
        //用输入文本的前10个字作为文件名，不够的话就直接用文本内容做文件名
        String fileName  = "";
        if (text.length() > 10){
             fileName = text.substring(0,9)+".mp3";
        }else {
             fileName = text+".mp3";
        }

        String filePathAndName = System.getProperty("user.dir")+"/"+fileName;
        int res = speechSynthesisService.SpeechSynthesis(text,per,filePathAndName);
        if (res == 0){
            return "语音转换失败，联系管理员";
        }
        System.out.println("语音合成结束，结果为（1代表成功 0代表失败）："+res);
        OutputStream outputStream = null;
        try {
            String userAgent = request.getHeader("User-Agent");
            response.reset();
            // 防止中文乱码
            // 针对IE或者以IE为内核的浏览器：
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else {
                // 非IE浏览器的处理：
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentType("application/json;charset=UTF-8");
            outputStream = new BufferedOutputStream(response.getOutputStream());
            byte[] bytes = getBytesByFile(filePathAndName);
            outputStream.write(bytes);
            outputStream.flush();
            //下载完成以后删除语音文件,不然越拖越大
            File file = new File(filePathAndName);
            if (file.exists() && file.isFile()){
                file.delete();
            }
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }
        }
    }






    /**
     * @Description: 读取MP3文件的工具
     * @Author: ykbian
     * @Date: 2019/11/14 11:30
     * @Param:
     * @return:
     */
    //将文件转换成Byte数组
    public static byte[] getBytesByFile(String pathStr) {
        File file = new File(pathStr);
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            byte[] data = bos.toByteArray();
            bos.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
