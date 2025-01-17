// Call from Java to set the random background color
function changeColor() {
	var x = Math.floor(Math.random() * 256);
	var y = Math.floor(Math.random() * 256);
	var z = Math.floor(Math.random() * 256);
	var bgColor = "rgb(" + x + "," + y + "," + z + ")";
	document.body.style.background = bgColor;
	document.getElementById("lastAction").innerText = "Background color set to " + bgColor;
}

// Call from Java to set the current selection
function setSelection(text) {
	document.getElementById("selection").innerText = text;
	document.getElementById("lastAction").innerText = "Selection set to " + text;
}

// Call to Java to open the preferences
function listTables() {
	try {
		document.getElementById("tables").innerHTML="";
		var url = document.getElementById("dbUrl").value;
		var username = document.getElementById("username").value;
		var password = document.getElementById("password").value;
		if(!url) {
			alert("please provide url");
			return;
		}
		if(!username) {
			alert("please provide username");
			return;
		}
		if(!password) {
			alert("please provide password");
			return;
		}
		var tableList = JSON.parse(invokeListTables(url,username,password)); // java의 ListTables 메소드 호출
		if(tableList) {
			tableList.forEach(table => {
				const divElement = document.createElement('div');
				let tableName = '<div>${table.tableName}</div>';
				let codegenBtn = '<button type="button" class="btn btn-secondary" onClick = \'generateCode("${table.tableName}")\'>${table.tableName} 생성</button>';
				let tableHead = '<tr>';
				let tableBody = '<tr>';
				let columnsArray = Object.entries(table.columns); // key 값을 알 수 없으므로 배열로 변환 후 index 로 접근
				columnsArray.forEach(column => {
					const [key, value] = column;
					tableHead += ('<th>' + key + '</th>');
					tableBody += ('<td>' + value + '</td>');
				})
				divElement.innerHTML = tableName + '<table><thead>' + tableHead + '</thead><tbody>' + tableBody + '</tbody></table>' + codegenBtn;
				
				document.getElementById("tables").appendChild(divElement);
		});
		const element = document.createElement('div');
		element.innerHTML = `<button type="button" class="btn btn-secondary" onClick = 'generateCode("${url}","${username}","${password}")'>모든 테이블 생성</button>`;
		document.getElementById("tables").appendChild(element);
		} else {
			alert("Invalid value ");
		}
		
	} catch (e) {
		alert("Error occured " + e.message);
	}
}

// Call from java to say something
function generateCode(url,username,password) {
	var targetPath = document.getElementById("targetPath").value;
	if(!targetPath) {
		alert("please provide targetPath");
		return;
	}
	invokeGenerateCode(url,username,password,targetPath); // java의 GenerateCode 메소드 호출
	invokeJUnitTestGenerator(targetPath); // java의 jUnitTestGenerator 메소드 호출
}
