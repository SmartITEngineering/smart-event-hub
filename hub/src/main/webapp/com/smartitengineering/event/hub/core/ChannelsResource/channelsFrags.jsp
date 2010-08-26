<%-- 
    Document   : channelFrags
    Created on : Aug 25, 2010, 3:44:46 PM
    Author     : kaisar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
<h1 align="center">Channels</h1>
<div id="eventFragRoot">
  <div id="eventPaginatedList" class="listAsTable">
    <table align="center">
      <tr>
        <th>Position</th>
        <th>Name</th>
        <th>Description</th>
        <th>Auth Token</th>
        <th>Events</th>
      </tr>
      <c:forEach var="channel" items="${it}" varStatus="status">
        <c:if test="${status.first}">
          <c:set var="first" value="${channel.position}" />
        </c:if>
        <c:if test="${status.last}">
          <c:set var="last" value="${channel.position}" />
        </c:if>
        <tr>
          <td>
            <c:out value="${channel.position}" />
          </td>
          <td>
            <a href="/api/channels/${channel.name}"><c:out value="${channel.name}" /></a>
          </td>
          <td>
            <c:set var="description" value="${channel.description}"></c:set>
            <a href="/api/channels/${channel.name}"><c:out value="${fn:substring(description,0,10)}"/></a>
          </td>
          <td>
            <c:out value="${channel.authToken}" />
          </td>
          <td>
            <a href="/api/channels/${channel.name}/events">View Events of ${channel.name}</a>
          </td>
        </tr>
      </c:forEach>
    </table>
    <br>
  </div>
  <div id="pagi" class="navigationContainer">
    <a id="pagination" href="/api/all-channels/after/${first}/frags${qParam}"><< Previous</a>
    <a href="/api/all-channels/before/${last}/frags${qParam}" id="pagination">Next >></a>
  </div>
</div>

