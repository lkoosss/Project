var tusMain = {
    convertedFiles: [],
    test: undefined,
    uploaded
}
function init() {
    addEvent();
}

function addEvent() {

    // 다운로드 버튼 클릭 이벤트
    $("#downloadBtn").click( () => {
        $.ajax({
            url: "/tusMain/download",
            type: "POST",
            success: (result) => {
                console.log(result);
            }
        })
    })

    // 파일선택 이벤트
    $('#fileInput').change( (e) => {
        $('#dataList').empty();

        const files = e.target.files;
        $.each(files, (index, item) => {
            tusMain.convertedFiles = [
                ...tusMain.convertedFiles,
                {id: URL.createObjectURL(item), file: item}
            ]
        })

        tusMain.convertedFiles.map( (item, key) => {
            const file = item.file;
            key ++;
            const chunkSize = parseInt(10485760, 10);

            var uploadFileTag = `    <div class="file-data" key=${key}>
                                                <div>파일명: ${item.file.name}</div>
                                                <div>파일 크기: ${item.file.size} byte</div>
                                                <div class="flex-grow-1">
                                                  <div class="progress pr_${key}">
                                                    <div class="progress-bar_${key} progress-bar-striped bg-success" role="progressbar"></div>
                                                  </div>
                                                  <div class="upload-text-progress" id="js-upload-text-progress_${key}"></div>
                                                </div>
                                              </div>`;
            $('#dataList').append(uploadFileTag);

            // const fileReader = new FileReader();
            // fileReader.onload = (e) => {
            //     console.log(e);
            //     const wordArray = CryptoJS.lib.WordArray.create(e.target.result)
            //     console.log(e.target.result);
            //     const md5Checksum = CryptoJS.MD5(wordArray);
            //     console.log(md5Checksum.toString());
            // }
            // fileReader.readAsArrayBuffer(file);

            var uploadConfig = {
                endpoint : "/tusMain/upload",
                retryDelays: [0, 1000, 3000, 5000],
                metadata: {
                    filename: file.name,
                    filetype: file.type,
                },
                chunkSize: chunkSize,
                // headers:
                //   {"Upload-Checksum": 'md5 iwwhwffrgrehe'},
                onBeforeRequest: (req) => {
                    console.log(req);
                    if (req._method === "PATCH") {
                        var xhr = req.getUnderlyingObject();
                        // console.log(req._headers.Upload-Offset)
                        console.log("it PATCH")
                        var header = req._headers;
                        console.log(header)
                        const chunkBlob = upload.file
                        const fileReader = new FileReader();
                    }

                },
                onProgress: (bytesUploaded, bytesTotal) => {
                    const percentage = ( (bytesUploaded / bytesTotal) * 100 ).toFixed(2);
                    $('.progress-bar_' + key).css('width', percentage + '%');

                    if (percentage < 100.00) {
                        $('#js-upload-text-progress_' + key).html(`Uploaded ${getByteFormat(bytesUploaded)} of ${getByteFormat(bytesTotal)} (${percentage}%)`);
                    } else {
                        $('#js-upload-text-progress_' + key).html(`Uploaded ${getByteFormat(bytesUploaded)} of ${getByteFormat(bytesTotal)} (${percentage}%) 파일 저장 중 `);
                    }
                },
                onSuccess: () => {
                    $('#js-upload-text-progress_' + key).html("파일 저장 완료");
                },
                onError: (error) => {
                    console.error("failed because: " + error);
                }
            }
            var upload = new tus.Upload(file, uploadConfig);

            // 업로드 클릭 이벤트
            $('#uploadBtn').click( (e) => {
                resetProgress();

                upload.findPreviousUploads().then( (previousUploads) => {
                    if (previousUploads.length) {
                        upload.resumeFromPreviousUpload(previousUploads[0]);
                    }

                    upload.start();
                })
            })
        })
    })
}

function resetProgress() {
    tusMain.convertedFiles.map( (i, key) => {
        $('.progress-bar_' + key).css('width', '0%');
        $('#js-upload-text-progress_' + key).html('');
    })
}

function getByteFormat(bytes, decimals = 2) {
    if (bytes === 0) {
        return '0 Bytes';
    }

    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const Unit = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))

    return parseFloat( (bytes / Math.pow(k, i)).toFixed(dm) ) + ' ' + Unit[i];
}

$(init)