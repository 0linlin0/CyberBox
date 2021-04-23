package tools;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import ui.FXMLDocumentController;
import vulpayload.Payload;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClassLoaderUtils {
//    @FXML
//    public ListView vullist_listview;
//    public void initvullist() {
//        String basePath = System.getProperty("user.dir")+ File.separator+"plugins"+ File.separator;
//        File file = new File(basePath);
//        // get the folder list
//        File[] arrayfilename = file.listFiles();
////        final Reflections reflections = new Reflections(Payload.class.getPackage().getName());
////        final Set<Class<? extends Payload>> payloadTypes = reflections.getSubTypesOf(Payload.class);
//        final Set<Class<? extends Payload>> payloadTypes = new HashSet<>();
//
//        try {
//            URL[] urls = new URL[arrayfilename.length];
//            for (int i = 0; i <arrayfilename.length; i++) {
//                URL url = arrayfilename[i].toURI().toURL();
//                urls[i] = url;
//                System.out.println(arrayfilename[i].getAbsolutePath());
//            }
//            ClassLoader classLoader = new URLClassLoader( urls, null );
//            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
//            if (!method.isAccessible()) {
//                method.setAccessible(true);
//            }
//
//            for (int i = 0;i < urls.length;i++){
//                try {
//                    method.invoke(classLoader, urls[i]);
//                    String theclassname  = "vulpayload."+arrayfilename[i].getName().substring(0,arrayfilename[i].getName().indexOf("."));
//                    Class<? extends Payload> tmpclass = (Class<? extends Payload>) classLoader.loadClass(theclassname);
//                    payloadTypes.add(tmpclass);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//            Class.forName("ysoserial.payloads.URLDNS");
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//
//
//        System.out.print(Payload.class.getPackage().getName());
//        PayloadClasses = new ArrayList<Class<? extends Payload>>(payloadTypes);
//        ObservableList strList = FXCollections.observableArrayList();
//        for(int i=0; i<PayloadClasses.size(); i++) {
////            Class selected_vulpaylod_class = PayloadClasses.get(i);
//            String selected_vulpayload_name = PayloadClasses.get(i).getName().replace("vulpayload.","");
//            strList.add(selected_vulpayload_name);
//        }
//
//        //设置监听器
//        vullist_listview.setItems(strList);
//        vullist_listview.getSelectionModel().selectedItemProperty().addListener(new FXMLDocumentController.ListItemChangeListener());
//
//    }

    /**
     * 私有化构造防止被实例化
     */
    public ClassLoaderUtils(){}

    public URLClassLoader[] urlClassLoaders;
    public URLClassLoader urlClassLoader;


    public static void addJar(File jarPath) {
        try {
            URLClassLoader classLoader = (URLClassLoader)FXMLDocumentController.class.getClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }

            URL url = jarPath.toURI().toURL();
            method.invoke(classLoader, url);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }


    public static Object invoke(Object obj, String methodName, Object... parameters) {
        try {
            ArrayList classes = new ArrayList();
            if (parameters != null) {
                for(int i = 0; i < parameters.length; ++i) {
                    Object o1 = parameters[i];
                    if (o1 != null) {
                        classes.add(o1.getClass());
                    } else {
                        classes.add((Object)null);
                    }
                }
            }

            Method method = ClassLoaderUtils.getMethodByClass(obj.getClass(), methodName, (Class[])classes.toArray(new Class[0]));
            return method.invoke(obj, parameters);
        } catch (Exception var7) {
            return null;
        }
    }

    public static Method getMethodByClass(Class cs, String methodName, Class... parameters) {
        Method method = null;

        while(cs != null) {
            try {
                method = cs.getDeclaredMethod(methodName, parameters);
                cs = null;
            } catch (Exception var6) {
                cs = cs.getSuperclass();
            }
        }

        return method;
    }
}
