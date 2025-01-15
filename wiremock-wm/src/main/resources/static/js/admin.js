//전역 상태 플래그
let isRequestInProgress = false;
let currentApiId = null;

// 수정 부분 처리
const editStateData = {
    normal: { mappings: "{}", files: "{}" },
    delay: { mappings: "{}", files: "{}" },
    error: { mappings: "{}", files: "{}" },
};
let currentEditState = "normal";

// 상태 데이터 로드 함수
const loadEditStateData = (state) => {
    currentEditState = state; // 현재 상태 업데이트
    document.getElementById("editApiMappings").value = editStateData[state].mappings;
    document.getElementById("editApiFiles").value = editStateData[state].files;
};

// 상태 데이터 저장 함수
const saveEditStateData = () => {
    editStateData[currentEditState].mappings = document.getElementById("editApiMappings").value;
    editStateData[currentEditState].files = document.getElementById("editApiFiles").value;
};

document.addEventListener("DOMContentLoaded", function () {
	
	// "API 추가" 버튼 클릭 시 팝업 열기
    const apiAddButton = document.querySelector(".btn-api-add");
    apiAddButton.addEventListener("click", function () {
        const apiAddModal = new bootstrap.Modal(document.getElementById("apiAddModal"));
        apiAddModal.show();
    });

	
	// "수정" 버튼 클릭 시 팝업 열기
    const editButtons = document.querySelectorAll(".btn-edit");
    editButtons.forEach(button => {
        button.addEventListener("click", function () {
            const apiEditModal = new bootstrap.Modal(document.getElementById("apiEditModal"));
            
            // API ID 가져오기
            currentApiId = button.getAttribute("data-id");

            // API 데이터를 가져와 모달에 채우기
            fetch(`/api/get/${currentApiId}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error("API 데이터를 가져오는 데 실패했습니다.");
                    }
                    return response.json();
                })
                .then(api => {
					// 기본 정보 채우기
	                document.getElementById("editApiName").value = api.apiName || "";
	                document.getElementById("editApiUrl").value = api.apiUrl || "";
				
					// 상태별 데이터 로드
	                editStateData.normal = { mappings: api.normalMappings || "{}", files: api.normalFiles || "{}" };
	                editStateData.delay = { mappings: api.delayMappings || "{}", files: api.delayFiles || "{}" };
	                editStateData.error = { mappings: api.errorMappings || "{}", files: api.errorFiles || "{}" };     

	                // 상태 버튼 이벤트 연결
	                document.getElementById("editNormalButton").onclick = () => {
	                    saveEditStateData();
	                    loadEditStateData("normal");
	                };
	                document.getElementById("editDelayButton").onclick = () => {
	                    saveEditStateData();
	                    loadEditStateData("delay");
	                };
	                document.getElementById("editErrorButton").onclick = () => {
	                    saveEditStateData();
	                    loadEditStateData("error");
	                };
					
					// 기본 상태 로드
            		loadEditStateData("normal");
					
                    // 모달 열기
                    apiEditModal.show();

                    // 저장 버튼 이벤트 설정
                    document.getElementById("saveEditApiButton").onclick = () => saveEditApi(currentApiId);
                })
                .catch(error => {
					console.log("errorapi : " + api);
                    console.error("Error fetching API data:", error);
                    alert("API 데이터를 가져오는 중 오류가 발생했습니다.");
                });
        });
    });
	
	// test api
	const dropdownItems = document.querySelectorAll('.dropdown-item');
	dropdownItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault(); // 기본 동작 방지 (링크 이동 방지)
            
            const id = item.closest(".dropdown-menu").getAttribute("data-id"); // API id 가져오기
            const type = item.getAttribute("data-type"); // 요청 유형 가져오기

            testApi(id, type); // testApi 함수 호출
        });
    });

    // "저장" 버튼 클릭 시 API 추가
    const saveApiButton = document.getElementById("saveApiButton");
    saveApiButton.addEventListener("click", function () {
        addNewApi();
    });
	
	// 삭제 버튼 이벤트
	const deleteButtons = document.querySelectorAll(".btn-delete");
    deleteButtons.forEach(button => {
        button.addEventListener("click", function () {
            deleteApi(button);
        });
    });

	/* 체크 박스 이벤트 부여 */
    const selectAllCheckbox = document.getElementById("select-all");	//전체
    const rowCheckboxes = document.querySelectorAll(".row-checkbox");	//개별
	
	//1. 전체 선택/해제
    selectAllCheckbox.addEventListener("change", function () {
        const isChecked = selectAllCheckbox.checked;
        rowCheckboxes.forEach(checkbox => {
            checkbox.checked = isChecked;
        });
    });
	//2. 개별 상태 변경 시, 전체 체크 박스 상태 변경
	rowCheckboxes.forEach(checkbox => {
		checkbox.addEventListener("change", function() {
			const allChecked = Array.from(rowCheckboxes).every(cb => cb.checked);
			const noneChecked = Array.from(rowCheckboxes).every(cb => !cb.checked);
			if (allChecked) {
				selectAllCheckbox.checked = true;
				selectAllCheckbox.indeterminate = false;
			} else if(noneChecked) {
				selectAllCheckbox.checked = false;
				selectAllCheckbox.indeterminate = false;
			} else {
				selectAllCheckbox.indeterminate = true;
			}
		});
	});
	/* end of 체크 박스 이벤트 부여 */
	
	//모드 단일 토글 이벤트 부여
	const modeButtons = document.querySelectorAll(".btn-toggle-mode");
	modeButtons.forEach(button => {
		button.addEventListener("click", function() {
			toggleMode(button);
		});
	});
	
	//모드 전체 전환 이벤트 부여
	const mockButton = document.querySelector(".btn-api-mock");
	const realButton = document.querySelector(".btn-api-real");
	mockButton.addEventListener("click", function() { changeApiMode(false); });
	realButton.addEventListener("click", function() { changeApiMode(true); });
	
	//헬스 체크 이벤트 부여
	const healthButtons = document.querySelectorAll(".btn-healthcheck");
	healthButtons.forEach(button => {
		button.addEventListener("click", function() {
			performHealthCheck(button);
		});
	})
	
	//실행 이벤트 부여
	const executeButtons = document.querySelectorAll(".btn-execute");
	executeButtons.forEach(button => {
		button.addEventListener("click", function() {
			executeApi(button);
		});
	})
	
});

// 수정 데이터 저장
const saveEditApi = (id) => {
	// 현재 활성화된 상태 데이터 저장
    saveEditStateData();

    const apiName = document.getElementById("editApiName").value;
    const apiUrl = document.getElementById("editApiUrl").value;
	
	// 상태별 고정값 적용 함수
    const applyFixedFormat = (state) => {
    let mappings = editStateData[state].mappings;
    let files = editStateData[state].files;

    if (state === "normal") {
        const fixedUrl = `"url": "/mock/api/{id}"`;
        const fixedBodyFileName = `"bodyFileName": "{id}-{apiName}-response.json"`;

        // URL과 bodyFileName에 고정값 적용
        if (!mappings.includes(fixedUrl)) {
            mappings = mappings.replace(/"url":\s*".*?"/, fixedUrl);
        }
        if (!mappings.includes(fixedBodyFileName)) {
            mappings = mappings.replace(/"bodyFileName":\s*".*?"/, fixedBodyFileName);
        }
    } else if (state === "delay") {
        const fixedBodyFileName = `"bodyFileName": "{id}-{apiName}-delay-response.json"`;
        if (!mappings.includes(fixedBodyFileName)) {
            mappings = mappings.replace(/"bodyFileName":\s*".*?"/, fixedBodyFileName);
        }
    } else if (state === "error") {
        const fixedBodyFileName = `"bodyFileName": "{id}-{apiName}-bad-response.json"`;
        if (!mappings.includes(fixedBodyFileName)) {
            mappings = mappings.replace(/"bodyFileName":\s*".*?"/, fixedBodyFileName);
        }
    }
	
    // 업데이트된 데이터를 반영
    editStateData[state].mappings = mappings;
    editStateData[state].files = files;
	};
	
	// 모든 상태에 고정값 포맷 적용
    applyFixedFormat("normal", apiName);
    applyFixedFormat("delay", apiName);
    applyFixedFormat("error", apiName);

    fetch(`/api/edit/${id}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            apiName,
            apiUrl,
			normalMappings: editStateData.normal.mappings,
            normalFiles: editStateData.normal.files,
            delayMappings: editStateData.delay.mappings,
            delayFiles: editStateData.delay.files,
            errorMappings: editStateData.error.mappings,
            errorFiles: editStateData.error.files,
        }),
    })
        .then(response => {
            if (response.ok) {
                alert("API가 성공적으로 수정되었습니다.");
                window.location.reload();
            } else {
                alert("API 수정 중 문제가 발생했습니다.");
            }
        })
        .catch(error => {
            console.error("Error updating API:", error);
            alert("네트워크 오류가 발생했습니다.");
        });
};

// API 추가 함수
const addNewApi = () => {
	
	const apiName = document.getElementById("apiName").value;
    const apiUrl = document.getElementById("apiUrl").value;

    // 현재 상태 데이터를 가져옵니다.
    const currentStatus = document.getElementById("statusSelector").value;
    let apiMappings = document.getElementById("apiMappings").value;
    let apiFiles = document.getElementById("apiFiles").value;

    // 상태별 고정값 적용
    if (currentStatus === "normal") {
        const fixedUrl = '"url": "/mock/api/{id}"';
        const fixedBodyFileName = '"bodyFileName": "{id}-{apiName}-response.json"';

        if (!apiMappings.includes(fixedUrl)) {
            apiMappings = apiMappings.replace(/"url":\s*".*?"/, fixedUrl);
        }
        if (!apiMappings.includes(fixedBodyFileName)) {
            apiMappings = apiMappings.replace(/"bodyFileName":\s*".*?"/, fixedBodyFileName);
        }
    } else if (currentStatus === "delay") {
        const fixedBodyFileName = '"bodyFileName": "{id}-{apiName}-delay-response.json"';

        if (!apiMappings.includes(fixedBodyFileName)) {
            apiMappings = apiMappings.replace(/"bodyFileName":\s*".*?"/, fixedBodyFileName);
        }
    } else if (currentStatus === "error") {
        const fixedBodyFileName = '"bodyFileName": "{id}-{apiName}-bad-response.json"';

        if (!apiMappings.includes(fixedBodyFileName)) {
            apiMappings = apiMappings.replace(/"bodyFileName":\s*".*?"/, fixedBodyFileName);
        }
    }

    // 상태별 데이터 저장소 업데이트
    stateData[currentStatus] = {
        mappings: apiMappings,
        files: apiFiles,
    };

    // 모든 상태 데이터를 전송
    const payload = {
        apiName,
        apiUrl,
        normalMappings: stateData.normal.mappings,
        normalFiles: stateData.normal.files,
        delayMappings: stateData.delay.mappings,
        delayFiles: stateData.delay.files,
        errorMappings: stateData.error.mappings,
        errorFiles: stateData.error.files,
    };
	
	if (!apiName || !apiUrl) {
        alert("API 이름과 주소를 입력해주세요.");
        return;
    }

    fetch('/api/add', {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (response.status === 201) {
            alert("API가 성공적으로 추가되었습니다.");
            window.location.reload();
        } else {
            alert("API 추가 중 문제가 발생했습니다.");
        }
    })
    .catch(error => {
        console.error("Error:", error);
        alert("네트워크 오류가 발생했습니다.");
    });
};

// files 수정 시 지연, 오류 mapping 수정
const apiFilesField = document.getElementById("apiFiles");

apiFilesField.addEventListener("input", () => {
    const statusType = document.getElementById("statusSelector").value;
    let mappings = document.getElementById("apiMappings").value;

    // 상태별 bodyFileName 업데이트
	let targetBodyFileName;
	let targetUrlPattern;
    if (statusType === "delay") {
		targetUrlPattern = '"urlPattern": "/mock/stub/delay/{id}"'
        targetBodyFileName = '"bodyFileName": "{id}-{apiName}-delay-response.json"';
    } else if (statusType === "error") {
		targetUrlPattern = '"urlPattern": "/mock/stub/bad/{id}"'
        targetBodyFileName = '"bodyFileName": "{id}-{apiName}-bad-response.json"';
    }

	// bodyFileName이 이미 원하는 값인지 확인
    if (targetBodyFileName && !mappings.includes(targetBodyFileName)) {
        // 기존 bodyFileName 값을 새로운 값으로 변경
        mappings = mappings.replace(/"bodyFileName":\s*".*?"/, targetBodyFileName);
        
        
    }
	// urlPattern이 이미 원하는 값인지 확인하고 수정
    if (targetUrlPattern && !mappings.includes(targetUrlPattern)) {
        mappings = mappings.replace(/"urlPattern":\s*".*?"/, targetUrlPattern);
    }
	
	// 업데이트된 mappings, urlPattern 적용
	document.getElementById("apiMappings").value = mappings;
	
});

// Mappings, Files 템플릿 기본 포맷
const templates = {
    normal: {
        mappings: `{
  "request": {
    "method": "GET",
    "url": "/mock/api/{id}"
  },
  "response": {
    "status": 200,
    "bodyFileName": "{id}-{apiName}-response.json",
	"headers": {
      "Content-Type": "application/json",
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type, Authorization"
    }
  }
}`,
        files: `{
  "message": "정상 응답입니다."
}`
    },
    delay: {
        mappings: `{
  "request": {
    "method": "ANY",
    "urlPattern": "/mock/stub/delay"
  },
  "response": {
    "status": 200,
    "bodyFileName": "common-delay-response.json",
	"headers": {
      "Content-Type": "application/json",
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type, Authorization"
    },
    "fixedDelayMilliseconds": 3000
  }
}`,
        files: `{
  "message": "지연 응답입니다."
}`
    },
    error: {
        mappings: `{
  "request": {
    "method": "ANY",
    "urlPattern": "/mock/stub/bad"
  },
  "response": {
    "status": 500,
    "bodyFileName": "common-bad-response.json",
	"headers": {
      "Content-Type": "application/json",
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type, Authorization"
    }
  }
}`,
        files: `{
  "message": "에러 응답입니다."
}`
    }
};

// 상태별 데이터 임시 저장소
const stateData = {
    normal: { mappings: templates.normal.mappings, files: templates.normal.files },
    delay: { mappings: templates.delay.mappings, files: templates.delay.files },
    error: { mappings: templates.error.mappings, files: templates.error.files },
};

// 템플릿 설정 및 데이터 복원
const setMappingTemplate = (type) => {
    if (templates[type]) {
        // 현재 상태의 입력값 저장
        const currentType = document.getElementById("statusSelector").value;
        stateData[currentType] = {
            mappings: document.getElementById("apiMappings").value || templates[currentType].mappings,
            files: document.getElementById("apiFiles").value || templates[currentType].files,
        };

        // 새로운 상태 설정
        document.getElementById("statusSelector").value = type;
        document.getElementById("apiMappings").value = stateData[type].mappings;
        document.getElementById("apiFiles").value = stateData[type].files;
    } else {
        console.error("알 수 없는 템플릿 타입:", type);
    }
};

// JSON 검증 함수
const isValidJson = (input) => {
    try {
        JSON.parse(input); // JSON 파싱 시도
		console.log("Parsed JSON:", json);
        return true; // 유효한 JSON 형식
    } catch (error) {
        return false; // JSON 파싱 실패
    }
};

// API Mappings 입력 시 실시간 검증
const apiMappingsField = document.getElementById("apiMappings");
apiMappingsField.addEventListener("input", () => {
	const statusType = document.getElementById("statusSelector").value; // 현재 상태 가져오기
	    let currentValue = apiMappingsField.value;

	    if (statusType === "normal") {
	        const fixedUrl = '"url": "/mock/api/{id}"';
	        const fixedBodyFileName = '"bodyFileName": "{id}-{apiName}-response.json"';

	        // url 및 bodyFileName 고정값 적용
	        if (!currentValue.includes(fixedUrl)) {
	            currentValue = currentValue.replace(/"url":\s*".*?"/, fixedUrl);
	        }
	        if (!currentValue.includes(fixedBodyFileName)) {
	            currentValue = currentValue.replace(/"bodyFileName":\s*".*?"/, fixedBodyFileName);
	        }
	    } else if (statusType === "delay") {
	        const fixedUrlPattern = '"urlPattern": "/mock/stub/delay"';
	        const fixedBodyFileName = '"bodyFileName": "{id}-{apiName}-delay-response.json"';

	        // urlPattern 및 bodyFileName 고정값 적용
	        if (!currentValue.includes(fixedUrlPattern)) {
	            currentValue = currentValue.replace(/"urlPattern":\s*".*?"/, fixedUrlPattern);
	        }
	        if (!currentValue.includes(fixedBodyFileName)) {
	            currentValue = currentValue.replace(/"bodyFileName":\s*".*?"/, fixedBodyFileName);
	        }
	    } else if(statusType === "error"){
			const fixedUrlPattern = '"urlPattern": "/mock/stub/bad"';
	        const fixedBodyFileName = '"bodyFileName": "{id}-{apiName}-bad-response.json"';

	        // urlPattern 및 bodyFileName 고정값 적용
	        if (!currentValue.includes(fixedUrlPattern)) {
	            currentValue = currentValue.replace(/"urlPattern":\s*".*?"/, fixedUrlPattern);
	        }
	        if (!currentValue.includes(fixedBodyFileName)) {
	            currentValue = currentValue.replace(/"bodyFileName":\s*".*?"/, fixedBodyFileName);
	        }
		}

	    // 수정된 값 반영
	    apiMappingsField.value = currentValue;
	});

// API 삭제
const deleteApi = (button) => {
    const id = button.getAttribute("data-id");

    if (!confirm(`${id}번 API를 삭제하시겠습니까?`)) {
        return;
    }

    fetch(`/api/delete/${id}`, {
        method: "DELETE"
    })
    .then(response => {
        if (response.status === 200) {
            alert("API가 성공적으로 삭제되었습니다.");
            window.location.reload();
        } else {
            alert("API 삭제 중 오류가 발생했습니다.");
        }
    })
    .catch(error => {
        console.error("Error:", error);
        alert("네트워크 오류가 발생했습니다.");
    });
};

const toggleMode = (button) => {
	const id = button.getAttribute("data-id");
	
	fetch('/api/toggle-mode', {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: id
	})
	.then(response => {
		if(response.status === 200) {
		    window.location.reload();
		} else {
		    alert("시스템 오류가 발생하였습니다.");
		}
	})
	.catch(error => {
		console.error("Error:", error);
		alert("네트워크 오류가 발생했습니다.");
	});
}

const changeApiMode = (targetMode) => {
	const checkboxes = document.querySelectorAll(".row-checkbox");
	const ids = getSelectedIdsFromCheckbox(checkboxes);
	
	if(ids.length === 0) {
		alert("전환할 API를 선택해주세요.");
		return;
	}
	
	fetch('/api/change-mode-selected', {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			ids: ids,
			targetMode
		})
	})
	.then(response => {
		if(response.status === 200) {
			if(targetMode === false) alert("선택한 API의 모드를 대응답으로 전환했습니다.");
			else alert("선택한 API 모드를 실서버로 전환했습니다.");
			window.location.reload();
		} else {
			alert("시스템 오류가 발생하였습니다.");
		}
	})
	.catch(error => {
		console.error("Error:", error);
		alert("네트워크 오류가 발생했습니다.");
	});
}

