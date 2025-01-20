package com.neobns.admin.codegen.common;

import com.squareup.javapoet.*;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Paths;

//JavaPoet 라이브러리: Java 코드를 프로그래밍 방식으로 생성
public class ServiceCodeGenerator {

    public static void generateServiceCode(String className, String packageName, String targetPath) throws IOException {
        // 클래스 이름
        String serviceName = className + "Service";
        String mapperName = className + "Mapper";
        String modelName = className;

        // 필드 생성
        FieldSpec mapperField = FieldSpec.builder(ClassName.get("com.example.mapper", mapperName), "mapper")
                .addAnnotation(ClassName.get("org.springframework.beans.factory.annotation", "Autowired"))
                .addModifiers(Modifier.PRIVATE)
                .build();

        // deleteByPrimaryKey 메서드 생성
        MethodSpec deleteByPrimaryKeyMethod = MethodSpec.methodBuilder("deleteById")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Long.class, "id")
                .addStatement("mapper.deleteByPrimaryKey(id)")
                .build();

        // insert 메서드 생성
        MethodSpec insertMethod = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(ClassName.get("com.example.model", modelName), "entity")
                .addStatement("mapper.insert(entity)")
                .build();

        // insertSelective 메서드 생성
        MethodSpec insertSelectiveMethod = MethodSpec.methodBuilder("insertSelective")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(ClassName.get("com.example.model", modelName), "entity")
                .addStatement("mapper.insertSelective(entity)")
                .build();

        // selectByPrimaryKey 메서드 생성
        MethodSpec selectByPrimaryKeyMethod = MethodSpec.methodBuilder("findById")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("com.example.model", modelName))
                .addParameter(Long.class, "id")
                .addStatement("return mapper.selectByPrimaryKey(id)")
                .build();

        // updateByPrimaryKeySelective 메서드 생성
        MethodSpec updateByPrimaryKeySelectiveMethod = MethodSpec.methodBuilder("updateSelective")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(ClassName.get("com.example.model", modelName), "entity")
                .addStatement("mapper.updateByPrimaryKeySelective(entity)")
                .build();

        // updateByPrimaryKey 메서드 생성
        MethodSpec updateByPrimaryKeyMethod = MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(ClassName.get("com.example.model", modelName), "entity")
                .addStatement("mapper.updateByPrimaryKey(entity)")
                .build();

        // 클래스 생성
        TypeSpec serviceClass = TypeSpec.classBuilder(serviceName)
                .addAnnotation(ClassName.get("org.springframework.stereotype", "Service"))
                .addModifiers(Modifier.PUBLIC)
                .addField(mapperField)
                .addMethod(deleteByPrimaryKeyMethod)
                .addMethod(insertMethod)
                .addMethod(insertSelectiveMethod)
                .addMethod(selectByPrimaryKeyMethod)
                .addMethod(updateByPrimaryKeySelectiveMethod)
                .addMethod(updateByPrimaryKeyMethod)
                .build();

        // Java 파일 생성
        JavaFile javaFile = JavaFile.builder(packageName, serviceClass)
                .build();

        // 파일 저장
        javaFile.writeTo(Paths.get(targetPath + "/src/main/java"));
        System.out.println("Service class generated successfully!");
    }
}
