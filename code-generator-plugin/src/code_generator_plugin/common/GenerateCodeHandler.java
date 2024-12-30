package code_generator_plugin.common;

import java.io.FileWriter;
import java.io.IOException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class GenerateCodeHandler {

    public static String execute(Shell shell,String name, String path) {

        // 파일 생성 경로 (예제: 워크스페이스의 특정 폴더에 파일 생성)
        String filePath = path + "/"+ name + ".java";

        // 코드 템플릿
        String codeTemplate = """
                public class %s {
                    public static void main(String[] args) {
                        System.out.println("Hello, Generated World!");
                    }
                }
                """.formatted(name);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(codeTemplate);
            System.out.println("Code generated successfully at: " + filePath);
            MessageDialog.openInformation(shell, "Code Generated", "Code generated successfully at: " + filePath);
        } catch (IOException e) {
            MessageDialog.openError(shell, "Error", "Failed to generate code: " + e.getMessage());
        }

        return null;
    }
}
