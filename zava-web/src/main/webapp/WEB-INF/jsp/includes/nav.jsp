<%-- 
  nav.jsp -- Shared navigation bar include for all Zava Social pages.

  Renders the pipe-separated navigation links in a dark gray bar.
  Login/Logout link changes based on session attribute "isLoggedIn".
  Shows "Welcome back, username!" when logged in.

  Include this after header.jsp:
    <%@ include file="/WEB-INF/jsp/includes/nav.jsp" %>

  CSS classes used (see docs/design-spec.md):
    .nav, .navLink, .navActive, .navSeparator, .navUser
--%>

    <!-- NAV -->
    <tr>
        <td class="nav">
            <table width="100%" cellpadding="0" cellspacing="0" border="0">
                <tr>
                    <td align="left">
                        <a href="/feed" class="navLink">HOME</a>
                        <span class="navSeparator"> | </span>
                        <a href="/post" class="navLink">POST A SHOE</a>
                        <span class="navSeparator"> | </span>
                        <a href="/profile" class="navLink">MY PROFILE</a>
                        <span class="navSeparator"> | </span>
<% if (session.getAttribute("isLoggedIn") != null) { %>
                        <a href="/login?action=logout" class="navLink">LOGOUT</a>
<% } else { %>
                        <a href="/login" class="navLink">LOGIN</a>
<% } %>
                    </td>
                    <td align="right">
<% if (session.getAttribute("currentUser") != null) { %>
                        <span class="navUser">Welcome back, <%= session.getAttribute("currentUser") %>!</span>
<% } %>
                    </td>
                </tr>
            </table>
        </td>
    </tr>

    <!-- CONTENT -->
    <tr>
        <td class="content">
