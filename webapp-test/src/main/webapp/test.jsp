<%@ page language="java" pageEncoding="UTF-8" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="now" class="java.util.Date" />

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <title>PRECOMPILE JSP TEST</title>
</head>
<body>

<h4>PRECOMPILE JSP/JSTL TEST. Тест прекомпиляции JSP.</h4>
<p>JSTL format date:</p>
<fmt:formatDate value="${now}" />
<p>JSTL out simple:</p>
<c:out value="simple"/>
</body>
</html>
