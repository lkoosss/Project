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
    <title>Title</title>

    <!-- jQuery Lib -->
    <script type="text/javascript" src="/js/lib/jquery.min_1.12.4.js" defer></script>

    <!-- blockly Lib-->
    <script type="text/javascript" src="/js/lib/blockly/blockly_compressed.js" defer></script>
    <script type="text/javascript" src="/js/lib/blockly/blocks_compressed.js" defer></script>
    <script type="text/javascript" src="/js/lib/blockly/javascript_compressed.js" defer></script>
    <script type="text/javascript" src="/js/views/blockly/blocklyMain.js" defer></script>
</head>
<body>
    <h1>Blockly Test</h1>
    <div id="blockly">

    </div>

    <xml id="toolbox" style="display: none">
        <category name="Logic" colour="%{BKY_LOGIC_HUE}">
            <block type="controls_if"></block>
            <block type="logic_compare"></block>
            <block type="logic_operation"></block>
            <block type="logic_negate"></block>
            <block type="logic_boolean"></block>
            <block type="logic_null"></block>
            <block type="logic_ternary"></block>
        </category>
    </xml>
</body>
</html>
