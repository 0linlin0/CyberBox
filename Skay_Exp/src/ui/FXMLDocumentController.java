package ui;

import baselib.Result;
import config.Config;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import tools.ClassLoaderUtils;
import vulpayload.Payload;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.reflections.Reflections;

import static javafx.scene.text.TextAlignment.CENTER;

/**
 *
 * @author saban
 */
public class FXMLDocumentController implements Initializable {
    private double xOffset = 0;
    private double yOffset = 0;
    private final Calendar cal = Calendar.getInstance();
    List<Class<? extends Payload>> PayloadClasses = new ArrayList<>();
    private Class payload_attck = null;
    ArrayList lables = new ArrayList();
    ArrayList attck_args_list_lable = new ArrayList();
    ArrayList attck_args_list_textfield = new ArrayList();
    Result test_result = null;
    Result attck_result = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        draggable();
        updateTime();
        try {
            initvullist();
            test_lable.setStyle("-fx-background-color: #2F3136;");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        initattck_listview();
    }



    private void draggable() {
        drag_panel.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = drag_panel.getScene().getWindow().getX() - event.getScreenX();
                yOffset = drag_panel.getScene().getWindow().getY() - event.getScreenY();
            }
        });
        drag_panel.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                drag_panel.getScene().getWindow().setX(event.getScreenX() + xOffset);
                drag_panel.getScene().getWindow().setY(event.getScreenY() + yOffset);
            }
        });
    }

    private void updateTime() {
        cal.setTime(new Date());
        data_label.setText(cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + "\t"
                + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
    }

    @FXML
    public void exit() {
        System.exit(0);
    }
    //
    @FXML
    public void iconized() {
        ((Stage) drag_panel.getScene().getWindow()).setIconified(true);
    }

    @FXML
    public Pane drag_panel;

    @FXML
    public Label value_label;

    @FXML
    public Label data_label;

    @FXML
    public Label delay_label;


    @FXML
    public Pane delayPane;
    @FXML
    public AnchorPane vul_home_info ;
    @FXML
    public AnchorPane user_info;
    @FXML
    public AnchorPane set_info;
    @FXML
    public AnchorPane no_info;
    @FXML
    public AnchorPane attck_pane;

    @FXML
    public void openSettings(){
        chiudiTutti(vul_home_info,user_info,set_info,no_info,attck_pane);
        set_info.setVisible(true);
    }
    @FXML
    public void openHome(){
        chiudiTutti(vul_home_info,user_info,set_info,no_info,attck_pane);
        vul_home_info.setVisible(true);
    }

    @FXML
    public AnchorPane login_pane;
    public AnchorPane welcome_pane;
    public Label login_button;
    public TextField user_textfield;
    public TextField password_textfield;
    public void user_login_clicked(MouseEvent mouseEvent) {
//        String url = Config.LOGIN_URL;
//        HttpUtils http_test_check = new HttpUtils();
//        Connection.Response http_return = null;
//        Map<String, String> data = new HashMap<String,String>();
//        data.put("username",user_textfield.getText());
//        data.put("password",password_textfield.getText());
//        try {
//            http_return = http_test_check.post(url,data);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//        }
//        JSONObject jsonObject = JSON.parseObject(http_return.body().toString());
//        Config.USER_TOKEN = jsonObject.getString("token");
////        System.out.print(http_return.body());
//        System.out.print(Config.USER_TOKEN+"\n");

        Config.USER_TOKEN = "test_success";

        if(Config.USER_TOKEN != null){
            login_button.setStyle("-fx-background-color: #0E9654;");
            login_pane.setVisible(false);
            welcome_pane.setVisible(true);
        }else {
            password_textfield.setText("");
        }
    }
    @FXML
    public Label hello_lable;
    @FXML
    public void openUser() throws InstantiationException, IllegalAccessException {
//        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
//        int noThreads = currentGroup.activeCount();
//        Thread[] lstThreads = new Thread[noThreads];
//        currentGroup.enumerate(lstThreads);
//        try{
//            for (int i = 0; i < noThreads; i++) {
//                System.out.println("线程号：" + i + " = " + lstThreads[i].getName());
//            }
//        }catch (Exception e){
//
//        }
        chiudiTutti(vul_home_info,user_info,set_info,no_info,attck_pane);
        user_info.setVisible(true);
        if(check_user()){
            welcome_pane.setVisible(true);
            login_pane.setVisible(false);
            if(check_user_payload() == false) {
                hello_lable.setText("无权限使用");
            }

        }else {
            welcome_pane.setVisible(false);
            login_pane.setVisible(true);
        }
    }

    @FXML
    public void openAttck() throws InstantiationException, IllegalAccessException {
        if(check_user_payload()){
            chiudiTutti(vul_home_info,user_info,set_info,no_info,attck_pane);
            no_info.setVisible(true);
            attck_pane.setVisible(true);
            init_attck_args();
        }else {
            openUser();
        }
    }

    private Boolean check_user_payload() throws IllegalAccessException, InstantiationException {
//        Payload check_tmp = (Payload) payload_attck.newInstance();
//        String url = Config.CHECK_PAYLOAD_URL+"?plugin_id="+check_tmp.payload_info.get("payload_id");
////        String url = Config.LOG_URL+"?plugin_id="+check_tmp.payload_info.get("payload_id")+"&create_time=1590249445&mac_address=127.0.0.1";
//        HttpUtils http_test_check = new HttpUtils();
//        Map<String, String> headers = new HashMap<String,String>();
//        headers.put("Authorization",Config.USER_TOKEN);
//        Connection.Response http_return = null;
//        try {
//            http_return = http_test_check.get(url,headers);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//        }
////        System.out.print(http_return.body());
////        JSONObject jsonObject = JSON.parseObject(http_return.body().toString());
////        String msg = jsonObject.getString("msg");
//        if(http_return.body().equals("没有数据")){
//            return false;
//        }else{
//            if(http_return.body().equals("{\"status\":\"201\",\"msg\":\"token格式错误\"}")){
//                return false;
//            }else {
//                String url_log = Config.LOG_URL+"?plugin_id="+check_tmp.payload_info.get("payload_id")+"&create_time=1590249445&mac_address=127.0.0.1";
//                HttpUtils http_test_check_log = new HttpUtils();
//                Map<String, String> headers_log = new HashMap<String,String>();
//                headers_log.put("Authorization",Config.USER_TOKEN);
//                Connection.Response http_return_log = null;
//                try {
//                    http_return_log = http_test_check_log.get(url_log,headers_log);
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                }
//                System.out.print(http_return_log.body());
//                return true;
//            }
//        }

        return true;
    }
    private Boolean check_user() throws IllegalAccessException, InstantiationException {
//        String url = Config.CHECK_USER_URL;
//        HttpUtils http_test_check = new HttpUtils();
//        Map<String, String> headers = new HashMap<String,String>();
//        headers.put("Authorization",Config.USER_TOKEN);
//        Connection.Response http_return = null;
//        try {
//            http_return = http_test_check.get(url,headers);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//        }
//        System.out.print(http_return.body());
//        JSONObject jsonObject = JSON.parseObject(http_return.body().toString());
//        String msg = jsonObject.getString("msg");
//        if(msg.equals("true")){
//            return true;
//        }else{
//            return false;
//        }
        if(Config.USER_TOKEN == null){
            return false;
        }else {
            return !Config.USER_TOKEN.equals("2333");
        }

    }

    private void chiudiTutti(Pane... panes) {
        for (int i = 0; i < panes.length; i++) {
            panes[i].setVisible(false);
        }
    }

    public void openNote(MouseEvent mouseEvent) {

    }
    //attck 页面
    @FXML
    public Label test_lable;
    @FXML
    public Label attck_lable;
    @FXML
    public Label getshell_lable;

    @FXML
    public TextArea output_textarea;

    public HashMap<String, String> get_args(){
        ArrayList<Node> nodes = new ArrayList<Node>();
        ArrayList<Label> labels_node = new ArrayList<Label>();
        ArrayList<TextField> textfields_node = new ArrayList<TextField>();
        HashMap<String,String> payload_args = new HashMap<>();
        addAllDescendents(args_scroll_pane, nodes);
        for(int i=0;i<nodes.size();i++){
//            String node_type = getType(nodes.get(i));
            if(getType(nodes.get(i)).equals("class javafx.scene.control.TextField")){
                textfields_node.add((TextField) nodes.get(i));
            }
            if(getType(nodes.get(i)).equals("class javafx.scene.control.Label")){
                labels_node.add((Label) nodes.get(i));
            }
        }
        for (int ii=0;ii<labels_node.size();ii++){
            payload_args.put(labels_node.get(ii).getText(),textfields_node.get(ii).getText());
        }
        return payload_args;
    }
    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
        }
    }

    public static String getType(Object o){ //获取变量类型方法
        return o.getClass().toString(); //使用int类型的getClass()方法
    }


    public void test_cliecked(MouseEvent mouseEvent) {
        HashMap new_payload_args = get_args();
        test_lable.setStyle("-fx-background-color: #0E9654;");
        attck_lable.setStyle("-fx-background-color: #202225;");
        getshell_lable.setStyle("-fx-background-color: #2F3136;");

        output_textarea.setText("Testing.......   请等待.....\n");
        Runnable runner = ()->{
            Payload test_now_obj = null;
            try {
                test_now_obj = (Payload) payload_attck.newInstance();
                test_now_obj.payload_args = new_payload_args;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            try {
                test_result = test_now_obj.test();
            } catch (Exception e) {
                e.printStackTrace();
            }


        };


        new Thread(runner).start();  //注意：同一个线程不可以启动多次
        if(test_result.is_success) {
            output_textarea.setText(output_textarea.getText()+test_result.return_content+"\n");
        }else {
            output_textarea.setText(output_textarea.getText()+"漏洞不存在 ！\n");
        }

    }
    public void getshell_cliecked(MouseEvent mouseEvent) throws IOException, InterruptedException {
        test_lable.setStyle("-fx-background-color: #2F3136;");
        attck_lable.setStyle("-fx-background-color: #202225;");
        getshell_lable.setStyle("-fx-background-color: #0E9654;");
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);
        try{
            for (int i = 7; i < noThreads; i++) {
                System.out.println("线程号：" + i + " = " + lstThreads[i].getName());
                output_textarea.setText(output_textarea.getText()+"终止线程：" + i + " = " + lstThreads[i].getName()+"\n");
                lstThreads[i].stop();
            }
        }catch (Exception e){

        }

//        HashMap new_payload_args = get_args();
//        test_lable.setStyle("-fx-background-color: #2F3136;");
//        attck_lable.setStyle("-fx-background-color: #202225;");
//        getshell_lable.setStyle("-fx-background-color: #0E9654;");
//
//        output_textarea.setText("9000 端口监听中.....\n");
//        Runnable runner = ()->{
//            Payload attck_now_obj = null;
//            try {
//                attck_now_obj = (Payload) payload_attck.newInstance();
//                attck_now_obj.payload_args = new_payload_args;
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            Result attck_result = null;
//            try {
//                attck_result = attck_now_obj.execution();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            if (attck_result.is_success){
//                if(attck_result.is_echo && attck_result.is_success){
//                    output_textarea.setText(output_textarea.getText()+attck_result.return_content+"\n");
//                }else{
//                    output_textarea.setText(output_textarea.getText()+"命令已成功执行，此漏洞不支持回显\n");
//                }
//
//            }else {
//                output_textarea.setText(output_textarea.getText()+"命令未成功执行\n");
//            }
//
//        };
//
//        new Thread(runner).start();  //注意：同一个线程不可以启动多次
    }
    public void attck_cliecked(MouseEvent mouseEvent) throws Exception {
        HashMap new_payload_args = get_args();
        test_lable.setStyle("-fx-background-color: #2F3136;");
        attck_lable.setStyle("-fx-background-color: #0E9654;");
        getshell_lable.setStyle("-fx-background-color: #2F3136;");
        //2333
        output_textarea.setText("Attcking.......   请等待.....\n");
        Runnable runner = ()->{
            Payload attck_now_obj = null;
            try {
                attck_now_obj = (Payload) payload_attck.newInstance();
                attck_now_obj.payload_args = new_payload_args;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            try {
                attck_result = attck_now_obj.execution();
            } catch (Exception e) {
                e.printStackTrace();
            }




        };

        new Thread(runner).start();  //注意：同一个线程不可以启动多次
        if(attck_result != null){
            if (attck_result.is_success){
                if(attck_result.is_echo && attck_result.is_success){
                    output_textarea.setText(output_textarea.getText()+attck_result.return_content+"\n");
                }else{
                    output_textarea.setText(output_textarea.getText()+"命令已成功执行，此漏洞不支持回显\n");
                }

            }else {
                output_textarea.setText(output_textarea.getText()+"命令未成功执行\n");
            }
        }else {
            output_textarea.setText(output_textarea.getText()+"命令未成功执行\n");
        }
    }

    @FXML
    public AnchorPane args_scroll_pane;
    public void init_attck_args() throws IllegalAccessException, InstantiationException {
//        payload_attck
        Payload attck_now_obj = (Payload) payload_attck.newInstance();
//        HashMap<String,String> payload_info_attck = attck_now_obj.payload_info;
        HashMap<String,String> payload_args_attck = attck_now_obj.payload_args;
        if(attck_args_list_lable.size()>0){
            for (int tmp = 0;tmp<attck_args_list_lable.size();tmp++){
                Label old_lable = (Label) attck_args_list_lable.get(tmp);
                old_lable.setText("");
                args_scroll_pane.getChildren().remove(old_lable);
            }
            attck_args_list_lable.clear();
        }
        if(attck_args_list_textfield.size()>0){
            for (int tmp = 0;tmp<attck_args_list_textfield.size();tmp++){
                TextField old_text = (TextField) attck_args_list_textfield.get(tmp);
                old_text.setText("");
                args_scroll_pane.getChildren().remove(old_text);
            }
            attck_args_list_textfield.clear();
        }
        int ai = 0;
        for (Map.Entry<String, String> entry : payload_args_attck.entrySet()) {
            Label args_lable = new Label();
            attck_args_list_lable.add(args_lable);
            args_lable.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
            args_lable.setText(entry.getKey());
            args_lable.setLayoutX(36.0);
            args_lable.setLayoutY(31.0+ai*46);
            System.out.print(ai+"lable");
            args_lable.setPrefHeight(18.0);
            args_lable.setPrefWidth(90.0);
            args_lable.setTextAlignment(CENTER);
            args_lable.setTextFill(Paint.valueOf("#0f9d58"));
            args_lable.setFont(Font.font("Arial", FontWeight.BOLD ,15));
            args_scroll_pane.getChildren().add(args_lable);

            TextField args_textfield = new TextField();
            attck_args_list_textfield.add(args_textfield);
            args_textfield.setLayoutX(125.0);
            args_textfield.setLayoutY(31.0+ai*46);
            System.out.print(ai+"text");
            args_textfield.setPrefHeight(27.0);
            args_textfield.setPrefWidth(561.0);
            args_textfield.setStyle("-fx-background-color: #202225;-fx-border-color: #2F3136;-fx-text-fill:white");
            args_scroll_pane.getChildren().add(args_textfield);
            ai++;

        }


        ScrollPane sp = new ScrollPane();
        sp.setStyle("-fx-background: #202225;-fx-border-color: #202225;-fx-background-color:#202225;-fx-progress-color: #202225; ");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        sp.setContent(args_scroll_pane);
        no_info.getChildren().add(sp);


    }

    //vullist 相关
    @FXML
    public ListView vullist_listview;
    public void initvullist() {
        String basePath = System.getProperty("user.dir")+ File.separator+"plugins"+ File.separator;
        File file = new File(basePath);
        // get the folder list
        File[] arrayfilename = file.listFiles();
        for(int i=0;i<arrayfilename.length;i++) {
            // only take file name
        }
        ClassLoaderUtils classLoaderUtils = new ClassLoaderUtils();
        final Reflections reflections = new Reflections(Payload.class.getPackage().getName());
        final Set<Class<? extends Payload>> payloadTypes = reflections.getSubTypesOf(Payload.class);


        try {
            for (int i = 0; i <arrayfilename.length; i++) {
                  ClassLoaderUtils.addJar(arrayfilename[i]);
                System.out.println(arrayfilename[i].getAbsolutePath());
            }
            for (int i = 0;i < arrayfilename.length;i++){
                try {
                    String theclassname  = "vulpayload."+arrayfilename[i].getName().substring(0,arrayfilename[i].getName().indexOf("."));

                    payloadTypes.add((Class<? extends Payload>) classLoaderUtils.getClass().getClassLoader().loadClass(theclassname));
                }catch (Exception e){

                }
            }
//            Class.forName("ysoserial.payloads.URLDNS");

        }catch (Exception e){
            e.printStackTrace();
        }


        System.out.print(Payload.class.getPackage().getName());

        PayloadClasses = new ArrayList<Class<? extends Payload>>(payloadTypes);
        ObservableList strList = FXCollections.observableArrayList();
        for(int i=0; i<PayloadClasses.size(); i++) {
            String selected_vulpayload_name = PayloadClasses.get(i).getName().replace("vulpayload.","");
            strList.add(selected_vulpayload_name);
        }

        //设置监听器
        vullist_listview.setItems(strList);
        vullist_listview.getSelectionModel().selectedItemProperty().addListener(new ListItemChangeListener());

    }

    //设置左半边漏洞详情页面
    @FXML
    public Label payload_name_lable;
    @FXML
    public Label module_name_lable;
    @FXML
    public Label version_lable;
    @FXML
    public Label numbering_lable;
    @FXML
    public Label cvss_lable;
    @FXML
    public Label affect_lable;
    @FXML
    public TextArea overview_textarea;

    @FXML
    public Label author_name_lable;
    @FXML
    public Label author_mail_lable;
    @FXML
    public Label author_date_lable;
    @FXML
    public Label author_website_lable;
    @FXML
    public Pane exec_args_pane;



    private class ListItemChangeListener implements ChangeListener<Object> {

        @Override
        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
            Class selected_vulpaylod_class = null;
            Class selected_vulpaylod_class_tmp;
            for(int i=0; i<PayloadClasses.size(); i++) {
                selected_vulpaylod_class_tmp = PayloadClasses.get(i);
                String selected_vulpayload_name = PayloadClasses.get(i).getName().replace("vulpayload.","");
                if(newValue.toString().equals(selected_vulpayload_name)) {
                    selected_vulpaylod_class = selected_vulpaylod_class_tmp;
                    payload_attck = selected_vulpaylod_class_tmp;
                    break;
                }
            }
            Payload selected_vulpaylod_obj = null;
            try {
                selected_vulpaylod_obj = (Payload) selected_vulpaylod_class.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            HashMap<String,String> payload_info_selected = selected_vulpaylod_obj.payload_info;
            HashMap<String,String> payload_args_selected = selected_vulpaylod_obj.payload_args;
            System.out.print(payload_info_selected.get("payload_name")+"---"+selected_vulpaylod_class.getName()+"==="+payload_args_selected.size()+"\n");
            payload_name_lable.setText(payload_info_selected.get("payload_name"));
            module_name_lable.setText(payload_info_selected.get("payload_module"));
            version_lable.setText(payload_info_selected.get("payload_affect_version"));
            numbering_lable.setText(payload_info_selected.get("payload_number"));
            cvss_lable.setText(payload_info_selected.get("payload_cvss"));
            affect_lable.setText(payload_info_selected.get("payload_affect"));
            overview_textarea.setText(payload_info_selected.get("payload_overview"));

            author_name_lable.setText(payload_info_selected.get("payload_author_id"));
            author_mail_lable.setText(payload_info_selected.get("payload_author_email"));
            author_date_lable.setText(payload_info_selected.get("payload_finish_time"));
            author_website_lable.setText(payload_info_selected.get("payload_author_website"));


//                System.out.print(lables.size()+" lables.size\n");
            for (int tmp = 0;tmp<lables.size();tmp++){
                Label old_lable = (Label) lables.get(tmp);
                old_lable.setText("");
                exec_args_pane.getChildren().remove(old_lable);
            }
            lables.clear();

            int ai = 0;
            for (Map.Entry<String, String> entry : payload_args_selected.entrySet()) {
                System.out.print(payload_info_selected.get("payload_name")+"---"+entry.getKey()+" :: "+entry.getValue()+"\n");
                Label args_lable = new Label();
                args_lable.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
                args_lable.setText(entry.getKey()+" : "+ entry.getValue());
                args_lable.setLayoutX(20.0);
                args_lable.setLayoutY(43.0+ai*30);
//                System.out.print(ai);
                args_lable.setPrefHeight(17.0);
                args_lable.setPrefHeight(90.0);
                args_lable.setTextAlignment(CENTER);
                args_lable.setTextFill(Paint.valueOf("#0f9d58"));

                exec_args_pane.getChildren().add(args_lable);

                lables.add(args_lable);
                ai++;
            }

        }

    }



    @FXML
    public Label volume;

    //NC相关

}
