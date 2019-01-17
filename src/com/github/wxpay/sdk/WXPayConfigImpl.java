package com.github.wxpay.sdk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;

public class WXPayConfigImpl extends WXPayConfig {
	private byte[] certData;
	private String appID;
	private String mchID;
	private String key;
	private String secert;
	private String notifyUrl;
	private int httpConnectTimeoutMs;
	private int httpReadTimeoutMs;
	private String primaryDomain;
	private String alternateDomain;
	private int reportWorkerNum;
	private int reportBatchSize;
	private Configuration configs;
	public static WXPayConfigImpl INSTANCE=null;

	private WXPayConfigImpl(String filename) throws Exception {
		try {
			configs = new PropertiesConfiguration(filename);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		if (configs == null) {
			throw new IllegalStateException(
					"can`t find file by path: wxinfo.properties");
		}

		appID = configs.getString("appID");
		mchID = configs.getString("mchID");
		key = configs.getString("key");
		secert = configs.getString("secert");
		notifyUrl = configs.getString("notifyUrl");
		httpConnectTimeoutMs = configs.getInt("httpConnectTimeoutMs");
		httpReadTimeoutMs = configs.getInt("httpReadTimeoutMs");
		primaryDomain = configs.getString("primaryDomain");
		alternateDomain = configs.getString("alternateDomain");
		reportWorkerNum = configs.getInt("reportWorkerNum");
		reportBatchSize = configs.getInt("reportBatchSize");
		

		String certPath = configs.getString("certPath");
		File file = new File(certPath);
		InputStream certStream = new FileInputStream(file);
		this.certData = new byte[(int) file.length()];
		certStream.read(this.certData);
		certStream.close();
	}

	public static WXPayConfigImpl Init(String filename) throws Exception {
		if (INSTANCE == null) {
			synchronized (WXPayConfigImpl.class) {
				if (INSTANCE == null) {
					INSTANCE = new WXPayConfigImpl(filename);
				}
			}
		}
		return INSTANCE;
	}

	public String getAppID() {
		return this.appID;
	}

	public String getMchID() {
		return this.mchID;
	}

	public String getKey() {
		return this.key;
	}

	public String getNotifyUrl() {
		return this.notifyUrl;
	}

	public InputStream getCertStream() {
		ByteArrayInputStream certBis;
		certBis = new ByteArrayInputStream(this.certData);
		return certBis;
	}

	public int getHttpConnectTimeoutMs() {
		return this.httpConnectTimeoutMs;
	}

	public int getHttpReadTimeoutMs() {
		return this.httpReadTimeoutMs;
	}

	IWXPayDomain getWXPayDomain() {
		return WXPayDomainSimpleImpl.instance();
	}

	public String getPrimaryDomain() {
		return this.primaryDomain;
	}

	public String getAlternateDomain() {
		return this.alternateDomain;
	}

	@Override
	public int getReportWorkerNum() {
		return this.reportWorkerNum;
	}

	@Override
	public int getReportBatchSize() {
		return this.reportBatchSize;
	}

	public String getSecert() {
		return secert;
	}

	public void setSecert(String secert) {
		this.secert = secert;
	}
}
