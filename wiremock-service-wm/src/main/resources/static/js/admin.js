//전역 상태 플래그
let isRequestInProgress = false;

document.addEventListener("DOMContentLoaded", function () {

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
	
	// "API 추가" 버튼 클릭 시 팝업 열기
    const apiAddButton = document.querySelector(".btn-api-add");
    apiAddButton.addEventListener("click", function () {
        const apiAddModal = new bootstrap.Modal(document.getElementById("apiAddModal"));
        apiAddModal.show();
    });
	
	// "저장" 버튼 클릭 시 API 추가
    const saveApiButton = document.getElementById("saveApiButton");
    saveApiButton.addEventListener("click", function () {
        addNewApi();
    });
	
});

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

// API 추가 함수
const addNewApi = () => {
	
	const apiName = document.getElementById("apiName").value;
    const apiUrl = document.getElementById("apiUrl").value;
	
	if (!apiName || !apiUrl) {
	        alert("API 이름과 주소를 입력해주세요.");
	        return;
	}
	
    // 모든 상태 데이터를 전송
    const payload = {
        apiName,
        apiUrl,
    };

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
	        alert("Json Validation 에러가 발생했습니다.");
	    }
	})
    .catch(error => {
        console.error("Error:", error);
        alert("네트워크 오류가 발생했습니다.");
    });
};



