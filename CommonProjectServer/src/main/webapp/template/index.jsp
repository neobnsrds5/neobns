<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.neo.plantUMLServer.dto.LogDTO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>지연 어플리케이션 관리</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css"/>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>
<body>

    <table class="table table-striped table-centered">
        <thead>
            <tr>
                <th scope="col">타임스탬프</th>
                <th scope="col">추적ID</th>
                <th scope="col">IP주소</th>
                <th scope="col">종류</th>
                <th scope="col">URI/쿼리</th>
                <th scope="col">소요시간(ms)</th>
                <th scope="col">사용자ID</th>
            </tr>
        </thead>
        <tbody>
            <% 
                List<LogDTO> logList = (List<LogDTO>) request.getAttribute("loglist");
                if (logList != null) {
                    for (LogDTO log : logList) {
            %>
            <tr>
                <td><%= log.getTimestmp() %></td>
                <td>
                    <% if (log.getTraceId() != null) { %>
                        <a href="/trace?traceId=<%= log.getTraceId() %>" target="_blank"><%= log.getTraceId() %></a>
                    <% } else { %>
                        N/A
                    <% } %>
                </td>
                <td><%= log.getCallerClass() %></td>
                <td><%= log.getCallerMethod() %></td>
                <td>
                    <% if (log.getUri() != null) { %>
                        <%= log.getUri() %>
                    <% } else if (log.getQuery() != null) { %>
                        <%= log.getQuery() %>
                    <% } %>
                </td>
                <td><%= log.getExecuteResult() %></td>
                <td><%= log.getUserId() %></td>
            </tr>
            <% 
                    }
                }
            %>
        </tbody>
    </table>
</body>
</html>
