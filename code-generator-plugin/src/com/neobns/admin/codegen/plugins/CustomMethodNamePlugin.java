package com.neobns.admin.codegen.plugins;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.xml.XmlElement;

public class CustomMethodNamePlugin extends PluginAdapter {

	@Override
	public boolean validate(List<String> warnings) {
		return true; // 플러그인 유효성 검증
	}

	@Override
	public boolean clientInsertMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		// insert 메소드를 생성하지 않음
		return !method.getName().equals("insert");
	}

	@Override
	public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
		// insert 문을 생성하지 않음
		return false;
	}
}
