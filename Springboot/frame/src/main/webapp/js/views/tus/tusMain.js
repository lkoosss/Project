var tusMain = {
    convertedFiles: []
}

$(init)

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
        $.each(files, (idx, element) => {
            tusMain.convertedFiles = [
                ...tusMain.convertedFiles,
                {id: URL.createObjectURL(element), file: element}
            ]
        })

        tusMain.convertedFiles.map( (element, idx) => {
            idx++;
            const chunkSize = parseInt(1048576, 10);

            var fileInfoHtml = `    <div class="file-data" key=${idx}>
                                                <div>파일명: ${element.file.name}</div>
                                                <div>파일 크기: ${element.file.size} byte</div>
                                                <div class="flex-grow-1">
                                                  <div class="progress pr_${idx}">
                                                    <div class="progress-bar_${idx} progress-bar-striped bg-success" role="progressbar"></div>
                                                  </div>
                                                  <div class="upload-text-progress" id="js-upload-text-progress_${idx}"></div>
                                                </div>
                                              </div>`;
            $('#dataList').append(fileInfoHtml);

            var uploadConfig = {
                endpoint : "/tusMain/upload",
                retryDelays: [0, 1000, 3000, 5000],
                metadata: {
                    filename: element.file.name,
                    filetype: element.file.type,
                },
                chunkSize: chunkSize,
                onBeforeRequest: async (req) => {
                    if (req._method === "PATCH") {
                        var currentOffset = req._headers["Upload-Offset"];
                        var chunkSizeFile = element.file.slice(currentOffset, currentOffset + chunkSize);
                        var arrayBuffer = await chunkSizeFile.arrayBuffer();

                        const wordArray = CryptoJS.lib.WordArray.create(arrayBuffer)
                        const hash = CryptoJS.SHA1(wordArray)
                        var checksum = hash.toString(CryptoJS.enc.Base64);

                        req.setHeader('Upload-Checksum', `sha1 ${checksum}`);

                    }
                },
                onProgress: (bytesUploaded, bytesTotal) => {
                    const percentage = ( (bytesUploaded / bytesTotal) * 100 ).toFixed(2);
                    $('.progress-bar_' + idx).css('width', percentage + '%');

                    if (percentage < 100.00) {
                        $('#js-upload-text-progress_' + idx).html(`Uploaded ${getByteFormat(bytesUploaded)} of ${getByteFormat(bytesTotal)} (${percentage}%)`);
                    } else {
                        $('#js-upload-text-progress_' + idx).html(`Uploaded ${getByteFormat(bytesUploaded)} of ${getByteFormat(bytesTotal)} (${percentage}%) 파일 저장 중 `);
                    }
                },
                onSuccess: () => {
                    $('#js-upload-text-progress_' + idx).html("파일 저장 완료");
                },
                onError: (error) => {
                    if (error.originalResponse.getStatus() == 460) {
                        alert("Checksum incorrect");
                    }
                    console.error("failed because: " + error);
                }
            }
            var upload = new tus.Upload(element.file, uploadConfig);

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

            // 일시정지 클릭 이벤트
            $('#pauseBtn').click( () => {
                upload.abort();
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