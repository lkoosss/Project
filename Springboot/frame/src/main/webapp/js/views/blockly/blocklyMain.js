var blocklyMain = {
    workspace: undefined
};

$(main);

///// 메인 함수 /////
function main() {
    blocklyInit();
    addEvent();
}

///// Blockly 초기화 /////
function blocklyInit() {
    // Blockly 설정 정의
    blocklyConfig = {
        toolbox: document.getElementById('toolbox')
    }

    // Blockly 시작
    blocklyMain.workspace = Blockly.inject('blocklyDiv', blocklyConfig);

    // Blockly 이벤트 등록
    blocklyMain.workspace.addChangeListener( (event) => {
        let codeString = Blockly.JavaScript.workspaceToCode(blocklyMain.workspace);
        $('#codeView').text(codeString);
    })
}

///// 이벤트 등록 /////
function addEvent() {
    // 코드실행 버튼 클릭 이벤트
    $('#runBtn').click( (event) => {
        let codeString = Blockly.JavaScript.workspaceToCode(blocklyMain.workspace);
        eval(codeString);
    })

    // 블럭 전체삭제 버튼 클릭 이벤트
    $('#deleteBlockBtn').click( () => {
        blocklyMain.workspace.clear();
    })
}