<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Insert title here</title>

    <!-- CSS 적용 -->
    <link type="text/css" rel="stylesheet" href="/css/views/zookeeper/mainControll.css" />

    <!-- Script -->
    <script type="text/javascript" src="/js/lib/jquery.min_1.12.4.js"></script>
    <script type="text/javascript" src="/js/views/zookeeper/mainControll.js"></script>

</head>
<body>
<div class="content">
    <div class="controllBox">
        <div class="selectBox">
            <h2 class="titleTxt">Select Znode</h2>
            <p>key</p>
            <input type="text" name="keyForSelect" id="keyForSelect" class="znodeInput"/>

            <button id="selectZnodeValueBtn">Value</button>
            <button id="selectZnodeStateBtn">State</button>
            <button id="selectZnodeChildBtn">Child</button>
        </div>

        <div class="createBox">
            <h2 class="titleTxt">Create Znode</h2>
            <p>key</p>
            <input type="text" name="keyForCreate" id="keyForCreate" class="znodeInput"/>

            <p>value</p>
            <input type="text" name="valueForCreate" id="valueForCreate" class="znodeInput"/>

            <p>Type</p>
            <select name="typeForCreate" id="typeForCreate">
                <option value="persistence">persistence</option>
                <option value="ephemeral">ephemeral</option>
            </select>

            <button id="createZnodeBtn">Create</button>
        </div>

        <div class="updateBox">
            <h2 class="titleTxt">update Znode</h2>
            <p>key</p>
            <input type="text" name="keyForUpdate" id="keyForUpdate" class="znodeInput"/>

            <p>value</p>
            <input type="text" name="valueForUpdate" id="valueForUpdate" class="znodeInput"/>

            <button id="updateZnodeBtn">Update</button>
        </div>

        <div class="deleteBox">
            <h2 class="titleTxt">delete Znode</h2>
            <p>key</p>
            <input type="text" name="keyForDelete" id="keyForDelete" class="znodeInput"/>

            <button id="deleteZnodeBtn">Delete</button>
        </div>
    </div>

    <div class="resultBox">
        <h2 class="titleTxt">Result</h2>

        <p>key</p>
        <input type="text" name="keyForResult" id="keyForResult" class="znodeInput"/>
        <p>Value</p>
        <input type="text" name="valueForResult" id="valueForResult" class="znodeInput"/>

        <h3 id="stateForResult"></h3>
    </div>
</div>
</body>
</html>