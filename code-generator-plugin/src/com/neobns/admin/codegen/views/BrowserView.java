package com.neobns.admin.codegen.views;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import com.neobns.admin.codegen.common.DatabaseConnector;
import com.neobns.admin.codegen.common.MyBatisGeneratorProgrammatic;

import jakarta.inject.Inject;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view with html and javascript content. The view 
 * shows how data can be exchanged between Java and JavaScript.
 */

public class BrowserView extends ViewPart  {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.neobns.admin.codegen.views.BrowserView";

	@Inject
	Shell shell;


	private Browser fBrowser;

	@Override
	public void createPartControl(Composite parent) {
		fBrowser = new Browser(parent, SWT.EDGE);
		fBrowser.setText(getContent());
		BrowserFunction prefs = new ListTables(fBrowser, "invokeListTables"); // js에서 invokeListTables를 호출하면 ListTables 메소드가 호출됨
		fBrowser.addDisposeListener(e -> prefs.dispose());
		BrowserFunction codeGen = new GenerateCode(fBrowser, "invokeGenerateCode"); // js에서 invokeGenerateCode를 호출하면 GenerateCode 메소드가 호출됨
		fBrowser.addDisposeListener(e -> codeGen.dispose());
	}

	@Override
	public void setFocus() {
		fBrowser.setFocus();
	}

	private class ListTables extends BrowserFunction { // 데이터베이스 테이블 목록 가져오기

		ListTables(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			try {
				return DatabaseConnector.getTablesInfo(arguments[0].toString(), arguments[1].toString(), arguments[2].toString()).toString();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private class GenerateCode extends BrowserFunction { // 입력한 데이터를 기반으로 코드 생성

		GenerateCode(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			return MyBatisGeneratorProgrammatic.execute(shell, arguments[0].toString(), arguments[1].toString(),
					arguments[2].toString(), arguments[3].toString(), arguments[4].toString());
		}
	}
	
	public String getContent() {
		String js = null;
		try (InputStream inputStream = getClass().getResourceAsStream("BrowserView.js")) {
			js = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<!doctype html>")
			.append("<html lang=\"en\">")
			.append("<head>")
			.append("<meta charset=\"utf-8\">")
			.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
			.append("<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css\"")
			.append(" rel=\"stylesheet\" crossorigin=\"anonymous\"")
			.append(" integrity=\"sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH\">")
			.append("<title>Code Generator</title>")
			.append("<script>" + js + "</script>") // 자바스크립트 내용을 추가
			.append("</script>")
			.append("</head>")
			.append("<body>")
			.append("<form>")
			.append("<label>데이터베이스 URL:</label><br>")
			.append("<input class=\"w-50\" type=\"text\" id=\"dbUrl\" name=\"dbUrl\" value=\"jdbc:mysql://localhost:3306/db2\"><br>")
			.append("<label>사용자명:</label><br>")
			.append("<input class=\"w-50\" type=\"text\" id=\"username\" name=\"username\" value=\"root\"><br>")
			.append("<label>비밀번호:</label><br>")
			.append("<input class=\"w-50\" type=\"password\" id=\"password\" name=\"password\" value=\"1234\"><br>")
			.append("<label>저장할 폴더명:</label><br>")
			.append("<input class=\"w-50\" type=\"text\" id=\"targetPath\" name=\"targetPath\" value=\"example\"><br>")
			.append("</form>")
			.append("<input class=\"btn btn-primary\" type=\"button\" value=\"전체 테이블 조회\" onclick=\"listTables();\">")
			.append("<div id=\"tables\"></div>")
			.append("</body>")
			.append("<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js\" crossorigin=\"anonymous\"")
			.append(" integrity=\"sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz\"></script>")
			.append("</html>");
		
		return sb.toString();
	}

}
