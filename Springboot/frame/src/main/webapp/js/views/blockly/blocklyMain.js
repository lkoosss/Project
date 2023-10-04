var blocklyMain = {

};

$(init);

function init() {
    blocklyInit();
}

function blocklyInit() {
    let blockly = $('#blockly');
    let blocklyConfig = {
        toolbox : $('#toolbox'),
        theme : Blockly.theme.Classic
    }
    let workspace = Blockly.inject(blockly, blocklyConfig)

    Blockly.svgResize(workspace);
}