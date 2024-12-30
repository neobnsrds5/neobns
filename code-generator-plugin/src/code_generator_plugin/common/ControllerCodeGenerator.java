package code_generator_plugin.common;

import com.squareup.javapoet.*;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Paths;

//JavaPoet 라이브러리: Java 코드를 프로그래밍 방식으로 생성
public class ControllerCodeGenerator {

    public static void generateControllerCode(String className, String packageName, String targetPath) throws IOException {
        // 클래스 이름
        String controllerName = className + "Controller";
        String serviceName = className + "Service";
        String modelName = className;
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
                        .addMember("value", "$S", "/{id}")
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
                .addAnnotation(ClassName.get("org.springframework.web.bind.annotation", "PostMapping"))
                .addParameter(ParameterSpec.builder(ClassName.get("com.example.model", modelName), "dto")
                        .addAnnotation(ClassName.get("org.springframework.web.bind.annotation", "RequestBody"))
                        .build())
                .addStatement("service.insert(dto)")
                .build();

        // insertSelective 메서드 생성
        MethodSpec insertSelectiveMethod = MethodSpec.methodBuilder("insertSelective")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(ClassName.get("org.springframework.web.bind.annotation", "PostMapping"))
                .addParameter(ParameterSpec.builder(ClassName.get("com.example.model", modelName), "dto")
                        .addAnnotation(ClassName.get("org.springframework.web.bind.annotation", "RequestBody"))
                        .build())
                .addStatement("service.insertSelective(dto)")
                .build();

        // findById 메서드 생성
        MethodSpec findByIdMethod = MethodSpec.methodBuilder("findById")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("com.example.model", modelName))
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
                        .addMember("value", "$S", "/{id}")
                        .build())
                .addParameter(ParameterSpec.builder(Long.class, "id")
                        .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "PathVariable"))
                                .addMember("value", "$S", "id")
                                .build())
                        .build())
                .addParameter(ParameterSpec.builder(ClassName.get("com.example.model", modelName), "dto")
                        .addAnnotation(ClassName.get("org.springframework.web.bind.annotation", "RequestBody"))
                        .build())
                .addStatement("service.updateSelective(dto)")
                .build();

        // update 메서드 생성
        MethodSpec updateMethod = MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "PutMapping"))
                        .addMember("value", "$S", "/{id}")
                        .build())
                .addParameter(ParameterSpec.builder(Long.class, "id")
                        .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "PathVariable"))
                                .addMember("value", "$S", "id")
                                .build())
                        .build())
                .addParameter(ParameterSpec.builder(ClassName.get("com.example.model", modelName), "dto")
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
