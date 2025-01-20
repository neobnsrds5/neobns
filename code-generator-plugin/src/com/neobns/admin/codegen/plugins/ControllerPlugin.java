package com.neobns.admin.codegen.plugins;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

public class ControllerPlugin extends PluginAdapter {

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}
	
	

	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
		// TODO Auto-generated method stub
		return super.contextGenerateAdditionalJavaFiles(introspectedTable);
	}



	public static void generateControllerCode(IntrospectedTable introspectedTable, String className, String packageName, String targetPath) throws IOException {
        // 클래스 이름
        String controllerName = className + "Controller";
        String serviceName = className + "Service";
        String dtoName = className;
        String requestMapping = "/" + className.toLowerCase();

        // 필드 생성
        FieldSpec serviceField = FieldSpec.builder(ClassName.get("com.example.service", serviceName), "service")
                .addAnnotation(ClassName.get("org.springframework.beans.factory.annotation", "Autowired"))
                .addModifiers(Modifier.PRIVATE)
                .build();

        // deleteById 메서드 생성
        MethodSpec deleteByIdMethod = MethodSpec.methodBuilder("deleteById")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "DeleteMapping"))
                        .addMember("value", "$S", "/delete/{id}")
                        .build())
                .addParameter(ParameterSpec.builder(Long.class, "id")
                        .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "PathVariable"))
                                .addMember("value", "$S", "id")
                                .build())
                        .build())
                .addStatement("service.deleteById(id)")
                .build();

        // insert 메서드 생성
        MethodSpec insertMethod = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "PostMapping"))
		                .addMember("value", "$S", "/insert")
		                .build())
                .addParameter(ParameterSpec.builder(ClassName.get("com.example.dto", dtoName), "dto")
                        .addAnnotation(ClassName.get("org.springframework.web.bind.annotation", "RequestBody"))
                        .build())
                .addStatement("service.insert(dto)")
                .build();

        // insertSelective 메서드 생성
        MethodSpec insertSelectiveMethod = MethodSpec.methodBuilder("insertSelective")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "PostMapping"))
                		.addMember("value", "$S", "/insert-selective")
		                .build())
                .addParameter(ParameterSpec.builder(ClassName.get("com.example.dto", dtoName), "dto")
                        .addAnnotation(ClassName.get("org.springframework.web.bind.annotation", "RequestBody"))
                        .build())
                .addStatement("service.insertSelective(dto)")
                .build();

        // findById 메서드 생성
        MethodSpec findByIdMethod = MethodSpec.methodBuilder("findById")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("com.example.dto", dtoName))
                .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "GetMapping"))
                        .addMember("value", "$S", "/{id}")
                        .build())
                .addParameter(ParameterSpec.builder(Long.class, "id")
                        .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "PathVariable"))
                                .addMember("value", "$S", "id")
                                .build())
                        .build())
                .addStatement("return service.findById(id)")
                .build();

        // updateSelective 메서드 생성
        MethodSpec updateSelectiveMethod = MethodSpec.methodBuilder("updateSelective")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "PutMapping"))
                        .addMember("value", "$S", "/update-selective/{id}")
                        .build())
                .addParameter(ParameterSpec.builder(Long.class, "id")
                        .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "PathVariable"))
                                .addMember("value", "$S", "id")
                                .build())
                        .build())
                .addParameter(ParameterSpec.builder(ClassName.get("com.example.dto", dtoName), "dto")
                        .addAnnotation(ClassName.get("org.springframework.web.bind.annotation", "RequestBody"))
                        .build())
                .addStatement("service.updateSelective(dto)")
                .build();

        // update 메서드 생성
        MethodSpec updateMethod = MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "PutMapping"))
                        .addMember("value", "$S", "/update/{id}")
                        .build())
                .addParameter(ParameterSpec.builder(Long.class, "id")
                        .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "PathVariable"))
                                .addMember("value", "$S", "id")
                                .build())
                        .build())
                .addParameter(ParameterSpec.builder(ClassName.get("com.example.dto", dtoName), "dto")
                        .addAnnotation(ClassName.get("org.springframework.web.bind.annotation", "RequestBody"))
                        .build())
                .addStatement("service.update(dto)")
                .build();

        // 클래스 생성
        TypeSpec controllerClass = TypeSpec.classBuilder(controllerName)
                .addAnnotation(ClassName.get("org.springframework.web.bind.annotation", "RestController"))
                .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "RequestMapping"))
                        .addMember("value", "$S", requestMapping)
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .addField(serviceField)
                .addMethod(deleteByIdMethod)
                .addMethod(insertMethod)
                .addMethod(insertSelectiveMethod)
                .addMethod(findByIdMethod)
                .addMethod(updateSelectiveMethod)
                .addMethod(updateMethod)
                .build();

        // Java 파일 생성
        JavaFile javaFile = JavaFile.builder(packageName, controllerClass)
                .build();

        // 파일 저장
        javaFile.writeTo(Paths.get(targetPath + "/src/main/java"));
        System.out.println("Controller class generated successfully!");
    }
}
