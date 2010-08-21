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
      <c:set var="first" value="0"></c:set>
      <c:set var="last" value="0"></c:set>
      <c:choose>
        <c:when test="${empty param.count}">
          <c:set var="qParam" value="" />
        </c:when>
        <c:otherwise>
          <c:set var="qParam" value="?count=${param.count}" />
        </c:otherwise>
      </c:choose>
      <c:forEach var="event" items="${it}" varStatus="status">
        <c:if test="${status.first}">
          <c:set var="first" value="${event.placeholderId}" />
        </c:if>
        <c:if test="${status.last}">
          <c:set var="last" value="${event.placeholderId}" />
        </c:if>
        <tr>
          <td>
            <a href="/api/event/${event.placeholderId}"><c:out value="${event.placeholderId}" /></a>
          </td>
          <td>
            <a href="/api/event/${event.placeholderId}"><c:out value="${event.universallyUniqueID}" /></a>
          </td>
          <td>
            <jsp:setProperty name="contentHelper" property="content" value="${event.eventContent}"/>
            <c:set var="content" value="${contentHelper.contentAsString}"></c:set>
            <a href="/api/event/${event.placeholderId}"><c:out value="${fn:substring(content,0,10)}"></c:out></a>
          </td>
          <td>
            <a href="/api/event/${event.placeholderId}"><c:out value="${event.creationDate}" /></a>
          </td>
        </tr>
      </c:forEach>
    </table>
    <div id="pagi">
      <c:if test="${not empty first}">
        <a id="pagination" href="/api/all-events/after/${first}${qParam}"><< Previous</a>
        <c:if test="${last!=1}">
          &nbsp;&nbsp;&nbsp;&nbsp;<a href="/api/all-events/before/${last}${qParam}" id="pagination">Next >></a>
        </c:if>
      </c:if>
    </div>

  </body>
</html>
