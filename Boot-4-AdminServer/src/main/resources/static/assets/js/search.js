// search.js

document.addEventListener('DOMContentLoaded', function () {

    // 검색 기능 구현
    function filterLogs() {

        // 검색 기준과 검색창 값 가져오기
        const searchCriteria = document.getElementById('searchCriteria').value; // 선택된 기준
        const searchValue = document.getElementById('searchInput').value.toLowerCase().trim(); // 입력값

        // 테이블의 모든 행 가져오기
        const rows = document.querySelectorAll('#logTableBody tr');

        let foundMatch = false; // 검색 결과 여부 확인 플래그

        rows.forEach(row => {
            let cellToCheck = null;

            // 선택된 검색 기준에 따라 검색할 셀 선택
            if (searchCriteria === 'traceId') {
                cellToCheck = row.querySelector('.text-start.trace-id a');
            } else if (searchCriteria === 'userId') {
                cellToCheck = row.querySelector('.text-start.user-id');
            } else if (searchCriteria === 'ipAddress') {
                cellToCheck = row.querySelector('.text-start.ip-address');
            } else if (searchCriteria === 'query') {
                cellToCheck = row.querySelector('.text-start.query');
            }

            // 빈 검색값 처리
            if (searchValue === '') {
                row.style.display = ''; // 빈값 검색 시 모든 행 표시
                foundMatch = true;
                return; // 다음 반복으로 이동
            }

            // 셀의 값을 검색 조건과 비교
            if (cellToCheck) {
                const cellValue = cellToCheck.textContent.trim().toLowerCase();

                // 포함 여부로 필터링
                if (cellValue.includes(searchValue)) {
                    row.style.display = ''; // 조건에 맞으면 행 표시
                    foundMatch = true;
                } else {
                    row.style.display = 'none'; // 조건에 맞지 않으면 행 숨기기
                }
            }
        });

        // 검색 결과가 없을 경우 알림
        if (!foundMatch) {
            alert("검색 결과가 없습니다.");
        }
    }

    // 검색 버튼 클릭 이벤트 연결
    const searchButton = document.getElementById('searchButton');
    if (searchButton) {
        searchButton.addEventListener('click', filterLogs);
        console.log("검색 버튼 이벤트 연결 완료");
    } else {
        console.error("검색 버튼(searchButton) 요소를 찾을 수 없습니다.");
    }

    // 검색창에서 Enter 키 입력 이벤트 연결
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function (event) {
            if (event.key === 'Enter') {
                filterLogs();
            }
        });
        console.log("검색창 Enter 키 이벤트 연결 완료");
    } else {
        console.error("검색 입력창(searchInput) 요소를 찾을 수 없습니다.");
    }
	
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
});
