<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%--
  login.jsp -- Login page for Zava Social.

  Shows a dropdown of usernames from the seed data. No password field
  because this is a demo app and there's no real auth backend.

  CSS classes: .loginForm, .formLabel, .formInput, .formButton, .errorMsg, .pageTitle
--%>

<%@ include file="/WEB-INF/jsp/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/includes/nav.jsp" %>

            <h1 class="pageTitle" style="text-align: center;">WELCOME TO ZAVA SOCIAL</h1>

            <p style="text-align: center;">Select your username to log in.</p>

<% if (request.getAttribute("errorMsg") != null) { %>
            <div class="errorMsg" style="width: 380px; margin: 0 auto 10px auto;"><%= request.getAttribute("errorMsg") %></div>
<% } %>

            <form method="post" action="<%= request.getContextPath() %>/login">
            <table class="loginForm" cellpadding="0" cellspacing="0" border="0">
                <tr>
                    <td class="formLabel" width="100">Username:</td>
                    <td class="formInput">
                        <select name="userName" style="width: 220px;">
                            <option value="">-- Select User --</option>
<%
    String[] userList = (String[]) request.getAttribute("userList");
    if (userList != null) {
        for (int i = 0; i < userList.length; i++) {
%>
                            <option value="<%= userList[i] %>"><%= userList[i] %></option>
<%
        }
    }
%>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td class="formInput" style="padding-top: 10px;">
                        <input type="submit" value="LOG IN" class="formButton">
                    </td>
                </tr>
            </table>
            </form>

<%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>
