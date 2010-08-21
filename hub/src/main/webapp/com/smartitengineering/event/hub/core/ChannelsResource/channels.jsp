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
        <c:forEach var="channel" items="${it}">
          <tr>
            <td>
              <a href="channels/${channel.name}"><c:out value="${channel.position}" /></a>
            </td>
            <td>
              <a href="channels/${channel.name}"><c:out value="${channel.name}" /></a>
            </td>
            <td>
              <c:set var="description" value="${channel.description}"></c:set>
              <a href="channels/${channel.name}"><c:out value="${fn:substring(description,0,10)}"/></a>
            </td>
            <td>
              <c:out value="${channel.authToken}" />
            </td>
            <td>
              <a href="channels/${channel.name}/events">View Events of ${channel.name}</a>
            </td>
          </tr>
        </c:forEach>
      </table>
      <br><br>
      <a href="" id="pagination">Previous</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="" id="pagination">Next</a>
      <center><button onclick=change() id="butt">Create New Channel</button></center>
    </div>

    <h3>
      <div class="hide" id="div2">
        <form action="/api/channels" method="post" id="create-channel">
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
