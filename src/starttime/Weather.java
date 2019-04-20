package starttime;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import starttime.util.HttpRequestUtils;

/**
 * 统计Eclipse启动时间
 * 
 * @author xiaofei.xian 日期：2019年4月19日 上午11:47:53
 */
public class Weather implements IStartup {

	private static String WEATHER_URL = "http://apis.juhe.cn/simpleWeather/query";

	private static String IP_URL = "http://ip.tool.chinaz.com/ipbatch";

	private static String CITY_URL = "http://ip.taobao.com/service/getIpInfo.php";

	private static String KEY = "a59427475115740ba2da0dd7bc7cdd3d";

	@Override
	public void earlyStartup() {

		// 获取当前线程的Display，并且同步执行Runnable接口的run方法
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				// 获取eclipse的启动时间
				long start = Long.parseLong(System.getProperty("eclipse.startTime"));
				long launchTime = (System.currentTimeMillis() - start) / 1000;
				String message = "本次启动耗时:" + launchTime + "秒。";

				// 通过Display获得一个窗口（Shell对象）
				Shell shell = Display.getDefault().getActiveShell();
				
				// 获取IP
				String ip = getIp();
				if(null == ip) {
					MessageDialog.openInformation(shell, "傻逼提示", message + "\n未获取到当前设备的外网IP，,您老人家自求多福或者联网试试。");
					return;
				}
				//获取城市
				String city = getCity(ip);
				if(null == city) {
					MessageDialog.openInformation(shell, "傻逼提示", message + "\n未获取到当前所在城市，,您老人家自求多福或者联网试试。");
					return;
				}
				//获取天气
				MessageDialog.openInformation(shell, "傻逼提示", message + "\n" + getWeather(city));
			}
		});
	}

	/**
	 * 获取本机外网IP地址
	 * 
	 * @return
	 */
	public String getIp() {
		Document doc = null;
		try {
			// 获取IP地址
			doc = Jsoup.connect(IP_URL).validateTLSCertificates(false).get();
			Element element = doc.getElementsByClass("fz24").get(0);
			return element.text();
		} catch (IOException e) {
			return "";
		}
	}

	/**
	 * 根据IP获取城市名称
	 * 
	 * @param ip
	 * @return
	 */
	public String getCity(String ip) {
		String response = null;
		response = HttpRequestUtils.sendGet(CITY_URL,"ip="+ip);
		if(null == response) {
			return response;
		}
		JsonParser parse = new JsonParser(); // 创建json解析器
		JsonObject json = (JsonObject) parse.parse(response);
		return json.get("data").getAsJsonObject().get("city").getAsString();
	}

	public String getWeather(String city) {
		String response = null;
		try {
			response = HttpRequestUtils.sendGet(WEATHER_URL,
					"city=" + URLEncoder.encode(city, "UTF-8") + "&key=" + KEY);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		JsonParser parse = new JsonParser(); // 创建json解析器
		JsonObject json = (JsonObject) parse.parse(response);
		if (0 == json.get("error_code").getAsInt()) {
			String info = json.get("result").getAsJsonObject().get("realtime").getAsJsonObject().get("info")
					.getAsString();
			String temperature = json.get("result").getAsJsonObject().get("realtime").getAsJsonObject()
					.get("temperature").getAsString();
			String power = json.get("result").getAsJsonObject().get("realtime").getAsJsonObject().get("temperature")
					.getAsString();
			String direct = json.get("result").getAsJsonObject().get("realtime").getAsJsonObject().get("direct")
					.getAsString();
			String humidity = json.get("result").getAsJsonObject().get("realtime").getAsJsonObject().get("humidity")
					.getAsString();
			String aqi = json.get("result").getAsJsonObject().get("realtime").getAsJsonObject().get("aqi")
					.getAsString();
			return city + ":" + info + "；温度：" + temperature + "；风力：" + power + "；风向：" + direct + "；湿度：" + humidity
					+ "；空气质量指数：" + aqi;
		} else {
			return "未查询到" + city + "天气状况,您老人家自求多福吧。";
		}
	}

}
