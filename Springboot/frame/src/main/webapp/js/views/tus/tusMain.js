const fileInput = document.querySelector('#js-file-input')
const dataDiv = document.querySelector('#data-pre');
const toggleBtn = document.querySelector('#toggle-btn')

function reset() {
    convertedFiles.map((i, key) => {
        $('.progress-bar_' + key).css('width', '0%');
        $('#js-upload-text-progress_' + key).html('');
    })
}
/**
 * Turn a byte number into a human readable format.
 * Taken from https://stackoverflow.com/a/18650828
 */
function formatBytes(bytes, decimals = 2) {
    if (bytes === 0) {
        return '0 Bytes'
    }

    const k = 1024
    const dm = decimals < 0 ? 0 : decimals
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i]
}

let convertedFiles = [];

fileInput.addEventListener('change', (e) => {
    dataDiv.innerHTML = '';
    const files = e.target.files;

    Object.keys(files).forEach(k => {
        convertedFiles = [
            ...convertedFiles,
            { id: URL.createObjectURL(files[k]), file: files[k]}
        ];
    });

    convertedFiles.map((i, key) => {
        const file = i.file;
        key ++
        // 10MB
        const chunkSize = parseInt(104857600, 10)
        console.log(chunkSize);

        dataDiv.innerHTML +=
            `
                      <div class="file-data" key=${key}>
<!--                        <img alt="what" src=${i.id} width="150" />-->
                        <div>파일명: ${i.file.name}</div>
                        <div>파일 타입: ${i.file.type}</div>
                        <div>파일 크기: ${i.file.size} byte</div>
                        <div class="flex-grow-1">
                          <div class="progress pr_${key}">
                            <div class="progress-bar_${key} progress-bar-striped bg-success" role="progressbar"></div>
                          </div>
                          <div class="upload-text-progress" id="js-upload-text-progress_${key}"></div>
                        </div>
                      </div>
                    `

        let upload = new tus.Upload(file, {
            endpoint: "/tusMain/upload",
            chunkSize,
            retryDelays: [0, 1000, 3000, 5000],
            metadata: {
                filename: file.name,
                filetype: file.type
            },
            onError: function (error) {
                console.log("Failed because: " + error);
            },
            onProgress: function (bytesUploaded, bytesTotal) {
                const percentage = ((bytesUploaded / bytesTotal) * 100).toFixed(2)
                $('.progress-bar_' + key).css('width', percentage + '%');

                if (percentage < 100.00) {
                    $('#js-upload-text-progress_' + key).html(`Uploaded ${formatBytes(bytesUploaded)} of ${formatBytes(bytesTotal)} (${percentage}%)`);
                } else {
                    $('#js-upload-text-progress_' + key).html(`Uploaded ${formatBytes(bytesUploaded)} of ${formatBytes(bytesTotal)} (${percentage}%) 파일 저장 중`);
                }
            },
            onSuccess: function () {
                $('#js-upload-text-progress_' + key).html("파일 저장 완료");
            }
        });

        toggleBtn.addEventListener('click', (e) => {
            reset();

            // Check if there are any previous uploads to continue.
            upload.findPreviousUploads().then(function (previousUploads) {
                // Found previous uploads so we select the first one.
                if (previousUploads.length) {
                    upload.resumeFromPreviousUpload(previousUploads[0])
                }

                // Start the upload
                upload.start()
            })
        });
    });
});

// function init() {
//     addEvent();
// }
//
// function formatBytes(bytes, decimals = 2) {
//     if (bytes === 0) {
//         return '0 Bytes';
//     }
//
//     const k = 1024;
//     const dm = decimals < 0 ? 0 : decimals;
//     const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
//     const i = Math.floor(Math.log(bytes) / Math.log(k))
//     return parseFloat( (bytes / Math.pow(k, i)).toFixed(dm) ) + ' ' + sizes[i];
// }
//
// function addEvent() {
//
//     $('#fileInput').change( () => {
//         console.log('change');
//     })
//
//     $("#downloadBtn").click( () => {
//         $.ajax({
//             url: "/tusMain/download",
//             type: "POST",
//             success: (result) => {
//                 console.log(result);
//             }
//         })
//     })
// }
//
// $(init)