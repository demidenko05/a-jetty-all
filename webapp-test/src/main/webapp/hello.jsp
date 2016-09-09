<%@ page language="java" pageEncoding="UTF-8" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <title>PRECOMPILE JSP TEST</title>
</head>
<body>

<h4>Say hello JSP TEST. JSP Тест скажи привет.</h4>
    <form action="webtest/" method="GET" accept-charset="UTF-8">
      <table>
        <tr>
          <td>
            <label>Say hello in your language:</label>
          </td>
          <td>
            <div>
              <input name="hello" value="привет">
            </div>
          </td>
        </tr>
      </table>
      <div>
        <input  type="submit"/>
      </div>
    </form>
</body>
</html>
