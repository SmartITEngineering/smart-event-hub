<%-- 
    Document   : channels
    Created on : Jul 19, 2010, 11:58:44 AM
    Author     : imyousuf
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <h1>Channels</h1>
    <div class="show" id="div1">
      <table>
        <tr>
          <th>Name</th>
          <th>Description</th>
          <th>Auth Token</th>
        </tr>
        <c:forEach var="channel" items="${it}">
          <tr>
            <td>
              <a href="channels/${channel.name}/events"><c:out value="${channel.name}" /></a>
            </td>
            <td>
              <c:out value="${channel.description}" />
            </td>
            <td>
              <c:out value="${channel.authToken}" />
            </td>
          </tr>
        </c:forEach>
      </table>
      <button onclick=change()>Create New Channel</button>
    </div>

    <h3>
      <div class="hide" id="div2">
        <form action="/api/channels" method="post" id="create-channel">
          <div>Name</div><input name="name" type="text" /><br />
          <div>Description</div><input name="description" type="text" /><br />
          <div>Auth Token</div><input name="authToken" type="text" /><br />
          <input name="submit" type="submit" />
        </form>
        <button onclick=change()>Back</button>
      </div>

    </h3>
  </body>
</html>
