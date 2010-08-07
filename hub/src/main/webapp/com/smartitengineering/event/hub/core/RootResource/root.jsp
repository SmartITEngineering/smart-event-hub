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
    <title>Home</title>
    <link type="text/css" rel='stylesheet' href='/css/channel.css' />
  </head>
  <body>
    
    <a href="api${it}">Link of events</a><br>
    <a href="api${channelLink}">Link of channels</a>
  </body>
</html>
