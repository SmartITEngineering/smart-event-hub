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
    <script type="text/javascript" src="/javascripts/jquery-1.4.2.js"></script>
    <script type="text/javascript" src="/javascripts/siteljquerylib.js"></script>
    <c:choose>
      <c:when test="${empty param.count}">
        <c:set var="qParam" value="" />
      </c:when>
      <c:otherwise>
        <c:set var="qParam" value="?count=${param.count}" />
      </c:otherwise>
    </c:choose>
    <script type="text/javascript">
      var url="/api/all-channels/frags${qParam}";
      $(document).ready(function(){
        $("#content").pagination(url, "pagi");
      });
    </script>
  </head>
  <body>
    <div id="content"></div>
    <div id="div1" class="show">
      
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
