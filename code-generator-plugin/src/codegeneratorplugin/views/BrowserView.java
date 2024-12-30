package codegeneratorplugin.views;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import code_generator_plugin.common.DatabaseConnector;
import code_generator_plugin.common.GenerateCodeHandler;
import code_generator_plugin.common.MyBatisGeneratorProgrammatic;
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
	public static final String ID = "codegeneratorplugin.views.BrowserView";

	@Inject
	Shell shell;


	private Browser fBrowser;

	@Override
	public void createPartControl(Composite parent) {
//		fBrowser = new Browser(parent, SWT.WEBKIT); // webkit 으로 실행 시 동작 X
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
				return DatabaseConnector.getTables(arguments[0].toString(), arguments[1].toString(), arguments[2].toString())
						.stream().collect(Collectors.joining(",")); // js로 결과를 반환하기 위해 콤마로 구분
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
					arguments[2].toString(), arguments[3].toString());
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
		buffer.append("<title>Sample View</title>");
		buffer.append("<script>" + js + "</script>"); // 자바스크립트 내용을 추가
		buffer.append("</script>");
		buffer.append("</head>");
		buffer.append("<body>");
		buffer.append("<form>");
		buffer.append("<label>Data Source URL:</label><br>");
		buffer.append("<input type=\"text\" id=\"dbUrl\" name=\"dbUrl\" value=\"jdbc:mysql://localhost:3306/db2\"><br>");
		buffer.append("<label>User Name:</label><br>");
		buffer.append("<input type=\"text\" id=\"username\" name=\"username\"><br>");
		buffer.append("<label>Password:</label><br>");
		buffer.append("<input type=\"password\" id=\"password\" name=\"password\"><br>");
		buffer.append("<label>Target Path:</label><br>");
		buffer.append("<input type=\"text\" id=\"targetPath\" name=\"targetPath\"><br>");
		buffer.append("</form>");
		buffer.append("<input id=button type=\"button\" value=\"Show Tables\" onclick=\"listTables();\">");
		buffer.append("<h3>Table List</h3>");
		buffer.append("<div id=\"tables\"></div>");
		buffer.append("</body>");
		buffer.append("</html>");
		return buffer.toString();
	}

}
