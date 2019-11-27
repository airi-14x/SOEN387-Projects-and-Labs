<%-- 
    Document   : viewBook
    Created on : 6 nov. 2019, 08:40:53
    Author     : Airi
--%>

<%@page import="java.io.OutputStream"%>
<%@page import="repository.core.Session"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="repository.core.Book"%>

<%
    Session currentSession = (Session) session.getAttribute("currentSession");
    if (!currentSession.isUserLoggedIn()) {
        response.sendRedirect("login.jsp");
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Display Book</title>
    </head>
    <body>
        <div>
            <%=request.getAttribute("error")%>
            <table border="1px solid black;">
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Description</th>
                    <th>ISBN</th>
                    <th>Author</th>
                    <th>Publisher Company</th>
                    <th>Publisher Address</th>
                    <th>Book Cover</th>
                </tr>
                <c:forEach items="${books}" var="book">
                    <tr>
                    <form action="ImageController" method="GET">
                        <td><input type="text" value="${book.id}" name="bookId" readonly/></td>
                        <td>${book.title}</td>
                        <td>${book.description}</td>
                        <td>${book.getISBN()}</td>
                        <td>${book.author}</td>
                        <td>${book.getPublisherCompany()}</td>
                        <td>${book.getPublisherAddress()}</td>
                        <td><input type="submit" value="View Cover" name="viewCover"/></td>
                    </form>
                    </tr>
                </c:forEach>   
            </table>
        </div>
    </body>
</html>