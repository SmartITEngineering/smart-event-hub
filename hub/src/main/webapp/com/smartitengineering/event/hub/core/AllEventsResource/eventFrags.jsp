<%-- 
    Document   : event-frags
    Created on : Aug 25, 2010, 12:10:22 PM
    Author     : kaisar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
  "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="contentHelper" class="com.smartitengineering.event.hub.core.ContentHelper" scope="page"/>
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
<div>
<h1 align="center">Events</h1>
<c:if test="${not empty it}">
  <div>
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
      <c:forEach var="event" items="${it}" varStatus="status">
        <c:if test="${status.first}">
          <c:set var="first" value="${event.placeholderId}" />
        </c:if>
        <c:if test="${status.last}">
          <c:set var="last" value="${event.placeholderId}" />
        </c:if>
        <div id="row${status.index}" class="row_of_list">
          <tr>
            <td id="eventName${status.index}" class="eventName"><a href="/api/event/${event.placeholderId}"><c:out value="${event.placeholderId}" /></a></td>
            <td id="uuId${status.index}" class="universallyUniqueId"><a href="/api/event/${event.placeholderId}"><c:out value="${event.universallyUniqueID}" /></a></td>
            <jsp:setProperty name="contentHelper" property="content" value="${event.eventContent}"/>
            <c:set var="content" value="${contentHelper.contentAsString}"></c:set>
            <td id="eventContent${status.index}" class="eventContent"><a href="/api/event/${event.placeholderId}"><c:out value="${fn:substring(content,0,10)}"></c:out></a></td>
            <td id="creationDate${status.index}" class="creationDate"><a href="/api/event/${event.placeholderId}"><c:out value="${event.creationDate}" /></a></td>
          </tr>
        </div>
      </c:forEach>
    </table>
  </div>
</c:if>
</div>
<br>
<div id="pagi" class="navigationContainer">
  <a id="pagination" href="/api/all-events/after/${first}/frags${qParam}"><< Previous</a>
  <a href="/api/all-events/before/${last}/frags${qParam}" id="pagination">Next >></a>
</div>
