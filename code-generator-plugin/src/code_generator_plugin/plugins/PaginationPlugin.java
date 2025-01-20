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
    	FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
    	
    	/*
    	 * find
    	 */
        XmlElement findElement = new XmlElement("select");
        findElement.addAttribute(new Attribute("id", "find" + baseRecordType.getShortName()));
        findElement.addAttribute(new Attribute("resultType", introspectedTable.getBaseRecordType()));
        // SQL 작성
        findElement.addElement(new TextElement("SELECT * FROM " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        findElement.addElement(new TextElement("WHERE 1=1"));
        // 모델 객체의 멤버 변수 조건 추가
        introspectedTable.getAllColumns().forEach(column -> {
            String fieldName = column.getJavaProperty();
            findElement.addElement(new TextElement(
                    "<if test=\"row." + fieldName + " != null\"> AND " + column.getActualColumnName() + " = #{row." + fieldName + "} </if>"
            ));
        });
        // 정렬 처리
        IntrospectedColumn primaryKey = introspectedTable.getPrimaryKeyColumns().get(0); // 첫번째 PK로 정렬
        findElement.addElement(new TextElement("ORDER BY " + primaryKey.getActualColumnName()));
        // 페이징 처리
        findElement.addElement(new TextElement("LIMIT #{offset}, #{limit}"));
        
        /*
         * count
         */
        XmlElement countElement = new XmlElement("select");
        countElement.addAttribute(new Attribute("id", "count" + baseRecordType.getShortName()));
        countElement.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        // SQL 작성
        countElement.addElement(new TextElement("SELECT * FROM " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        countElement.addElement(new TextElement("WHERE 1=1"));
        // 모델 객체의 멤버 변수 조건 추가
        introspectedTable.getAllColumns().forEach(column -> {
            String fieldName = column.getJavaProperty();
            countElement.addElement(new TextElement(
                    "<if test=\"" + fieldName + " != null\"> AND " + column.getActualColumnName() + " = #{" + fieldName + "} </if>"
            ));
        });
        
        document.getRootElement().addElement(findElement);
        document.getRootElement().addElement(countElement);
        
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
    	FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
    	
		Method findMethod = new Method("find" + baseRecordType.getShortName());
		findMethod.setVisibility(JavaVisibility.PUBLIC);
		findMethod.setAbstract(true);
		findMethod.setReturnType(new FullyQualifiedJavaType("List<" + introspectedTable.getBaseRecordType() + ">"));
		findMethod.addParameter(createAnnotatedParameter("@Param(\"offset\")", FullyQualifiedJavaType.getIntInstance(), "offset"));
	    findMethod.addParameter(createAnnotatedParameter("@Param(\"limit\")", FullyQualifiedJavaType.getIntInstance(), "limit"));
	    findMethod.addParameter(createAnnotatedParameter("@Param(\"row\")", baseRecordType, "row"));
	    
	    Method countMethod = new Method("count" + baseRecordType.getShortName());
	    countMethod.setVisibility(JavaVisibility.PUBLIC);
	    countMethod.setAbstract(true);
	    countMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
	    countMethod.addParameter(new Parameter(baseRecordType, "row"));

        interfaze.addMethod(findMethod);
        interfaze.addMethod(countMethod);

        return super.clientGenerated(interfaze, introspectedTable);
    }
    
    /**
     * 어노테이션이 포함된 매개변수를 생성
     */
    private Parameter createAnnotatedParameter(String annotation, FullyQualifiedJavaType type, String name) {
        Parameter parameter = new Parameter(type, name);
        parameter.addAnnotation(annotation); // 어노테이션 추가
        return parameter;
    }

}
