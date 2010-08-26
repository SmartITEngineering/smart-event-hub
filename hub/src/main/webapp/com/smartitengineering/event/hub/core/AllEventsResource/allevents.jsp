<%--
    Document   : events
    Created on : Jul 22, 2010, 6:07:01 PM
    Author     : kaisar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
  "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="contentHelper" class="com.smartitengineering.event.hub.core.ContentHelper" scope="page"/>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Events</title>
    <link type="text/css" rel='stylesheet' href='/css/channel.css' />
    <script type="text/javascript" src="/javascripts/jquery-1.4.2.js"></script>
    <script type="text/javascript" src="/javascripts/siteljquerylib.js"></script>
    <c:choose>
      <c:when test="${empty param.count}">
        <c:set var="qParam" value="" />
      </c:when>
      <c:otherwise>
        <c:set var="qParam" value="?count=${param.count}" />
      </c:otherwise>
    </c:choose>
    <script type="text/javascript">
      var url="/api/all-events/frags${qParam}";
      $(document).ready(function(){
        $("#content").pagination(url, "pagi");
      });
    </script>
  </head>
  <body>
    <div id="content"></div>
  </body>
</html>