const performHealthCheck = (button) => {
	if (isRequestInProgress) {
        alert("Health Check가 진행 중입니다. 잠시 후 다시 시도해주세요.");
        return;
    }

	const id = button.getAttribute("data-id");
	isRequestInProgress = true;//요청 상태 플래그 설정
		
	fetch('/api/health-check', {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: id
	})
	.then(response => {
		if(response.status === 200) {
			alert(`${id}번 API의 Health Check가 성공적으로 완료되었습니다.`);
		    window.location.reload();
		} else {
		    alert(`${id}번 API의 Health Check 진행 중 오류가 발생했습니다.`);
		}
	})
	.catch(error => {
		console.error("Error:", error);
		alert("네트워크 오류가 발생했습니다.");
	})
	.finally(() => {
		isRequestInProgress = false;//요청 상태 플래그 해제
	})
}

const executeApi = (button) => {
	const id = button.getAttribute("data-id");
	
	const newWindow = window.open("", "_blank");
    fetch(`/api/execute/${id}`, {
        method: "GET",
        redirect: "follow"
    })
    .then(response => {
        if(response.redirected) {
			newWindow.location.href = response.url;
			window.location.reload();
		} else throw new Error("API 실행 중 리디렉션이 발생하지 않았습니다.");
    })
    .catch(error => {
        console.error("Error:", error);	
        newWindow.close();
        alert("API 실행 중 오류가 발생했습니다. 다시 시도해주세요.");
    });
}

const getSelectedIdsFromCheckbox = (checkboxes) => {
	const selectedIds = [];
	checkboxes.forEach(checkbox => {
		if(checkbox.checked) {
			const id = checkbox.getAttribute("data-id");
			selectedIds.push(parseInt(id, 10));
		}
	});
	return selectedIds;
}

// testApi 함수 정의
function testApi(id, type) {
    // 서버로 보낼 URL 생성
    const url = `/api/test/${id}/${type}`;
	const newWindow = window.open("", "_blank");
    
    // fetch로 백엔드 API 호출
	fetch(url, {
        method: "GET",
        redirect: "follow" // 리디렉션 허용
    })
        .then(response => {
            if (response.redirected) {
                // 리디렉션된 URL로 새 창 이동
                newWindow.location.href = response.url;
            } else {
                throw new Error("API 실행 중 리디렉션이 발생하지 않았습니다.");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            newWindow.close(); // 새 창 닫기
            alert("API 실행 중 오류가 발생했습니다. 다시 시도해주세요.");
        });
}

