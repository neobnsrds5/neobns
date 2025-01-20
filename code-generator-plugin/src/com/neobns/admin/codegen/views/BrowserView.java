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
		BrowserFunction prefs = new ListTables(fBrowser, "invokeListTables"); // js에서 invokeListTables를 호출하면 ListTables 메소드라 호출됨
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
					arguments[2].toString(), arguments[3].toString(), arguments[4].toString(), arguments[5].toString());
		}
	}
	
	public String getContent() {
		String js = null;
		try (InputStream inputStream = getClass().getResourceAsStream("BrowserView.js")) {
			js = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append("<!doctype html>");
		buffer.append("<html lang=\"en\">");
		buffer.append("<head>");
		buffer.append("<meta charset=\"utf-8\">");
		buffer.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		buffer.append("<title>Code Generator</title>");
		buffer.append("<script>" + js + "</script>"); // 자바스크립트 내용을 추가
		buffer.append("</script>");
		buffer.append("</head>");
		buffer.append("<body>");
		buffer.append("<form>");
		buffer.append("<label>데이터베이스 URL:</label><br>");
		buffer.append("<input type=\"text\" id=\"dbUrl\" name=\"dbUrl\" value=\"jdbc:mysql://localhost:3306/db2\"><br>");
		buffer.append("<label>사용자명:</label><br>");
		buffer.append("<input type=\"text\" id=\"username\" name=\"username\" value=\"root\"><br>");
		buffer.append("<label>비밀번호:</label><br>");
		buffer.append("<input type=\"password\" id=\"password\" name=\"password\" value=\"1234\"><br>");
		buffer.append("<label>저장할 폴더명:</label><br>");
		buffer.append("<input type=\"text\" id=\"targetPath\" name=\"targetPath\" value=\"example\"><br>");
		buffer.append("</form>");
		buffer.append("<input id=button type=\"button\" value=\"전체 테이블 조회\" onclick=\"listTables();\">");
		buffer.append("<div id=\"tables\"></div>");
		buffer.append("</body>");
		buffer.append("</html>");
		return buffer.toString();
	}

}
