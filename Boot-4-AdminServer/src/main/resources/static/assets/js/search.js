// DOMContentLoaded 이벤트를 사용하여 DOM이 완전히 로드된 후 실행
document.addEventListener('DOMContentLoaded', function () {
    console.log("search.js 로드 완료 및 실행");

    // 검색 기능 구현
    function filterLogs() {
        console.log("filterLogs 함수 실행"); // 디버그용 로그

        const searchValue = document.getElementById('traceIdSearch').value.toLowerCase().trim();
        const rows = document.querySelectorAll('#logTableBody tr');

        rows.forEach(row => {
            const traceIdCell = row.querySelector('.text-start.trace-id a');

            if (traceIdCell) {
                const traceId = traceIdCell.textContent.trim().toLowerCase();

                if (traceId.includes(searchValue) || searchValue === '') { // 'startsWith' 대신 'includes'로 변경
                    row.style.display = ''; // 조건에 맞으면 행 표시
                } else {
                    row.style.display = 'none'; // 조건에 맞지 않으면 행 숨기기
                }
            }
        });
    }

    const searchButton = document.getElementById('searchButton');
    if (searchButton) {
        searchButton.addEventListener('click', filterLogs);
    }

    const searchInput = document.getElementById('traceIdSearch');
    if (searchInput) {
        searchInput.addEventListener('keypress', function (event) {
            if (event.key === 'Enter') {
                filterLogs();
            }
        });
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
