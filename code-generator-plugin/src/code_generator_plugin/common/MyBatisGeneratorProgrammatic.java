package code_generator_plugin.common;

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

	public static boolean execute(Shell shell, String url, String userId, String password, String targetPath) {
		try {
			List<String> tables = DatabaseConnector.getTables(url, userId, password);
			// Warnings list to capture any warnings during the generation process
			List<String> warnings = new ArrayList<>();

			// Overwrite existing files if necessary
			boolean overwrite = true;

			// Create the MyBatis Generator Configuration
			Configuration config = new Configuration();

			// Context (runtime: MyBatis3, MyBatis3Simple, etc.)
			Context context = new Context(ModelType.CONDITIONAL);
			context.setId("MyBatis3Context");
			context.setTargetRuntime("MyBatis3"); // Use "MyBatis3Simple" if you want simpler output
			config.addContext(context);

			// JDBC Connection Configuration
			JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
			jdbcConnectionConfiguration.setDriverClass("com.mysql.cj.jdbc.Driver");
			jdbcConnectionConfiguration.setConnectionURL(url);
			jdbcConnectionConfiguration.setUserId(userId);
			jdbcConnectionConfiguration.setPassword(password);
			context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

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
			sqlMapGeneratorConfiguration.setTargetPackage("com.example.mapper"); // Output package for XML files
			sqlMapGeneratorConfiguration.setTargetProject(resources.toString()); // Output directory
			context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

			String mapperPackage = "com.example.mapper";
			// Java Client Generator Configuration
			JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
			javaClientGeneratorConfiguration.setTargetPackage(mapperPackage); // Output package for mapper interfaces
			javaClientGeneratorConfiguration.setTargetProject(javaPath.toString()); // Output directory
			javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER"); // Use XML-based mapper
			context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

			for (String table : tables) {
				// Add Table Configurations
				TableConfiguration tableConfiguration = new TableConfiguration(context);
				tableConfiguration.setTableName(table); // Table name in the database
				tableConfiguration.setDomainObjectName(toCamelCase(table)); // Java entity name
				tableConfiguration.setSelectByExampleStatementEnabled(false);
				tableConfiguration.setDeleteByExampleStatementEnabled(false);
				tableConfiguration.setCountByExampleStatementEnabled(false);
				tableConfiguration.setUpdateByExampleStatementEnabled(false);
				// tableConfiguration.setGeneratedKey(new GeneratedKey("id", "JDBC", true,
				// null)); // Optional: configure primary key generation
				context.addTableConfiguration(tableConfiguration);
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
			String contorllerPackage = "com.example.contorller";
			// table 별로 code generator를 활용해서 코드 생성
			for (String table : tables) {
				JUnitTestGenerator.generateJunitTest(toCamelCase(table), mapperPackage, targetPath);
				ServiceCodeGenerator.generateServiceCode(toCamelCase(table), servicePackage, targetPath);
				ControllerCodeGenerator.generateControllerCode(toCamelCase(table), contorllerPackage, targetPath);
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
