package com.api.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.api.annotation.WebServlet;
import com.api.dao.table.Device;
import com.api.dao.tablehelp.DeviceDBHelp;
import com.api.utils.Tools;

public class DeviceServlet {

	@WebServlet(name = "AddDeviceServlet", urlPatterns = {"/AddDevice.admin"})
	public void AddDeviceServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		Device device = (Device) Tools.requestParamsToObject(request, Device.class);
		DeviceDBHelp.insertDevice(device);
		out.print(device);

		out.flush();
		out.close();

	}

	@WebServlet(name = "DeleteDeviceServlet", urlPatterns = {"/DeleteDevice.admin"})
	public void DeleteDeviceServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		Device device = (Device) Tools.requestParamsToObject(request, Device.class);
		DeviceDBHelp.deleteDevice(device);
		out.print(device);

		out.flush();
		out.close();
	}

	@WebServlet(name = "GetDeviceServlet", urlPatterns = {"/GetDevice.admin"})
	public void GetDeviceServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		Device device = (Device) Tools.requestParamsToObject(request, Device.class);
		out.print(DeviceDBHelp.getDevice(device));

		out.flush();
		out.close();
	}
	
	@WebServlet(name = "GetDeviceListServlet", urlPatterns = {"/GetDeviceList.admin"})
	public void GetDeviceListServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		int start = 0, num = 10;
		try {
			if (request.getParameter("start") != null)
				start = Integer.parseInt(request.getParameter("start"));
			if (request.getParameter("num") != null)
				num = Integer.parseInt(request.getParameter("num"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		out.println(DeviceDBHelp.getDeviceList(start, num));
		out.flush();
		out.close();
	}

	@WebServlet(name = "UpdateDeviceServlet", urlPatterns = {"/UpdateDevice.admin"})
	public void UpdateDeviceServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		Device newDevice = (Device) Tools.requestParamsToObject(request, Device.class);
		Device oldDevice = new Device();
		oldDevice.setId(newDevice.getId());
		DeviceDBHelp.updateDevice(oldDevice, newDevice);
		out.print(newDevice);

		out.flush();
		out.close();
	}
	
	@WebServlet(name = "GetDeviceCountServlet", urlPatterns = {"/GetDeviceCount.admin"})
	public void GetDeviceCountServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();


		
		out.print(DeviceDBHelp.getDeviceCount());

		out.flush();
		out.close();
	}

}