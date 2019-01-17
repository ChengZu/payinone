package com.api.servlet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.api.annotation.WebServlet;
import com.api.client.NotifySocket;
import com.api.client.SocketClient;
import com.api.dao.table.Device;
import com.api.dao.table.WXTrade;
import com.api.dao.table.ZFBTrade;
import com.api.dao.tablehelp.DeviceDBHelp;
import com.api.dao.tablehelp.WXTradeDBHelp;
import com.api.dao.tablehelp.ZFBTradeDBHelp;
import com.api.init.StartInit;
import com.api.utils.Tools;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfigImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PayServlet {

	static final Logger logger = LoggerFactory.getLogger(PayServlet.class);

	@WebServlet(name = "PayServlet", urlPatterns = { "/Pay.html" }, method = { "get" })
	public void WXTradeRefundServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		String UA = this.getUserAgent(request);
		String mid = request.getParameter("mid");
		String code = request.getParameter("code");	
		
		if (isWXAndNoCode(response, UA, mid, code)) {
			return;
		}

		String openId=null;
		if (UA=="WX" && code != null) {
			openId=this.getWXOpenId(code);
			mid=request.getParameter("state");
			
		}
		
		if (mid == null){
			errorPage(response, "二维码错误");
			return;
		}
			

		// 检查设备是否在线
		Device device = getDevice(mid);
		
	
		if (device == null) {
			errorPage(response, "未知设备");
			return;
		}
		
		if (!checkDeviceOnline(device)) {
			errorPage(response, "设备离线或正在使用");
			return;
		}
		
		
		if (UA == "WX") {
			if(openId == null){
				errorPage(response, "获取用户信息错误");
				return;
			}
			createWXTrade(request, response, device, openId);

		} else if (UA == "ZFB") {
			createZFBTrade(response, device);

		} else {
			errorPage(response, "请使用支付宝或微信");
		}

	}
	
	private boolean isWXAndNoCode(HttpServletResponse response, String UA, String mid, String code) throws IOException{	
		if (UA == "WX" && mid != null && code == null) {
			// 跳转获取wxopid链接
			String redirectUrl = "http%3a%2f%2fapi.czios.com%2fPay.html";
			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
					+ WXPayConfigImpl.INSTANCE.getAppID()
					+ "&redirect_uri="
					+ redirectUrl
					+ "&response_type=code&scope=snsapi_base&state="
					+ mid
					+ "#wechat_redirect";
			Location(response, url);
			return true;
		} else {
			return false;
		}
	}
	

	private Device getDevice(String mid) {
		Device queryDevice = new Device();
		queryDevice.setId(mid);
		List<Object> deviceList = (List<Object>) DeviceDBHelp
				.getDevice(queryDevice);

		Device device = null;

		if (deviceList.size() > 0) {
			device = (Device) deviceList.get(0);
		}
		return device;
	}
	
	private boolean checkDeviceOnline(Device device) {
		if (device == null)
			return false;
		SocketClient mc = NotifySocket.getClientById(device.getParentId());
		if (mc != null && mc.isMachineOnUse(device.getId()))
			return true;
		return false;
	}

	private String getWXOpenId(String wxCode) {

		/* Custom DNS resolver */
		DnsResolver dnsResolver = new SystemDefaultDnsResolver() {
			@Override
			public InetAddress[] resolve(final String host)
					throws UnknownHostException {
				if (host.equalsIgnoreCase("ChengZu.os")) {
					return new InetAddress[] { InetAddress
							.getByName("127.0.0.1") };
				} else {
					return super.resolve(host);
				}
			}
		};

		BasicHttpClientConnectionManager connManager = new BasicHttpClientConnectionManager(
				RegistryBuilder
						.<ConnectionSocketFactory> create()
						.register("http",
								PlainConnectionSocketFactory.getSocketFactory())
						.register("https",
								SSLConnectionSocketFactory.getSocketFactory())
						.build(), null, /* Default ConnectionFactory */
				null, /* Default SchemePortResolver */
				dnsResolver /* Our DnsResolver */
		);

		HttpClient httpClient = HttpClientBuilder.create()
				.setConnectionManager(connManager).build();

		
		
		String oauth_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
				+ WXPayConfigImpl.INSTANCE.getAppID()
				+ "&secret="
				+ WXPayConfigImpl.INSTANCE.getSecert()
				+ "&code="
				+ wxCode
				+ "&grant_type=authorization_code";

		/* Should hit 127.0.0.1, regardless of DNS */
		HttpGet httpRequest = new HttpGet(oauth_url);
		String openId = null;
		try {
			// 使用DefaultHttpClient类的execute方法发送HTTP GET请求，并返回HttpResponse对象。
			HttpResponse httpResponse = httpClient.execute(httpRequest);// 其中HttpGet是HttpUriRequst的子类
			HttpEntity httpEntity = httpResponse.getEntity();
			String result = EntityUtils.toString(httpEntity);// 取出应答字符串
			Map<String, String> resultMap = new Gson().fromJson(result,
					new TypeToken<Map<String, String>>() {
					}.getType());
			openId = resultMap.get("openid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return openId;
	}

	private void createWXTrade(HttpServletRequest request,HttpServletResponse response, Device device, String openid){
		
		WXTrade wxtrade = new WXTrade();
		wxtrade.setDevice_info(device.getId());
		
		wxtrade.setBody(device.getTradeSubject());
		
		String amount=device.getTradeAmount().replace(".", "");
		//TODO
		//wxtrade.setTotal_fee(amount);
		wxtrade.setTotal_fee("1");
		
		wxtrade.setTrade_type("JSAPI");
		wxtrade.setOut_trade_no(WXTradeDBHelp.creatWXOutTradeNo());

		if (wxtrade.getDevice_info() == null)
			wxtrade.setDevice_info("1");

		if (wxtrade.getFee_type() == null)
			wxtrade.setFee_type("CNY");

		if (wxtrade.getTrade_type() == null)
			wxtrade.setTrade_type("NATIVE");

		if (wxtrade.getProduct_id() == null)
			wxtrade.setProduct_id("1");

		// 终端IP
		String spbill_creat_ip = getAddrIp(request);
		wxtrade.setSpill_create_ip(spbill_creat_ip);

		// 通知地址，必填
		String notify_url = WXPayConfigImpl.INSTANCE.getNotifyUrl();

		String create_time = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
				.format(new java.util.Date());

		wxtrade.setCreate_time(create_time);

		WXPay wxpay;

		try {
			wxpay = new WXPay(WXPayConfigImpl.INSTANCE);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("WXPay 初始化失败");
			return;
		}
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("body", wxtrade.getBody());
		data.put("out_trade_no", wxtrade.getOut_trade_no());
		data.put("device_info", wxtrade.getDevice_info());
		data.put("fee_type", wxtrade.getFee_type());
		data.put("total_fee", wxtrade.getTotal_fee());
		data.put("spbill_create_ip", spbill_creat_ip);
		data.put("notify_url", notify_url);
		data.put("trade_type", wxtrade.getTrade_type());
		data.put("product_id", wxtrade.getProduct_id());
		data.put("openid", openid);
		// data.put("time_expire", time_expire);

		Map<String, String> r = new HashMap<String, String>();
		try {
			r = wxpay.unifiedOrder(data);
			r.put("out_trade_no", wxtrade.getOut_trade_no());
			switch (r.get("result_code")) {
			case "SUCCESS":
				logger.info("微信预下单成功: )");

				WXTradeDBHelp.insertWXTrade(wxtrade);
				
				Map<String, String> payMap = new HashMap<String, String>();
				
				
				payMap.put("appId", WXPayConfigImpl.INSTANCE.getAppID());
				
				payMap.put("nonceStr", create_nonce_str());
				payMap.put("package", "prepay_id=" + r.get("prepay_id"));
				payMap.put("signType", "MD5");
				payMap.put("timeStamp", create_timestamp());
				String paySign = paySign(payMap, WXPayConfigImpl.INSTANCE.getKey());
				
				payMap.put("paySign", paySign);
				payMap.put("tradeAmount", device.getTradeAmount());
				payMap.put("tradeSubject", device.getTradeSubject());
				payMap.put("tradeBody", device.getTradeBody());

				render(response, payMap, "WEB-INF\\html\\wxPay.html");
				

				break;

			case "FAILED":
				logger.error("微信预下单失败!!!");
				errorPage(response, "微信预下单失败");
				break;

			case "UNKNOWN":
				logger.error("系统异常，预下单状态未知!!!");
				errorPage(response, "系统异常，预下单状态未知");
				break;

			default:
				logger.error("不支持的交易状态，交易返回异常!!!");
				errorPage(response, "不支持的交易状态，交易返回异常");
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private void createZFBTrade(HttpServletResponse response, Device device) throws IOException {
		
		
		ZFBTrade zfbtrade = new ZFBTrade();
		zfbtrade.setStoreId(device.getId());
		zfbtrade.setSubject(device.getTradeSubject());
		zfbtrade.setBody(device.getTradeBody());
		//TODO
		zfbtrade.setTotalAmount("0.01");
		//zfbtrade.setTotalAmount(device.getTradeAmount());

		zfbtrade.setOutTradeNo(ZFBTradeDBHelp.creatZFBOutTradeNo());

		String createTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
				.format(new java.util.Date());

		zfbtrade.setCreateTime(createTime);

		AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder()
				.build();

		// 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
		ExtendParams extendParams = new ExtendParams();
		extendParams.setSysServiceProviderId("2088100200300400500");

		// 支付超时，定义为120分钟
		String timeoutExpress = "120m";
		/*
		 * // 商品明细列表，需填写购买商品详细信息， List<GoodsDetail> goodsDetailList = new
		 * ArrayList<GoodsDetail>(); //
		 * 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
		 * GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "全麦小面包",
		 * 1500, 1); // 创建好一个商品后添加至商品明细列表 goodsDetailList.add(goods1);
		 * 
		 * // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.05元，购买了两件 GoodsDetail goods2 =
		 * GoodsDetail.newInstance("goods_id002", "黑人牙刷", 505, 2);
		 * goodsDetailList.add(goods2);
		 */

		// String
		// goodsList="[{\"goods_id\":\"goods_id001\",\"goods_name\":\"全麦小面包\",\"quantity\":\"1\",\"price\":\"15\"},{\"goods_id\":\"goods_id002\",\"goods_name\":\"黑人牙刷\",\"quantity\":\"2\",\"price\":\"5.05\"}]";
		List<GoodsDetail> goodsDetailList = new Gson().fromJson(
				zfbtrade.getGoodsList(), new TypeToken<List<GoodsDetail>>() {
				}.getType());

		// AlipayTradePrecreateContentBuilder builder = new
		// AlipayTradePrecreateContentBuilder()

		AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
				.setSubject(zfbtrade.getSubject())
				.setTotalAmount(zfbtrade.getTotalAmount())
				.setOutTradeNo(zfbtrade.getOutTradeNo())
				.setUndiscountableAmount(zfbtrade.getUndiscountableAmount())
				.setSellerId(zfbtrade.getSellerId())
				.setBody(zfbtrade.getBody())
				.setOperatorId(zfbtrade.getOperatorId())
				.setStoreId(zfbtrade.getStoreId())
				.setExtendParams(extendParams)
				.setTimeoutExpress(timeoutExpress)
				.setNotifyUrl("http://api.czios.com/zfb_notify.do")// 支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
				.setGoodsDetailList(goodsDetailList);

		AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);

		switch (result.getTradeStatus()) {
		case SUCCESS:
			logger.info("支付宝预下单成功: )");

			ZFBTradeDBHelp.insertZFBTrade(zfbtrade);
			
			Map<String, String> payMap = new HashMap<String, String>();
			payMap.put("tradeAmount", device.getTradeAmount());
			payMap.put("tradeSubject", device.getTradeSubject());
			payMap.put("tradeBody", device.getTradeBody());
			payMap.put("tradeUrl", result.getResponse().getQrCode());

			render(response, payMap, "WEB-INF\\html\\zfbPay.html");
			//Location(response, result.getResponse().getQrCode());

			break;

		case FAILED:
			logger.error("支付宝预下单失败!!!");
			errorPage(response, "支付宝预下单失败");
			break;

		case UNKNOWN:
			logger.error("系统异常，预下单状态未知!!!");
			errorPage(response, "系统异常，预下单状态未知");
			break;

		default:
			logger.error("不支持的交易状态，交易返回异常!!!");
			errorPage(response, "不支持的交易状态，交易返回异常");
			break;
		}

	}

	private void Location(HttpServletResponse response, String url) throws IOException{
		Map<String, String> arg = new HashMap<String, String>();
		arg.put("refreshUrl", url);
		render(response, arg, "WEB-INF\\html\\refreshUrl.html");
	}
	
	private void errorPage(HttpServletResponse response, String errorMsg) throws IOException{
		Map<String, String> arg = new HashMap<String, String>();
		arg.put("errorMsg", errorMsg);
		render(response, arg, "WEB-INF\\html\\errorPage.html");
	}
	
	private String getUserAgent(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		//logger.info("浏览器标识：" + userAgent);

		String agent = "UNKOWN";
		if (userAgent.indexOf("micromessenger") != -1) {
			agent = "WX";
		}else if (userAgent.indexOf("alipay") != -1) {
			agent = "ZFB";
		}
		return agent;
	}

	private String paySign(Map<String, String> pramas, String paternerKey) {
		String paySign = "";
		paySign += "appId=" + pramas.get("appId") + "&";
		paySign += "nonceStr=" + pramas.get("nonceStr") + "&";
		paySign += "package=" + pramas.get("package") + "&";
		paySign += "signType=MD5&";
		paySign += "timeStamp=" + pramas.get("timeStamp") + "&";
		paySign += "key=" + paternerKey;
		return Tools.MD5(paySign).toUpperCase();
	}

	private String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	private String create_nonce_str() {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String res = "";
		for (int i = 0; i < 32; i++) {
			Random rd = new Random();
			res += chars.charAt(rd.nextInt(chars.length() - 1));
		}
		return res;
	}

	private String getAddrIp(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	private void render(HttpServletResponse response,
			Map<String, String> pramas, String url) throws IOException {
		PrintWriter out = response.getWriter();
		String html = new String();
		String file = StartInit.APPLICATIONPATH + "\\" + url;
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String s = null;
			while ((s = br.readLine()) != null) {
				html += s + "\n";
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (pramas != null) {
			for (Entry<String, String> m : pramas.entrySet()) {
				String str = "{" + m.getKey() + "}";
				html = html.replace(str, m.getValue());
			}
		}
		out.print(html);
		out.flush();
		out.close();
	}
}
