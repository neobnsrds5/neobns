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
		// 플러그인이 유효한지 확인
        String primaryKeyColumn = properties.getProperty("primaryKeyColumn");
        if (primaryKeyColumn == null || primaryKeyColumn.trim().isEmpty()) {
            warnings.add("PaginationPlugin: The 'primaryKeyColumn' property must be specified.");
            return false;
        }
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement selectElement = new XmlElement("select");
        selectElement.addAttribute(new Attribute("id", "selectByPrimaryKeyWithPaging"));
        selectElement.addAttribute(new Attribute("resultType", introspectedTable.getBaseRecordType()));
        
        // 설정에서 지정한 가상 기본 키 컬럼
        IntrospectedColumn column = introspectedTable.getColumn(properties.getProperty("primaryKeyColumn").trim()).orElse(null);
        String primaryKeyColumn = column.getActualColumnName();

        selectElement.addElement(new TextElement(
            "SELECT * FROM " + introspectedTable.getFullyQualifiedTableNameAtRuntime() +
            " WHERE " + primaryKeyColumn + " = #{" + primaryKeyColumn + "}" +
            " ORDER BY #{" + primaryKeyColumn + "} LIMIT #{offset}, #{limit}"
        ));

        document.getRootElement().addElement(selectElement);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
    	Method method = new Method("selectByPrimaryKeyWithPaging");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("List<" + introspectedTable.getBaseRecordType() + ">"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"offset\") int"), "offset"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"limit\") int"), "limit"));
        
        // 설정에서 지정한 가상 기본 키 컬럼을 가져와서 parameter 설정
        String primaryKeyColumn = properties.getProperty("primaryKeyColumn");
        IntrospectedColumn column = introspectedTable.getColumn(primaryKeyColumn.trim()).orElse(null);
        if (column != null) {
        	method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"" + column.getActualColumnName() + "\") " + column.getFullyQualifiedJavaType()), column.getActualColumnName()));
        }
        
        interfaze.addMethod(method);

        return super.clientGenerated(interfaze, introspectedTable);
    }

}
