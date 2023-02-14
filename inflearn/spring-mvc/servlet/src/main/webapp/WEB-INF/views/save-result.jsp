<%@ page import="me.rgunny.servlet.domain.member.Member" %><%--
  Created by IntelliJ IDEA.
  User: rgunny
  Date: 2023/02/14
  Time: 9:08 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
성공
<ul>
<%--    <li>id=<%=((Member)request.getAttribute("member")).getId()%></li>--%>
<%--    <li>username=<%=((Member)request.getAttribute("member")).getUsername()%></li>--%>
<%--    <li>age=<%=((Member)request.getAttribute("member")).getAge()%></li>    --%>
<%--    jsp가 제공하는 문법 ${} : property 접근법 -> request 의 attribute 에 담긴 데이터를 편리하게 조회 --%>
    <li>id=${member.id}</li>
    <li>username=${member.username}</li>
    <li>age=${member.age}</li>
</ul>
<a href="/index.html">메인</a>
</body>
</html>
