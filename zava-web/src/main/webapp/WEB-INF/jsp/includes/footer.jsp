<%-- 
  footer.jsp -- Shared footer include for all Zava Social pages.

  Closes the content cell, renders the footer row, and closes
  all HTML tags opened by header.jsp.

  Include this at the BOTTOM of every page JSP:
    <%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>

  CSS classes used (see docs/design-spec.md):
    .footer, .footerLink, .navSeparator, .browserNote
--%>

        </td>
    </tr>

    <!-- FOOTER -->
    <tr>
        <td class="footer">
            &copy; 2007 Zava Social. All rights reserved.<br>
            <a href="#" class="footerLink">Contact Us</a>
            <span class="navSeparator"> | </span>
            <a href="#" class="footerLink">Terms of Use</a>
            <span class="navSeparator"> | </span>
            <a href="#" class="footerLink">Privacy Policy</a><br>
            <span class="browserNote">Best viewed in Internet Explorer 6.0 at 1024x768</span>
        </td>
    </tr>

</table>

</body>
</html>
