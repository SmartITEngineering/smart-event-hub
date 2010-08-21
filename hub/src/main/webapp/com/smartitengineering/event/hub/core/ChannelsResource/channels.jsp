<%-- 
    Document   : channels
    Created on : Jul 19, 2010, 11:58:44 AM
    Author     : imyousuf
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
  "http://www.w3.org/TR/html4/loose.dtd">

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Channels</title>
    <script type="text/javascript" src="/javascripts/js_1.js"></script>
    <link type="text/css" rel='stylesheet' href='/css/channel.css' />
  </head>
  <body>
    <h1 align="center">Channels</h1>
    <div class="show" id="div1">
      <table>
        <tr>
          <th>Position</th>
          <th>Name</th>
          <th>Description</th>
          <th>Auth Token</th>
          <th>Events</th>
        </tr>
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
      <div id="pagi">
        <c:if test="${not empty first}">
          <a href="/api/all-channels/after/${first}${qParam}" id="pagination"> << Previous</a>&nbsp;&nbsp;&nbsp;&nbsp;
          <c:if test="${last!=1}">
            <a href="/api/all-channels/before/${last}${qParam}" id="pagination">Next >> </a>
          </c:if>
        </c:if>
      </div>
      <br>
      <center><button onclick=change() id="butt">Create New Channel</button></center>
    </div>

    <h3>
      <div class="hide" id="div2">
        <form action="/api/all-channels" method="post" id="create-channel">
          <div>Name</div><input name="name" type="text" /><br />
          <div>Description</div><textarea name="description" cols="30" rows="5"></textarea><br />
          <div>Auth Token</div><input name="authToken" type="text" /><br />
          <input name="submit" type="submit" />
        </form>
        <button onclick=change()>Back</button>
      </div>

    </h3>
  </body>
</html>
