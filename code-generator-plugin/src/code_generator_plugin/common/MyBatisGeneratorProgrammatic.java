package code_generator_plugin.common;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;

import code_generator_plugin.dto.TableDTO;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// mybatis-generator-core 라이브러리: MyBatis에서 제공하는 코드 생성기
public class MyBatisGeneratorProgrammatic {

	public static boolean execute(Shell shell, String url, String userId, String password, String targetPath) {
		try {
			List<TableDTO> tables = DatabaseConnector.getTablesInfo(url, userId, password);
			// Warnings list to capture any warnings during the generation process
			List<String> warnings = new ArrayList<>();

			// Overwrite existing files if necessary
			boolean overwrite = true;

			// Create the MyBatis Generator Configuration
			Configuration config = new Configuration();


			// ModelType.FLAT: 기본 키 분리 안함
			// ModelType.HIERARCHICAL: 기본 키 분리함 (복합 키 가 있는 경우 유용)
			// ModelType.CONDITIONAL: 복합 키가 있는 경우 HIERARCHICAL 방식, 없는 경우 FLAT 방식으로 자동 결정
			Context context = new Context(ModelType.CONDITIONAL);
			// Context (runtime: MyBatis3, MyBatis3Simple, etc.)
			context.setId("MyBatis3Context");
			// MyBatis3: Example 클래스 (동적 쿼리를 위한 도우미 클래스)도 추가
			// MyBatis3Simple: deleteByPrimaryKey, insert, selectByPrimaryKey, updateByPrimaryKey
			context.setTargetRuntime("MyBatis3"); // Use "MyBatis3Simple" if you want simpler output
			config.addContext(context);

			// JDBC Connection Configuration
			JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
			jdbcConfig.setDriverClass("com.mysql.cj.jdbc.Driver");
			jdbcConfig.setConnectionURL(url);
			jdbcConfig.setUserId(userId);
			jdbcConfig.setPassword(password);
			context.setJdbcConnectionConfiguration(jdbcConfig);

			Path javaPath = Paths.get(targetPath).resolve("src/main/java").toAbsolutePath();
			Path resources = Paths.get(targetPath).resolve("src/main/resources").toAbsolutePath();

			if (Files.notExists(javaPath)) {
				// Create the directory and all necessary parent directories
				Files.createDirectories(javaPath);
				System.out.println("Directory created at: " + javaPath.toAbsolutePath());
			}
			if (Files.notExists(resources)) {
				// Create the directory and all necessary parent directories
				Files.createDirectories(resources);
				System.out.println("Directory created at: " + resources.toAbsolutePath());
			}

			// Java Model Generator Configuration
			JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
			javaModelGeneratorConfiguration.setTargetPackage("com.example.model"); // Output package for model classes
			javaModelGeneratorConfiguration.setTargetProject(javaPath.toString()); // Output directory
			context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

			// SQL Map Generator Configuration
			SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
			sqlMapGeneratorConfiguration.setTargetPackage("mapper"); // Output package for XML files
			sqlMapGeneratorConfiguration.setTargetProject(resources.toString()); // Output directory
			context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

			String mapperPackage = "com.example.mapper";
			// Java Client Generator Configuration
			JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
			javaClientGeneratorConfiguration.setTargetPackage(mapperPackage); // Output package for mapper interfaces
			javaClientGeneratorConfiguration.setTargetProject(javaPath.toString()); // Output directory
			javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER"); // Use XML-based mapper
			context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

			for (TableDTO table : tables) {
				// Add Table Configurations
				TableConfiguration tableConfig = new TableConfiguration(context);
//				tableConfig.setTableName(table); // Table name in the database
//				tableConfig.setDomainObjectName(toCamelCase(table)); // Java entity name
//				tableConfig.setSelectByExampleStatementEnabled(false);
//				tableConfig.setDeleteByExampleStatementEnabled(false);
//				tableConfig.setCountByExampleStatementEnabled(false);
//				tableConfig.setUpdateByExampleStatementEnabled(false);
				// tableConfiguration.setGeneratedKey(new GeneratedKey("id", "JDBC", true,
				// null)); // Optional: configure primary key generation
				context.addTableConfiguration(tableConfig);
			}

			// Additional Plugins (Optional)
			PluginConfiguration toStringPlugin = new PluginConfiguration();
			toStringPlugin.setConfigurationType("org.mybatis.generator.plugins.ToStringPlugin");
			context.addPluginConfiguration(toStringPlugin);

			// Run the MyBatis Generator
			DefaultShellCallback callback = new DefaultShellCallback(overwrite);
			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
			myBatisGenerator.generate(null);

			// Print warnings, if any
			for (String warning : warnings) {
				System.out.println(warning);
			}
			MessageDialog.openInformation(shell, "Code Generated",
					"Code generated successfully at: " + targetPath + " for tables: " + tables);

			String servicePackage = "com.example.service";
			String controllerPackage = "com.example.controller";
			// table 별로 code generator를 활용해서 코드 생성
			for (TableDTO table : tables) {
//				JUnitTestGenerator.generateJunitTest(toCamelCase(table), mapperPackage, targetPath);
//				ServiceCodeGenerator.generateServiceCode(toCamelCase(table), servicePackage, targetPath);
//				ControllerCodeGenerator.generateControllerCode(toCamelCase(table), controllerPackage, targetPath);
			}
			return true;
		} catch (Exception e) {
			MessageDialog.openError(shell, "Error", e.getMessage());
			return false;
		}
	}

	public static String toCamelCase(String input) {
		// Split the input string by underscores
		String[] parts = input.split("_");

		// Initialize a StringBuilder for the result
		StringBuilder camelCaseString = new StringBuilder();

		// Iterate through the parts
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			// Capitalize the first letter of the subsequent parts
			camelCaseString.append(part.substring(0, 1).toUpperCase());
			camelCaseString.append(part.substring(1).toLowerCase());
		}

		return camelCaseString.toString();
	}
}
