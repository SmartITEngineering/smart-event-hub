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
  </head>
  <body>
    <h1 align="center">Events</h1>
    <table align="center">
      <th>
        Placeholder Id
      </th>
      <th>
        Universally Unique Id
      </th>
      <th>
        Event Content
      </th>
      <th>
        Creation Date
      </th>
      <c:set var="count" value="0"></c:set>
      <c:forEach var="event" items="${it}">
        <tr>
          <td>
            <c:out value="${event.placeholderId}" />
            <c:set var="nextUrl" value="${event.placeholderId}"></c:set>
          </td>
          <c:if test="${count==0}">
            <c:set var="previousUrl" value="${event.placeholderId}"></c:set>
            <c:set var="count" value="2"></c:set>
          </c:if>
          <td>
            <c:out value="${event.universallyUniqueID}" />
          </td>
          <td>
            <jsp:setProperty name="contentHelper" property="content" value="${event.eventContent}"/>
            <c:set var="content" value="${contentHelper.contentAsString}"></c:set>
            <c:out value="${fn:substring(content,0,10)}"></c:out>
          </td>
          <td>
            <c:out value="${event.creationDate}" />
          </td>
        </tr>
      </c:forEach>
    </table>
    <div id="pagi">
      <c:if test="${not empty nextUrl}">
        <a id="pagination" href="/api/events/after/${previousUrl}"><< Previous</a>
        <c:if test="${nextUrl!=1}">
          &nbsp;&nbsp;&nbsp;&nbsp;<a href="/api/events/before/${nextUrl}" id="pagination">Next >></a>
        </c:if>
      </c:if>
    </div>

  </body>
</html>
