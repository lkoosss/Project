<%--
  Created by IntelliJ IDEA.
  User: ssoh
  Date: 23. 6. 5.
  Time: 오후 2:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>

    <!--  CSS 적용  -->
    <link type="text/css" rel="stylesheet" href="/css/views/tus/tusMain.css">

    <!--  Script  -->
    <script type="text/javascript" src="/js/lib/jquery.min_1.12.4.js" defer></script>
    <script type="text/javascript" src="/js/lib/tus/tus.min.js" defer></script>
    <script type="text/javascript" src="/js/views/tus/tusMain.js" defer></script>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
</head>
<body>
    <h1>TUS Test</h1>
    <div class="eddy-div">
        <button id="downloadBtn">다운로드</button>
        <button id="uploadBtn">업로드</button>
        <input type="file" id="fileInput" multiple>
        <div id="dataList">

        </div>
    </div>
</body>
</html>
