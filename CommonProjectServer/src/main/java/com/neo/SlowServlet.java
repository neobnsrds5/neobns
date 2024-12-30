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

@WebServlet("/")
public class SlowServlet extends HttpServlet {

	private final DatabaseUtil databaseUtil = new DatabaseUtil();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		List<LogDTO> list = null;

		try {
			list = databaseUtil.getSlowList(1, 10);
		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("loglist", list);

		String url = "/template/index.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);
		dispatcher.forward(request, response);

	}

}
