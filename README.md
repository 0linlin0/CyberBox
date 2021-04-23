Exp Poc框架并不少，TangScan、Pocsuite 等等，用python写一个其实是很简单的事情。为什么要重复造这个轮子呢?

看过不少漏洞了，差不多都是本地很杂乱的存放poc，很多语言都有，而且大多数poc也只能弹个计算器而已.....所以很早就想拥有一个属于自己的统一存放Exp的地方，也希望让身边人用起来(理想很丰满，现实很骨干...)

回归正题，目前经手的Java漏洞很多，反序列化漏洞用脚本语言编写拿眼一点点分析字节码真🐕，而且目前接触的最多的也是Java，选择用Java来编写。其实一年前这个框架已经成型，这次花了几天时间主要修改了插件的集成方式，之前直接集成在框架内部，随着漏洞的增加，不同的中间件需要不同的依赖，框架本身体积将会越来越大，此次修改借鉴了哥斯拉思路，将jar动态注入到jvm中，实现热加载，极大的减少了框架本身体积，在此超级感谢北辰老板动手修改jar动态加载部分(我太菜了.....)。

# 一、基本使用

## 1.目录

工具包含两部分，首先是jar包，以及plugins文件夹，插件放入plugins文件夹下工具即可自动加载，命令行下java -jar CyberExp.j

