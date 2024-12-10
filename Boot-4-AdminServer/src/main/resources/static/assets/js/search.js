document.addEventListener('DOMContentLoaded', function () {

    // 서버에 검색 요청
    function fetchLogsFromServer() {
        const searchCriteria = document.getElementById('searchCriteria').value; // 검색 기준
        const searchValue = document.getElementById('searchInput').value.trim(); // 검색 값

        if (searchValue === '') {
            alert("검색 값을 입력하세요.");
            return;
        }
	// 현재 페이지의 URL 경로를 확인하여 API 경로를 설정
	    const currentPath = window.location.pathname;
	    let apiEndpoint;
	    if (currentPath.includes("errors")) {
	        apiEndpoint = "/admin/api/logs/error/search";
	    } else if (currentPath.includes("slow")) {
	        apiEndpoint = "/admin/api/logs/slow/search";
	    }

        // 수정된 fetch 요청 URL
        fetch(`${apiEndpoint}?criteria=${searchCriteria}&value=${searchValue}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('서버 응답 오류');
                }
                return response.json();
            })
            .then(data => {
                updateTable(data); // 검색 결과로 테이블 업데이트
            })
            .catch(error => {
                console.error('검색 요청 중 오류 발생:', error);
                alert("검색 중 오류가 발생했습니다.");
            });
    }

    // 테이블 업데이트 함수
    function updateTable(logs) {
        const tableBody = document.getElementById('logTableBody');
        tableBody.innerHTML = ''; // 기존 데이터 초기화

        logs.forEach(log => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${log.timestmp || 'N/A'}</td>
                <td class="text-start trace-id">
                    ${log.traceId ? `<a href="/admin/trace?traceId=${log.traceId}" target="_blank">${log.traceId}</a>` : 'N/A'}
                </td>
                <td class="text-start ip-address">${log.ipAddress || 'N/A'}</td>
                <td>${log.executeResult || 'N/A'}</td>
                <td>${log.callerClass || 'N/A'}</td>
                <td>${log.callerMethod || 'N/A'}</td>
                <td>${log.uri || 'N/A'}</td>
                <td class="text-start query">${log.query || 'N/A'}</td>
                <td class="text-start user-id">${log.userId || 'N/A'}</td>
            `;
            tableBody.appendChild(row);
        });
    }

    // 검색 버튼 이벤트
    document.getElementById('searchButton').addEventListener('click', fetchLogsFromServer);

    // Enter 키 입력 시 검색
    document.getElementById('searchInput').addEventListener('keypress', function (event) {
        if (event.key === 'Enter') {
            fetchLogsFromServer();
        }
    });
});

document.querySelectorAll('.text-start.trace-id a').forEach(link => {
		    link.addEventListener('click', function (event) {
		        event.preventDefault(); // 기본 링크 이동 동작 방지

		        const url = this.href; // 클릭된 링크의 URL 가져오기
		        const popupWidth = Math.min(window.screen.width * 0.9, 1400); // 화면의 90% 또는 최대 1200px
		        const popupHeight = Math.min(window.screen.height * 0.9, 900); // 화면의 90% 또는 최대 900px
		        const left = (window.screen.width - popupWidth) / 2; // 화면 중앙 계산
		        const top = (window.screen.height - popupHeight) / 2; // 화면 중앙 계산

		        // 팝업 창 열기
		        window.open(
		            url,
		            '_blank',
		            `width=${popupWidth},height=${popupHeight},top=${top},left=${left},resizable=yes,scrollbars=yes`
		        );

		        console.log(`팝업 창 열림: ${url}`);
		    });
		});
