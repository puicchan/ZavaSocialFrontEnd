<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%--
  newPost.jsp -- New shoe post form for Zava Social.

  Form fields: brand, model, description, imageUrl.
  Brand and model are required -- everything else is optional.

  CSS classes: .pageTitle, .formTable, .formLabel, .formInput, .formButton,
               .required, .errorMsg, .metaText
--%>

<%@ include file="/WEB-INF/jsp/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/includes/nav.jsp" %>

            <h1 class="pageTitle">POST YOUR KICKS</h1>

<% if (request.getAttribute("errorMsg") != null) { %>
            <div class="errorMsg"><%= request.getAttribute("errorMsg") %></div>
<% } %>

            <form method="post" action="<%= request.getContextPath() %>/post">
            <table class="formTable" width="500" cellpadding="0" cellspacing="0" border="0">
                <tr>
                    <td class="formLabel" width="120">Brand: <span class="required">*</span></td>
                    <td class="formInput"><input type="text" name="brand" size="40" style="width: 350px;" value="<%= request.getParameter("brand") != null ? request.getParameter("brand") : "" %>"></td>
                </tr>
                <tr>
                    <td class="formLabel">Model: <span class="required">*</span></td>
                    <td class="formInput"><input type="text" name="model" size="40" style="width: 350px;" value="<%= request.getParameter("model") != null ? request.getParameter("model") : "" %>"></td>
                </tr>
                <tr>
                    <td class="formLabel">Description:</td>
                    <td class="formInput"><textarea name="description" rows="8" cols="40" style="width: 350px; height: 150px;"><%= request.getParameter("description") != null ? request.getParameter("description") : "" %></textarea></td>
                </tr>
                <tr>
                    <td class="formLabel">Image URL:</td>
                    <td class="formInput"><input type="text" name="imageUrl" size="40" style="width: 350px;" value="<%= request.getParameter("imageUrl") != null ? request.getParameter("imageUrl") : "" %>"></td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td class="formInput" style="padding-top: 10px;">
                        <input type="submit" value="POST IT" class="formButton">
                    </td>
                </tr>
            </table>
            </form>

            <br>
            <span class="metaText"><span class="required">*</span> Required fields</span>

<%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>