![图片](https://uploader.shimo.im/f/u0bNK41x0Yre9m5V.png!thumbnail?fileGuid=QrrQ8c8xt3KX3qQq)

## 2.启动

打开工具是一个Demo界面，单击你所要使用的插件

![图片](https://uploader.shimo.im/f/pi4aIxO5ODXOtUAf.png!thumbnail?fileGuid=QrrQ8c8xt3KX3qQq)![图片](https://uploader.shimo.im/f/naVj7NBuUudUuJT4.png!thumbnail?fileGuid=QrrQ8c8xt3KX3qQq)

## 3.参数配置

点击按钮进行参数配置，参数说明在右侧给出

![图片](https://uploader.shimo.im/f/HP66pbW00ojlJZec.png!thumbnail?fileGuid=QrrQ8c8xt3KX3qQq)

## 4.Test

填写完参数后点击Test 进行测试

![图片](https://uploader.shimo.im/f/oHt80k3JfWTGKRQE.png!thumbnail?fileGuid=QrrQ8c8xt3KX3qQq)


![图片](https://uploader.shimo.im/f/gJjpH93rZ7beaUnU.png!thumbnail?fileGuid=QrrQ8c8xt3KX3qQq)



## 5.Attack

Attack同Test

![图片](https://uploader.shimo.im/f/YYhpqGs7p9l5xuCt.png!thumbnail?fileGuid=QrrQ8c8xt3KX3qQq)

![图片](https://uploader.shimo.im/f/1DNp70G1EYsbkH7h.png!thumbnail?fileGuid=QrrQ8c8xt3KX3qQq)

## 6.注意

如果需要测试其它漏洞，需先点击插件标签，再点击参数配置按钮![图片](https://uploader.shimo.im/f/PLsAiDa1SIQ34c0m.png!thumbnail?fileGuid=QrrQ8c8xt3KX3qQq)

## 7.Stop All 功能

测试和攻击是可能会出现目标IP不可达等问题，Stop All 可以停止所有测试攻击线程

![图片](https://uploader.shimo.im/f/E1NhyQW6RAEtrp0a.png!thumbnail?fileGuid=QrrQ8c8xt3KX3qQq)


# 二、插件编写

由于插件需要使用Java编写，还是有一丢丢门槛，为了大家编写方便，已经将插件生成的idea项目打包上传 Plugin_Project

## 1.在vulpayload下新建Java类

![图片](https://uploader.shimo.im/f/e1AueNfTNYSuaHnL.png!thumbnail?fileGuid=QrrQ8c8xt3KX3qQq)

新建类需继承Payload类 无参构造函数配置payload_info、以及payload_args。(注意payload_info必须按照Demo规范写，payload_args名称值用户可自定义)

然后重写test() 、execution() 方法，编写main方法用来导出jar包。如下：

```plain
package vulpayload;
import baselib.Result;
import org.apache.shiro.crypto.AesCipherService;
/**
 * @auther Skay
 * @date 2021/4/22 22:17
 * @description
 */
public class Mypayload extends Payload{
    public Mypayload(){
        payload_info.put("payload_name","AAA 反序列化漏洞");
        payload_info.put("payload_id","1");
        payload_info.put("payload_module","AAA");
        payload_info.put("payload_affect_version","<= 1.2.4");
        payload_info.put("payload_number","aaa");
        payload_info.put("payload_cvss","9.8");
        payload_info.put("payload_affect","无需登录，远程代码执行");
        payload_info.put("payload_overview","AAA 在 Java 的权限及安全验证框架中占用重要的一席之地，\n\n在它编号为550的 issue 中爆出严重的 Java 反序列化漏洞\n\n issue地址：https://issues.apache.org/jira/browse/SHIRO-550");
        payload_info.put("payload_author_id","Skay");
        payload_info.put("payload_author_email","aaa@gmail.com");
        payload_info.put("payload_finish_time","2020/5/18");
        payload_info.put("payload_author_website","http://github.com/0linlin0");
        payload_info.put("args_num","5");
        if(payload_args.isEmpty()){
            payload_args.put("ip","10.251.0.111");
            payload_args.put("protocol","http");
            payload_args.put("url_back","/login");
            payload_args.put("port","8080");
            payload_args.put("cmd","curl 10.251.0.111:9999");
        }else {
            payload_args.clear();
            payload_args.put("ip","10.251.0.111");
            payload_args.put("protocol","http");
            payload_args.put("back","/login");
            payload_args.put("port","8080");
            payload_args.put("cmd","curl 10.251.0.111:9999");
        }
    }

    @Override
    public Result test() throws Exception {
        AesCipherService aesCipherService = new AesCipherService();
        Result result = new Result();
        result.return_content = "success";
        result.is_success = true;
        result.is_echo = true;
        return result;
    }
    @Override
    public Result execution() throws Exception {
        Result result = new Result();
        result.return_content = "success";
        result.is_success = true;
        result.is_echo = true;
        return result;
    }
    public static void main(String[] args){
        System.out.println("test");
    }
}
```
## 2.idea配置导出jar包

请勿将框架本身jar包含到插件jar中

![图片](https://uploader.shimo.im/f/c1HK419RyV6e2jmD.png!thumbnail?fileGuid=QrrQ8c8xt3KX3qQq)

## 3.导出

![图片](https://uploader.shimo.im/f/SgA8OdG1XnznjDC0.png!thumbnail?fileGuid=QrrQ8c8xt3KX3qQq)

注意插件名称需要与新建的Class相同。

## 4.其它

框架本身提供了HttpUtils工具类，可以满足大部分http请求处理，编写过程中需要其它依赖，自行打包到插件即可，框架运行时会自动加载。

```plain
package tools;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
/**
 * Http发送post请求工具，兼容http和https两种请求类型
 */
public class HttpUtils {
    /**
     * 请求超时时间
     */
    private static final int TIME_OUT = 120000;
    /**
     * Https请求
     */
    private static final String HTTPS = "https";
    /**
     * Content-Type
     */
    private static final String CONTENT_TYPE = "Content-Type";
    /**
     * 表单提交方式Content-Type
     */
    private static final String FORM_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";
    /**
     * JSON提交方式Content-Type
     */
    private static final String JSON_TYPE = "application/json;charset=UTF-8";
    /**
     * 发送Get请求
     * 
     * @param url 请求URL
     * @return HTTP响应对象
     * @throws IOException 程序异常时抛出，由调用者处理
     */
    public static Response get(String url) throws IOException {
        return get(url, null);
    }
    /**
     * 发送Get请求
     * 
     * @param url 请求URL
     * @param headers 请求头参数
     * @return HTTP响应对象
     * @throws IOException 程序异常时抛出，由调用者处理
     */
    public static Response get(String url, Map<String, String> headers) throws IOException {
        if (null == url || url.isEmpty()) {
            throw new RuntimeException("The request URL is blank.");
        }
        // 如果是Https请求
        if (url.startsWith(HTTPS)) {
            getTrust();
        }
        Connection connection = Jsoup.connect(url);
        connection.method(Method.GET);
        connection.timeout(TIME_OUT);
        connection.ignoreHttpErrors(true);
        connection.ignoreContentType(true);
        connection.maxBodySize(0);
        if (null != headers) {
            connection.headers(headers);
        }
        Response response = connection.execute();
        return response;
    }
    /**
     * 发送JSON格式参数POST请求
     * 
     * @param url 请求路径
     * @param params JSON格式请求参数
     * @return HTTP响应对象
     * @throws IOException 程序异常时抛出，由调用者处理
     */
    public static Response post(String url, String params) throws IOException {
        return doPostRequest(url, null, params);
    }
    /**
     * 发送JSON格式参数POST请求
     * 
     * @param url 请求路径
     * @param headers 请求头中设置的参数
     * @param params JSON格式请求参数
     * @return HTTP响应对象
     * @throws IOException 程序异常时抛出，由调用者处理
     */
    public static Response post(String url, Map<String, String> headers, String params) throws IOException {
        return doPostRequest(url, headers, params);
    }
    /**
     * 字符串参数post请求
     * 
     * @param url 请求URL地址
     * @param paramMap 请求字符串参数集合
     * @return HTTP响应对象
     * @throws IOException 程序异常时抛出，由调用者处理
     */
    public static Response post(String url, Map<String, String> paramMap) throws IOException {
        return doPostRequest(url, null, paramMap, null);
    }
    /**
     * 带请求头的普通表单提交方式post请求
     * 
     * @param headers 请求头参数
     * @param url 请求URL地址
     * @param paramMap 请求字符串参数集合
     * @return HTTP响应对象
     * @throws IOException 程序异常时抛出，由调用者处理
     */
    public static Response post(Map<String, String> headers, String url, Map<String, String> paramMap) throws IOException {
        return doPostRequest(url, headers, paramMap, null);
    }
    /**
     * 带上传文件的post请求
     * 
     * @param url 请求URL地址
     * @param paramMap 请求字符串参数集合
     * @param fileMap 请求文件参数集合
     * @return HTTP响应对象
     * @throws IOException 程序异常时抛出，由调用者处理
     */
    public static Response post(String url, Map<String, String> paramMap, Map<String, File> fileMap) throws IOException {
        return doPostRequest(url, null, paramMap, fileMap);
    }
    /**
     * 带请求头的上传文件post请求
     * 
     * @param url 请求URL地址
     * @param headers 请求头参数
     * @param paramMap 请求字符串参数集合
     * @param fileMap 请求文件参数集合
     * @return HTTP响应对象
     * @throws IOException 程序异常时抛出，由调用者处理
     */
    public static Response post(String url, Map<String, String> headers, Map<String, String> paramMap, Map<String, File> fileMap) throws IOException {
        return doPostRequest(url, headers, paramMap, fileMap);
    }
    /**
     * 执行post请求
     * 
     * @param url 请求URL地址
     * @param headers 请求头
     * @param jsonParams 请求JSON格式字符串参数
     * @return HTTP响应对象
     * @throws IOException 程序异常时抛出，由调用者处理
     */
    private static Response doPostRequest(String url, Map<String, String> headers, String jsonParams) throws IOException {
        if (null == url || url.isEmpty()) {
            throw new RuntimeException("The request URL is blank.");
        }
        // 如果是Https请求
        if (url.startsWith(HTTPS)) {
            getTrust();
        }
        Connection connection = Jsoup.connect(url);
        connection.method(Method.POST);
        connection.timeout(TIME_OUT);
        connection.ignoreHttpErrors(true);
        connection.ignoreContentType(true);
        connection.maxBodySize(0);
        if (null != headers) {
            connection.headers(headers);
        }
        connection.header(CONTENT_TYPE, JSON_TYPE);
        connection.requestBody(jsonParams);
        Response response = connection.execute();
        return response;
    }
    /**
     * 普通表单方式发送POST请求
     * 
     * @param url 请求URL地址
     * @param headers 请求头
     * @param paramMap 请求字符串参数集合
     * @param fileMap 请求文件参数集合
     * @return HTTP响应对象
     * @throws IOException 程序异常时抛出，由调用者处理
     */
    private static Response doPostRequest(String url, Map<String, String> headers, Map<String, String> paramMap, Map<String, File> fileMap) throws IOException {
        if (null == url || url.isEmpty()) {
            throw new RuntimeException("The request URL is blank.");
        }
        // 如果是Https请求
        if (url.startsWith(HTTPS)) {
            getTrust();
        }
        Connection connection = Jsoup.connect(url);
        connection.method(Method.POST);
        connection.timeout(TIME_OUT);
        connection.ignoreHttpErrors(true);
        connection.ignoreContentType(true);
        connection.maxBodySize(0);
        if (null != headers) {
            connection.headers(headers);
        }
        // 收集上传文件输入流，最终全部关闭
        List<InputStream> inputStreamList = null;
        try {
            // 添加文件参数
            if (null != fileMap && !fileMap.isEmpty()) {
                inputStreamList = new ArrayList<InputStream>();
                InputStream in = null;
                File file = null;
                for (Entry<String, File> e : fileMap.entrySet()) {
                    file = e.getValue();
                    in = new FileInputStream(file);
                    inputStreamList.add(in);
                    connection.data(e.getKey(), file.getName(), in);
                }
            }
            // 普通表单提交方式
            else {
                connection.header(CONTENT_TYPE, FORM_TYPE);
            }
            // 添加字符串类参数
            if (null != paramMap && !paramMap.isEmpty()) {
                connection.data(paramMap);
            }
            Response response = connection.execute();
            return response;
        }
        // 关闭上传文件的输入流
        finally {
            closeStream(inputStreamList);
        }
    }
    /**
     * 关流
     * 
     * @param streamList 流集合
     */
    private static void closeStream(List<? extends Closeable> streamList) {
        if (null != streamList) {
            for (Closeable stream : streamList) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 获取服务器信任
     */
    private static void getTrust() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] { new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
## 5.关于Plugins文件夹

此文件夹可以存放插件jar包，以及一些通用依赖库，程序运行时会将其全部加载进jvm中，你可以选择将插件的依赖单独放置在文件夹中或者整体打包到插件本身

# 三、欢迎提建议

由于开发水平实在有限，肯定会有很多bug，使用起来可能很不好用，勿喷，欢迎提交issus

还有，有什么漏洞非常想要exp欢迎在issue中留言






