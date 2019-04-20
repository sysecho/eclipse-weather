package starttime;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import starttime.util.HttpRequestUtils;

/**
 * 统计Eclipse启动时间
 * 
 * @author xiaofei.xian 日期：2019年4月19日 上午11:47:53
 */
public class StartUpTimeShow implements IStartup {

  private static String CITY = "深圳";
  
  private static String URL = "http://apis.juhe.cn/simpleWeather/query";
  
  private static String KEY = "a59427475115740ba2da0dd7bc7cdd3d";
  
  @Override
  public void earlyStartup() {
    
    // 获取当前线程的Display，并且同步执行Runnable接口的run方法
    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        // 获取eclipse的启动时间
        long start = Long.parseLong(System.getProperty("eclipse.startTime"));
        long launchTime = (System.currentTimeMillis() - start)/1000;
        String message = "本次启动耗时:" + launchTime + "秒。";
        
        //获取天气
        String weather = getWeather(CITY);
        
        // 通过Display获得一个窗口（Shell对象）
        Shell shell = Display.getDefault().getActiveShell();
        // 使用得到的shell，使用MessageDialog打开一个信息对话框
        MessageDialog.openInformation(shell, "傻逼提示", message+"\n"+weather);
      }
    });
  }
  
  public String getWeather(String city) {
    String response = null;
	try {
		response = HttpRequestUtils.sendGet("http://apis.juhe.cn/simpleWeather/query", "city="+URLEncoder.encode(CITY, "UTF-8")+"&key="+KEY);
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
    JsonParser parse =new JsonParser();  //创建json解析器
    JsonObject json =(JsonObject) parse.parse(response);
    if(0 == json.get("error_code").getAsInt()) {
      String info = json.get("result").getAsJsonObject().get("realtime").getAsJsonObject().get("info").getAsString();
      String temperature = json.get("result").getAsJsonObject().get("realtime").getAsJsonObject().get("temperature").getAsString();
      String power = json.get("result").getAsJsonObject().get("realtime").getAsJsonObject().get("temperature").getAsString();
      String direct = json.get("result").getAsJsonObject().get("realtime").getAsJsonObject().get("direct").getAsString();
      String humidity = json.get("result").getAsJsonObject().get("realtime").getAsJsonObject().get("humidity").getAsString();
      String aqi = json.get("result").getAsJsonObject().get("realtime").getAsJsonObject().get("aqi").getAsString();
      return CITY+":"+info+"；温度："+temperature+"；风力："+power+"；风向："+direct+"；湿度："+humidity+"；空气质量指数："+aqi;
    }else {
      return "未查询到"+CITY+"天气状况,你自求多福吧。";
    }
  }

}
