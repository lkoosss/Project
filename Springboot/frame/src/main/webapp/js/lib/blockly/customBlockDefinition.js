/** Custom 블럭을 정의 **/
Blockly.defineBlocksWithJsonArray(get_allBlock())

function get_allBlock() {
    let allBlock = [];
    allBlock.push(getBlock_hello());
    allBlock.push(getBlock_inputBox());
    allBlock.push(getBlock_button());
    return allBlock;
}

function getBlock_hello() {
    return {
        type: 'hello',
        message0: '%1번 인사하기',
        args0: [
            {
                type: 'field_number',
                name: 'TIME',
                value: 0,
                min: 0,
                precision: 1,
            },
        ],
        previousStatement: null,
        nextStatement: null,
        colour: '#fdd835',
    }
}

function getBlock_inputBox() {
    return {
        type: 'inputBox',
        message0: 'create Input Box UI: %1',
        args0: [
            {
                type: 'field_input',
                name: 'FIELDNAME',
            }
        ]
    }
}

function getBlock_button() {
    return {
        type: 'button',
        message0: 'create Button UI: %1',
        args0: [
            {
                type: 'field_input',
                name: 'BTNNAME',
            }
        ]
    }
}