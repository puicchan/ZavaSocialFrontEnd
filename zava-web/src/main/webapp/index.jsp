<%-- 
  index.jsp -- Welcome/redirect page for Zava Social.
  
  This page simply forwards the user to the feed servlet.
  It exists because web.xml needs a welcome-file that is a
  real file on disk, and we want the root URL to go to /feed.
  
  Using jsp:forward instead of a meta refresh because it's
  server-side and faster -- the user never sees this page.
--%>
<jsp:forward page="/feed" />
