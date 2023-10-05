/** Custom 블록의 코드 동작을 정의 **/
Blockly.JavaScript.forBlock['hello'] = (block) => {
    const time = block.getFieldValue('TIME');
    const code = `for(let i = 0; i < ${time}; i += 1) { alert("hello"); }\n`;
    return code;
}

Blockly.JavaScript.forBlock['inputBox'] = (block) => {
    const inputString = block.getFieldValue('FIELDNAME');
    const inputBoxHtml = `<input type=text value=${inputString} />`;
    const code = `$('#previewDiv').append('${inputBoxHtml}') \n`
    return code;
}

Blockly.JavaScript.forBlock['button'] = (block) => {
    const inputString = block.getFieldValue('BTNNAME');
    const buttonHtml = `<button type="button">${inputString}</button>`
    const code = `$('#previewDiv').append('${buttonHtml}') \n`
    return code
}