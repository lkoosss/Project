<%--
  Created by IntelliJ IDEA.
  User: ssoh
  Date: 23. 10. 4.
  Time: 오후 5:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Blockly Page</title>

    <link type="text/css" rel="stylesheet" href="/css/views/blockly/blocklyMain.css">

    <!-- jQuery Lib -->
    <script type="text/javascript" src="/js/lib/jquery.min_1.12.4.js" defer></script>

    <!-- blockly Lib-->
    <script type="text/javascript" src="/js/lib/blockly/blockly_compressed.js" defer></script>
    <script type="text/javascript" src="/js/lib/blockly/blocks_compressed.js" defer></script>
    <script type="text/javascript" src="/js/lib/blockly/javascript_compressed.js" defer></script>
    <script type="text/javascript" src="/js/lib/blockly/customBlockDefinition.js" defer></script>
    <script type="text/javascript" src="/js/lib/blockly/customCodeGenerator.js" defer></script>
    <script type="text/javascript" src="/js/lib/blockly/en.js" defer></script>

    <!-- Page JS -->
    <script type="text/javascript" src="/js/views/blockly/blocklyMain.js" defer></script>
</head>
<body>
    <div id="splitDiv">
        <div class="splitChildDiv">
            <div id="blocklyDiv"></div>
            <div id="previewDiv"></div>
        </div>
        <div class="splitChildDiv">
            <div id="codeView"></div>
            <button id="runBtn" class="blockControlBtn">코드 실행</button>
            <button id="deleteBlockBtn" class="blockControlBtn">블럭 전체삭제</button>
        </div>
    </div>

    <xml id="toolbox" style="display: none">
        <category name="Logic" colour="%{BKY_LOGIC_HUE}">
            <block type="controls_if"></block>
            <block type="controls_repeat_ext"></block>
            <block type="logic_compare"></block>
            <block type="logic_operation"></block>
            <block type="logic_negate"></block>
            <block type="logic_boolean"></block>
            <block type="logic_null"></block>
            <block type="logic_ternary"></block>
            <block type="logic_compare"></block>
            <block type="math_number"></block>
            <block type="math_arithmetic"></block>
            <block type="text"></block>
            <block type="text_print"></block>
        </category>
        <category name="Custom" colour="#fdd835">
            <block type="hello"></block>
            <block type="inputBox"></block>
            <block type="button"></block>
        </category>
    </xml>
</body>
</html>
