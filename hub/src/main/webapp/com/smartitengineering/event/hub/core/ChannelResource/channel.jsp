<%-- 
    Document   : channel
    Created on : Jul 29, 2010, 12:49:15 PM
    Author     : kaisar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
  "http://www.w3.org/TR/html4/loose.dtd">

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Add New Event in ${it.name} Channel</title>
  </head>
  <body bgcolor="#00FFCC">
    <h3>
      <div>
        <div id="name">Channel Name : ${it.name}</div>
        <div id="des">Description : ${it.description}</div>
        <div id="aut">Auth-Token : ${it.authToken}</div>
      </div>
    </h3>
    <form action="/api/channels/${it.name}/hub" method="post">
      Enter Event Name <textarea name="event" id="event">Event</textarea><br>
      <input type="submit" value="Submit" />
    </form>
  </body>
</html>
