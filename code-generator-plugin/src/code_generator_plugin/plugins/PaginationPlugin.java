package code_generator_plugin.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;

public class PaginationPlugin extends PluginAdapter {

	@Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
    	List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
		if (primaryKeyColumns.isEmpty()) {
			throw new IllegalStateException("No primary key column found.");
		}

		IntrospectedColumn primaryKeyColumn = primaryKeyColumns.get(0); // 첫 번째 기본 키 사용
		String columnName = primaryKeyColumn.getActualColumnName();
    	
        XmlElement selectElement = new XmlElement("select");
        selectElement.addAttribute(new Attribute("id", "selectByPrimaryKeyWithPaging"));
        selectElement.addAttribute(new Attribute("resultType", introspectedTable.getBaseRecordType()));
        
        

        selectElement.addElement(new TextElement(
            "SELECT * FROM " + introspectedTable.getFullyQualifiedTableNameAtRuntime() +
            " WHERE " + columnName + " = #{" + columnName + "}" +
            " ORDER BY #{" + columnName + "} LIMIT #{offset}, #{limit}"
        ));

        document.getRootElement().addElement(selectElement);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
		List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
		if (primaryKeyColumns.isEmpty()) {
			throw new IllegalStateException("No primary key column found.");
		}

		IntrospectedColumn primaryKeyColumn = primaryKeyColumns.get(0); // 첫 번째 기본 키 사용

		// 기본 키를 기준으로 메서드 생성
		Method method = new Method("selectByPrimaryKeyWithPaging");
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(new FullyQualifiedJavaType("List<" + introspectedTable.getBaseRecordType() + ">"));
		method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"offset\") int"), "offset"));
		method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"limit\") int"), "limit"));
		method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"" + primaryKeyColumn.getJavaProperty()
				+ "\") " + primaryKeyColumn.getFullyQualifiedJavaType()), primaryKeyColumn.getJavaProperty()));

        interfaze.addMethod(method);

        return super.clientGenerated(interfaze, introspectedTable);
    }

}
