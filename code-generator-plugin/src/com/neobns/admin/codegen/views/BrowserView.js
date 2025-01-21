// innerHTML을 사용하여 요소를 생성하는 유틸리티 함수
function createElementWithHTML(tag, html) {
	const element = document.createElement(tag);
	element.innerHTML = html;
	return element;
}

// 랜덤 배경색을 생성하는 함수
function changeColor() {
	const x = Math.floor(Math.random() * 256);
	const y = Math.floor(Math.random() * 256);
	const z = Math.floor(Math.random() * 256);
	const bgColor = `rgb(${x}, ${y}, ${z})`;
	document.body.style.background = bgColor;
	document.getElementById("lastAction").innerText = `배경색이 ${bgColor}로 설정되었습니다.`;
}

// 현재 선택된 텍스트를 설정하는 함수
function setSelection(text) {
	document.getElementById("selection").innerText = text;
	document.getElementById("lastAction").innerText = `선택된 텍스트가 ${text}로 설정되었습니다.`;
}

// Java에서 테이블 정보를 가져와 목록으로 표시하는 함수
function listTables() {
	try {
		const tablesContainer = document.getElementById("tables");
		tablesContainer.innerHTML = "<h3>테이블 목록</h3>";
		
		// DB 정보
		const url = document.getElementById("dbUrl").value;
		const username = document.getElementById("username").value;
		const password = document.getElementById("password").value;
		// 저장할 파일명
		const targetPath = document.getElementById("targetPath").value;
		
		if (!url || !username || !password || !targetPath) {
			alert("URL, 사용자 이름, 비밀번호 및 파일명을 모두 입력해주세요.");
			return;
		}

		const tableList = JSON.parse(invokeListTables(url, username, password)); // Java의 ListTables 메소드 호출

		if (tableList && Array.isArray(tableList)) {
			tableList.forEach(table => {
				const columnsArray = Object.entries(table.columns); // 객체를 배열로 변환하여 인덱스 기반 접근

				const columnName = columnsArray.map(([key]) => `<td>${key}</td>`).join("");
				const columnInfo = columnsArray.map(([key, value]) => `<td><input type='radio' name='pk_${table.tableName}' value='${key}' ${value ? 'checked' : ''}></td>`).join("");

				const tableHTML = `
                    <div>${table.tableName}</div>
                    <table border='1'>
                        <tr><th>컬럼명</th>${columnName}</tr>
                        <tr><th>기본키</th>${columnInfo}</tr>
                    </table>
					<button type="button" class="btn btn-secondary" onClick='generateCode("${url}", "${username}", "${password}", "${table.tableName}", "${targetPath}")'>${table.tableName} 생성</button>
			   	`;

				const tableElement = createElementWithHTML('div', tableHTML);
				tablesContainer.appendChild(tableElement);
			});
		} else {
			alert("유효하지 않은 값입니다.");
		}

	} catch (e) {
		alert(`오류가 발생했습니다: ${e.message}`);
	}
}

// 기본키를 확인하고 generateCode 호출
function generateCode(url, username, password, tableName, targetPath) {
	
    // 해당 테이블의 선택된 기본키 찾기
    const selectedPK = document.querySelector(`input[name='pk_${tableName}']:checked`);
    
    if (!selectedPK) {
        alert(`테이블 ${tableName}에 대해 기본키를 선택해주세요.`);
        return;
    }

    const primaryKey = selectedPK.value; // 선택된 컬럼명 가져오기

	invokeGenerateCode(url, username, password, targetPath, tableName, primaryKey);
}
