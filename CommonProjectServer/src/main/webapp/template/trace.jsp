<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.neo.plantUMLServer.dto.LogDTO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>지연 어플리케이션 관리</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="A fully responsive premium admin dashboard template, Real Estate Management Admin Template" />
    <meta name="author" content="Techzaa" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css"/>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/plantuml-encoder/dist/plantuml-encoder.min.js"></script>
    
</head>
<body>
	
	<div style="justify-items: center; justify-content: center;" id="plantuml-container">
       <uml>
           <%= request.getAttribute("imgSource") != null ? request.getAttribute("imgSource") : "" %>
       </uml>
    </div>

    <script>
        $("uml").each(function() {
            var src = "//www.plantuml.com/plantuml/img/" + window.plantumlEncoder.encode($(this).text());
            $(this).replaceWith($('<img style="display:flex; justify-content:center; width: 100%;">').attr('src', src));
        });

        const plantumlContainer = document.getElementById("plantuml-container");
    </script>


    <table class="table table-striped table-centered">
        <thead>
            <tr>
                <th scope="col">타임스탬프</th>
                <th scope="col">추적 ID</th>
                <th scope="col">유저 ID</th>
                <th scope="col">레벨 문자열</th>
                <th scope="col">호출 클래스</th>
                <th scope="col">호출 메서드</th>
                <th scope="col">실행 결과</th>
            </tr>
        </thead>
        <tbody>
            <% 
                List<LogDTO> logList = (List<LogDTO>) request.getAttribute("logList");
				System.out.println("jsp loglist : " + logList);
                if (logList != null) {
                    for (LogDTO log : logList) {
            %>
            <tr>
                <!-- 타임스탬프 -->
                <td><%= log.getTimestmp() %></td>
                <!-- 추적 ID -->
                <td>
                    <% if (log.getTraceId() != null) { %>
                        <%= log.getTraceId() %>
                    <% } else { %>
                        N/A
                    <% } %>
                </td>
                <!-- 유저 ID -->
                <td><%= log.getUserId() != null ? log.getUserId() : "N/A" %></td>
                <!-- 레벨 문자열 -->
                <td><%= log.getLevelString() != null ? log.getLevelString() : "N/A" %></td>
                <!-- 호출 클래스 -->
                <td><%= log.getCallerClass() != null ? log.getCallerClass() : "N/A" %></td>
                <!-- 호출 메서드 -->
                <td><%= log.getCallerMethod() != null ? log.getCallerMethod() : "N/A" %></td>
                <!-- 실행 결과 -->
                <td><%= log.getExecuteResult() != null ? log.getExecuteResult() : "N/A" %></td>
            </tr>
            <% 
                    }
                } else {
            %>
            <tr>
                <td colspan="7">No logs found</td>
            </tr>
            <% 
                }
            %>
        </tbody>
    </table>
</body>
</html>
