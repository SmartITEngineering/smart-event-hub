<%--
    Document   : event
    Created on : Aug 7, 2010, 10:07:01 AM
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
  </head>
  <body>
    <b>Placeholder Id</b>          <c:out value="${it.placeholderId}" /><br>
    <b>Universally Unique Id</b>        <c:out value="${it.universallyUniqueID}" /><br>
    <jsp:setProperty name="contentHelper" property="content" value="${it.eventContent}"/>
    <c:set var="content" value="${contentHelper.contentAsString}"></c:set>
    <b>Event Content</b>        <c:out value="${content}"/><br>
    <b>Creation Date</b>        <c:out value="${it.creationDate}" /><br>
    <a href="/api/all-channels"><< Back</a>
  </body>
</html>
