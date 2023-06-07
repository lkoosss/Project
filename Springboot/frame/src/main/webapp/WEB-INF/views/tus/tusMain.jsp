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
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">

    <!--  Script  -->
    <script type="text/javascript" src="/js/lib/jquery.min_1.12.4.js" defer></script>
    <script type="text/javascript" src="/js/lib/secret/md5.min.js"></script>
    <script type="text/javascript" src="/js/lib/tus/tus.js" defer></script>
    <script type="text/javascript" src="/js/views/tus/tusMain.js" defer></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js" integrity="sha512-E8QSvWZ0eCLGk4km3hxSsNmGWbLtSCSUcewDQPQWZF6pEU8GlT8a5fF32wOl1i8ftdMhssTrF/OhyGWwonTcXA==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/md5.min.js" integrity="sha512-3sGbaDyhjGb+yxqvJKi/gl5zL4J7P5Yh4GXzq+E9puzlmVkIctjf4yP6LfijOUvtoBI3p9pLKia9crHsgYDTUQ==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/lib-typedarrays.min.js" integrity="sha512-IYLn1Vhe6FU/6vVifkxxGV8exi8kFXjrIVuNuYlGrQQ/gv3+fa/fPFY5Nh1QCB+EdUrY+QRVocT9jtxPzlkjWQ==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>

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
