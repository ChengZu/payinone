package com.api.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePayRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.api.annotation.WebServlet;
import com.api.client.NotifySocket;
import com.api.dao.table.ZFBTrade;
import com.api.dao.tablehelp.ZFBTradeDBHelp;
import com.api.utils.Tools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ZFBServlet {
	static final Logger logger = LoggerFactory.getLogger(ZFBServlet.class);

	@WebServlet(name = "ZFBTradePayServlet", urlPatterns = {"/ZFBTradePay"}, method = {"post"})
	public void WXTradePayServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		ZFBTrade zfbtrade = (ZFBTrade) Tools.requestParamsToObject(request,
				ZFBTrade.class);

		if (zfbtrade.getBody() == null)
			return;

		zfbtrade.setOutTradeNo(ZFBTradeDBHelp.creatZFBOutTradeNo());
		AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder()
				.build();

		// (必填) 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码
		String authCode = request.getParameter("authCode");

		// 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
		String providerId = "2088100200300400500";
		ExtendParams extendParams = new ExtendParams();
		extendParams.setSysServiceProviderId(providerId);

		// 支付超时，线下扫码交易定义为5分钟
		String timeoutExpress = "5m";

		List<GoodsDetail> goodsDetailList = new Gson().fromJson(
				zfbtrade.getGoodsList(), new TypeToken<List<GoodsDetail>>() {
				}.getType());

		// 创建请求builder，设置请求参数
		// AlipayTradePayContentBuilder builder = new
		// AlipayTradePayContentBuilder()
		AlipayTradePayRequestBuilder builder = new AlipayTradePayRequestBuilder()
				.setOutTradeNo(zfbtrade.getOutTradeNo())
				.setSubject(zfbtrade.getSubject()).setAuthCode(authCode)
				.setTotalAmount(zfbtrade.getTotalAmount())
				.setStoreId(zfbtrade.getStoreId())
				.setUndiscountableAmount(zfbtrade.getUndiscountableAmount())
				.setBody(zfbtrade.getBody())
				.setOperatorId(zfbtrade.getOperatorId())
				.setExtendParams(extendParams)
				.setSellerId(zfbtrade.getSellerId())
				.setGoodsDetailList(goodsDetailList)
				.setTimeoutExpress(timeoutExpress);
		
		// 调用tradePay方法获取当面付应答
		AlipayF2FPayResult result = tradeService.tradePay(builder);
		switch (result.getTradeStatus()) {
		case SUCCESS:
			zfbtrade.setPayState("SUCCESS");
			ZFBTradeDBHelp.insertZFBTrade(zfbtrade);
			logger.info("支付宝支付成功: )");
			break;

		case FAILED:
			logger.error("支付宝支付失败!!!");
			break;

		case UNKNOWN:
			logger.error("系统异常，订单状态未知!!!");
			break;

		default:
			logger.error("不支持的交易状态，交易返回异常!!!");
			break;
		}
		out.println(result.getResponse().getBody());

	}

	@WebServlet(name = "ZFBTradePreCreateServlet", urlPatterns = {"/ZFBTradePreCreate"}, method = {"post"})
	public void ZFBTradePreCreateServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		ZFBTrade zfbtrade = (ZFBTrade) Tools.requestParamsToObject(request,
				ZFBTrade.class);
		zfbtrade.setOutTradeNo(ZFBTradeDBHelp.creatZFBOutTradeNo() + "");

		String time = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
		.format(new java.util.Date());
		
		zfbtrade.setCreateTime(time);
		
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
				.setNotifyUrl("http://118.190.44.144/zfb_notify.do")// 支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
				.setGoodsDetailList(goodsDetailList);

		AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
		switch (result.getTradeStatus()) {
		case SUCCESS:
			logger.info("支付宝预下单成功: )");

			ZFBTradeDBHelp.insertZFBTrade(zfbtrade);
			/*
			AlipayTradePrecreateResponse res = result.getResponse();
			
			String basePath = request.getSession().getServletContext()
					.getRealPath("/");
			String fileName = String.format("images%sqr-%s.png",
					File.separator, res.getOutTradeNo());
			String filePath = new StringBuilder(basePath).append(fileName)
					.toString();

			out.println("<img src=\"" + fileName + "\" />");
			ZxingUtils.getQRCodeImge(res.getQrCode(), 256, filePath);
			*/
			break;

		case FAILED:
			logger.error("支付宝预下单失败!!!");
			break;

		case UNKNOWN:
			logger.error("系统异常，预下单状态未知!!!");
			break;

		default:
			logger.error("不支持的交易状态，交易返回异常!!!");
			break;
		}
		out.println(result.getResponse().getBody());

		out.flush();
		out.close();
	}

	@WebServlet(name = "ZFBTradeQueryServlet", urlPatterns = {"/ZFBTradeQuery"}, method = {"post"})
	public void ZFBTradeQueryServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();

		if (request.getParameter("outTradeNo") != null) {

			AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder()
					.build();

			// (必填) 商户订单号，通过此商户订单号查询当面付的交易状态
			String outTradeNo = request.getParameter("outTradeNo");
			AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
					.setOutTradeNo(outTradeNo);
			AlipayF2FQueryResult result = tradeService
					.queryTradeResult(builder);
			switch (result.getTradeStatus()) {
			case SUCCESS:
				logger.info("查询返回该订单支付成功: )");

				AlipayTradeQueryResponse resp = result.getResponse();

				logger.info(resp.getTradeStatus());
				logger.info(resp.getFundBillList().toString());
				break;

			case FAILED:
				logger.error("查询返回该订单支付失败!!!");
				break;

			case UNKNOWN:
				logger.error("系统异常，订单支付状态未知!!!");
				break;

			default:
				logger.error("不支持的交易状态，交易返回异常!!!");
				break;
			}
			out.println(result.getResponse().getBody());
		}

		out.flush();
		out.close();

	}

	@WebServlet(name = "ZFBTradeRefundServlet", urlPatterns = {"/ZFBTradeRefund"}, method = {"post"})
	public void ZFBTradeRefundServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();

	    if(request.getParameter("outTradeNo")!=null){

	        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

	        // (必填) 外部订单号，需要退款交易的商户外部订单号
	        String outTradeNo = request.getParameter("outTradeNo");

	        // (必填) 退款金额，该金额必须小于等于订单的支付金额，单位为元
	        String refundAmount = request.getParameter("refundAmount");

	        // (可选，需要支持重复退货时必填) 商户退款请求号，相同支付宝交易号下的不同退款请求号对应同一笔交易的不同退款申请，
	        // 对于相同支付宝交易号下多笔相同商户退款请求号的退款交易，支付宝只会进行一次退款
	        String outRequestNo = request.getParameter("outRequestNo");

	        // (必填) 退款原因，可以说明用户退款原因，方便为商家后台提供统计
	        String refundReason = request.getParameter("refundReason");

	        // (必填) 商户门店编号，退款情况下可以为商家后台提供退款权限判定和统计等作用，详询支付宝技术支持
	        String storeId = request.getParameter("storeId");

	        AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder()
	                .setOutTradeNo(outTradeNo)
	                .setRefundAmount(refundAmount)
	                .setRefundReason(refundReason)
	                .setOutRequestNo(outRequestNo)
	                .setStoreId(storeId);

	        AlipayF2FRefundResult result = tradeService.tradeRefund(builder);
	        switch (result.getTradeStatus()) {
	            case SUCCESS:
	                logger.info("支付宝退款成功: )");
	                break;

	            case FAILED:
	                logger.error("支付宝退款失败!!!");
	                break;

	            case UNKNOWN:
	                logger.error("系统异常，订单退款状态未知!!!");
	                break;

	            default:
	                logger.error("不支持的交易状态，交易返回异常!!!");
	                break;
	        }
	        out.println(result.getResponse().getBody());
	    }
		out.flush();
		out.close();

	}

	@WebServlet(name = "ZFBTradeNotifyServlet", urlPatterns = {"/zfb_notify.do"}, method = {"post"})
	public void ZFBTradeNotifyServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();

		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter
				.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}


		// *
		boolean signVerified = false;
		try {
			signVerified = AlipaySignature.rsaCheckV1(params,
					Configs.getAlipayPublicKey(), "UTF-8", Configs.getSignType());
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		if (signVerified) {
			// TODO 验签成功则继续业务操作，最后在response中返回success
			out.print("success");
			
			
			ZFBTrade newZFBTrade = new ZFBTrade();
			newZFBTrade.setPayState(params.get("trade_status"));

			ZFBTrade oldZFBTrade = new ZFBTrade();
			oldZFBTrade.setOutTradeNo(params.get("out_trade_no"));

			ZFBTradeDBHelp.updateZFBTrade(oldZFBTrade, newZFBTrade);
			
			
			String outTradeNo = params.get("out_trade_no");
			//向客户端发送订单支付成功消息
			NotifySocket.zfbTradeSuccess(outTradeNo);
			
		} else {
			// TODO 验签失败则记录异常日志，并在response中返回failure.
			out.print("failure");
		}

		out.flush();
		out.close();

	}
	
	@WebServlet(name = "ZFBTradeGatewayServlet", urlPatterns = {"/zfb_gateway.do"}, method = {"post"})
	public void ZFBTradeGatewayServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();

		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter
				.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}


		// *
		boolean signVerified = false;
		try {
			signVerified = AlipaySignature.rsaCheckV1(params,
					Configs.getAlipayPublicKey(), "UTF-8", Configs.getSignType());
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (signVerified) {
			// TODO 验签成功则继续业务操作，最后在response中返回success
			out.print("success");
			//
		} else {
			// TODO 验签失败则记录异常日志，并在response中返回failure.
			out.print("failure");
		}

		out.flush();
		out.close();

	}
	

}
