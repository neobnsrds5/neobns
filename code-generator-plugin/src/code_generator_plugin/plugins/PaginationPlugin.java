package code_generator_plugin.plugins;

import java.util.List;

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
        XmlElement selectElement = new XmlElement("select");
        selectElement.addAttribute(new Attribute("id", "selectWithPaging"));
        selectElement.addAttribute(new Attribute("parameterType", "map"));
        selectElement.addAttribute(new Attribute("resultType", introspectedTable.getBaseRecordType()));

        selectElement.addElement(new TextElement(
            "SELECT * FROM " + introspectedTable.getFullyQualifiedTableNameAtRuntime() +
            " ORDER BY ${orderBy} LIMIT #{offset}, #{limit}"
        ));

        document.getRootElement().addElement(selectElement);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        Method method = new Method("selectWithPaging");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("java.util.List<" + introspectedTable.getBaseRecordType() + ">"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("java.util.Map<String, Object>"), "params"));
        interfaze.addMethod(method);

        return super.clientGenerated(interfaze, introspectedTable);
    }

}
