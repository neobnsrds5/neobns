package com.neo;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.neo.plantUMLServer.dto.LogDTO;
import com.neo.plantUMLServer.service.TraceUmlService;

@WebServlet("/trace")
public class TraceServlet extends HttpServlet {

	private final DatabaseUtil databaseUtil = new DatabaseUtil();
	private TraceUmlService traceUmlSevice;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String traceId = (String) request.getParameter("traceId");
		System.out.println("TRACE ID : " + traceId);
		List<LogDTO> list = null;

		try {
			list = databaseUtil.getTraceList(traceId);
			System.out.println(list.get(0).toString());
			System.out.println("trace method activated");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String plantUml = traceUmlSevice.buildUmlList(list);

		request.setAttribute("logList", list);
		request.setAttribute("imgSource", plantUml);
		System.out.println("list size : " + list.size());

		String url = "/template/trace.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);
		dispatcher.forward(request, response);
	}
}
