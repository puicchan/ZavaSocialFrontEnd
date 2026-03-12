<%-- 
  header.jsp -- Shared header include for all Zava Social pages.

  Outputs: DOCTYPE, <html>, <head>, opening <body>, and the header
  section of the master layout table.

  Include this at the TOP of every page JSP:
    <%@ include file="/WEB-INF/jsp/includes/header.jsp" %>

  NOTE: This include opens several HTML tags that are closed
  by footer.jsp. Do not close </body> or </html> in page JSPs.

  CSS classes used (see docs/design-spec.md):
    .container, .header, .headerTitle, .headerTagline
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <title>Zava Social - Share Your Kicks</title>
    <link rel="stylesheet" type="text/css" href="/css/zava.css">
</head>
<body>

<table class="container" width="800" cellpadding="0" cellspacing="0" border="0" align="center">

    <!-- HEADER -->
    <tr>
        <td class="header">
            <table width="100%" cellpadding="0" cellspacing="0" border="0">
                <tr>
                    <td align="left" valign="middle">
                        <span class="headerTitle">ZAVA SOCIAL</span>
                    </td>
                    <td align="right" valign="bottom">
                        <span class="headerTagline">kicks. culture. community.</span>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
