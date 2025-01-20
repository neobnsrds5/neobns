package com.neobns.admin.codegen.common;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// mybatis-generator-core 라이브러리: MyBatis에서 제공하는 코드 생성기
public class MyBatisGeneratorProgrammatic {

	public static boolean execute(Shell shell, String url, String userId, String password, String targetPath,
			String tableName, String primaryKey) {
		try {
			// Warnings list to capture any warnings during the generation process
			List<String> warnings = new ArrayList<>();

			// Overwrite existing files if necessary
			boolean overwrite = true;

			// Create the MyBatis Generator Configuration
			Configuration config = new Configuration();
			
			/*
			 * Context 설정
			 */
			// ModelType.FLAT: 기본 키 분리 안함
			// ModelType.HIERARCHICAL: 기본 키 분리함 (복합 키 가 있는 경우 유용)
			// ModelType.CONDITIONAL: 복합 키가 있는 경우 HIERARCHICAL 방식, 없는 경우 FLAT 방식으로 자동 결정
			Context context = new Context(ModelType.CONDITIONAL);
			context.setId("MyBatis3Context");
			// MyBatis3: Example 클래스 (동적 쿼리를 위한 도우미 클래스)도 추가
			// MyBatis3Simple: deleteByPrimaryKey, insert, selectByPrimaryKey, updateByPrimaryKey, ...
			context.setTargetRuntime("MyBatis3"); // Use "MyBatis3Simple" if you want simpler output
			config.addContext(context);

			/*
			 * JDBC 연결 설정
			 */
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

			/*
			 * Java Model(DTO) 생성 설정
			 */
			JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();
			modelConfig.setTargetPackage("com.example.dto"); // Output package for model classes
			modelConfig.setTargetProject(javaPath.toString()); // Output directory
			context.setJavaModelGeneratorConfiguration(modelConfig);

			/*
			 * Mapper Interface 생성 설정
			 */
			JavaClientGeneratorConfiguration clientConfig = new JavaClientGeneratorConfiguration();
			clientConfig.setTargetPackage("com.example.mapper"); // Output package for mapper interfaces
			clientConfig.setTargetProject(javaPath.toString()); // Output directory
			clientConfig.setConfigurationType("XMLMAPPER"); // Use XML-based mapper
			context.setJavaClientGeneratorConfiguration(clientConfig);
			
			/*
			 * Mapper XML 생성 설정
			 */
			SqlMapGeneratorConfiguration sqlMapConfig = new SqlMapGeneratorConfiguration();
			sqlMapConfig.setTargetPackage("mappers"); // Output package for XML files
			sqlMapConfig.setTargetProject(resources.toString()); // Output directory
			context.setSqlMapGeneratorConfiguration(sqlMapConfig);

			/*
			 * 코드 생성할 테이블 설정
			 */
			TableConfiguration tableConfig = new TableConfiguration(context);
//			tableConfig.setSchema(schemaName); // Schema name in the database
			tableConfig.setTableName(tableName); // Table name in the database
			tableConfig.setDomainObjectName(toCamelCase(tableName)); // Java entity name
			// dynamic query와 관련된 코드 생성 X
			tableConfig.setSelectByExampleStatementEnabled(false);
			tableConfig.setDeleteByExampleStatementEnabled(false);
			tableConfig.setCountByExampleStatementEnabled(false);
			tableConfig.setUpdateByExampleStatementEnabled(false);
			context.addTableConfiguration(tableConfig);
			
			// Additional Plugins (Optional)
			PluginConfiguration virtualPKPlugin = new PluginConfiguration();
			virtualPKPlugin.setConfigurationType("com.neobns.admin.codegen.plugins.ForceVirtualPrimaryKeyPlugin");
			virtualPKPlugin.addProperty("primaryKeyColumns", primaryKey); // 기본 키로 사용할 컬럼 지정
			context.addPluginConfiguration(virtualPKPlugin);
			
			PluginConfiguration paginationPlugin = new PluginConfiguration();
			paginationPlugin.setConfigurationType("com.neobns.admin.codegen.plugins.PaginationPlugin");
			context.addPluginConfiguration(paginationPlugin);
			
			PluginConfiguration toStringPlugin = new PluginConfiguration();
			toStringPlugin.setConfigurationType("org.mybatis.generator.plugins.ToStringPlugin");
			context.addPluginConfiguration(toStringPlugin);

			// Run the MyBatis Generator
			DefaultShellCallback callback = new DefaultShellCallback(overwrite);
			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
			myBatisGenerator.generate(null);
			
			// Run JavaPoet Generator
			JUnitTestGenerator.generateJunitTest(toCamelCase(tableName), "com.example.mapper", targetPath);
			ServiceCodeGenerator.generateServiceCode(toCamelCase(tableName), "com.example.service", targetPath);
			ControllerCodeGenerator.generateControllerCode(toCamelCase(tableName), "com.example.controller", targetPath);

			// Print warnings, if any
			for (String warning : warnings) {
				System.out.println(warning);
			}
			
			MessageDialog.openInformation(shell, "Code Generated", "Code generated successfully at: " + targetPath + " for table: " + tableName);

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
