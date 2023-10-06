<%--
  Created by IntelliJ IDEA.
  User: ssoh
  Date: 23. 10. 6.
  Time: 오후 2:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>mxgraph Page</title>

    <link rel="shortcut icon" href="#">

    <!-- jQuery Lib -->
    <script type="text/javascript" src="/js/lib/jquery.min_1.12.4.js" defer></script>

    <!-- mxgraph Lib -->
    <script type="text/javascript">
        mxBasePath = '/js/lib/mxgraph'
    </script>
    <script type="text/javascript" src="/js/lib/mxgraph/mxClient.min.js" defer></script>

    <!-- Page JS -->
    <script type="text/javascript" src="/js/views/mxgraph/mxgraphMain.js" defer></script>
</head>
<body>
    <div id="mxGraphDiv">
        <div id="toolbar"></div>
    </div>
</body>
</html>
