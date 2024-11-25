package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;

public class JMeterTestPlanCodeGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema schema) throws IOException {
        String baseUrl = "localhost:8080";  // Adjust this to your actual base URL
        String testPlan = createJMeterTestPlan(packageName, resourceName, baseUrl);
        
        // Write the generated test plan to the file
        writeToFile(packageDir + resourceName + "TestPlan.jmx", testPlan);
    }

    private String createJMeterTestPlan(String packageName, String resourceName, String baseUrl) {
        String resourceNameLower = resourceName.toLowerCase();

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
          .append("<jmeterTestPlan version=\"1.2\" properties=\"5.0\" jmeter=\"5.6.3\">\n")
          .append("  <hashTree>\n")
          .append("    <TestPlan guiclass=\"TestPlanGui\" testclass=\"TestPlan\" testname=\"").append(resourceName).append(" API Test Plan\" enabled=\"true\">\n")
          .append("      <stringProp name=\"TestPlan.comments\"></stringProp>\n")
          .append("      <boolProp name=\"TestPlan.functional_mode\">false</boolProp>\n")
          .append("      <boolProp name=\"TestPlan.tearDown_on_shutdown\">true</boolProp>\n")
          .append("      <boolProp name=\"TestPlan.serialize_threadgroups\">false</boolProp>\n")
          .append("      <elementProp name=\"TestPlan.user_defined_variables\" elementType=\"Arguments\" guiclass=\"ArgumentsPanel\" testclass=\"Arguments\" testname=\"User Defined Variables\" enabled=\"true\">\n")
          .append("        <collectionProp name=\"Arguments.arguments\"/>\n")
          .append("      </elementProp>\n")
          .append("    </TestPlan>\n")
          .append("    <hashTree>\n")
          .append("      <ThreadGroup guiclass=\"ThreadGroupGui\" testclass=\"ThreadGroup\" testname=\"").append(resourceName).append(" API Thread Group\">\n")
          .append("        <intProp name=\"ThreadGroup.num_threads\">2</intProp>\n")
          .append("        <intProp name=\"ThreadGroup.ramp_time\">1</intProp>\n")
          .append("        <boolProp name=\"ThreadGroup.same_user_on_next_iteration\">true</boolProp>\n")
          .append("        <stringProp name=\"ThreadGroup.on_sample_error\">continue</stringProp>\n")
          .append("        <elementProp name=\"ThreadGroup.main_controller\" elementType=\"LoopController\" guiclass=\"LoopControlPanel\" testclass=\"LoopController\" testname=\"Loop Controller\">\n")
          .append("          <stringProp name=\"LoopController.loops\">1</stringProp>\n")
          .append("          <boolProp name=\"LoopController.continue_forever\">false</boolProp>\n")
          .append("        </elementProp>\n")
          .append("      </ThreadGroup>\n")
          .append("      <hashTree>\n")
          // HTTP Request Defaults
          .append("        <ConfigTestElement guiclass=\"HttpDefaultsGui\" testclass=\"ConfigTestElement\" testname=\"HTTP Request Defaults\" enabled=\"true\">\n")
          .append("          <stringProp name=\"HTTPSampler.domain\">localhost</stringProp>\n")
          .append("          <stringProp name=\"HTTPSampler.port\">8080</stringProp>\n")
          .append("          <stringProp name=\"HTTPSampler.protocol\">http</stringProp>\n")
          .append("          <stringProp name=\"HTTPSampler.contentEncoding\">UTF-8</stringProp>\n")
          .append("          <elementProp name=\"HTTPsampler.Arguments\" elementType=\"Arguments\" guiclass=\"HTTPArgumentsPanel\" testclass=\"Arguments\" testname=\"User Defined Variables\">\n")
          .append("            <collectionProp name=\"Arguments.arguments\"/>\n")
          .append("          </elementProp>\n")
          .append("        </ConfigTestElement>\n")
          .append("        <hashTree/>\n")
          // User Defined Variables
          .append("        <Arguments guiclass=\"ArgumentsPanel\" testclass=\"Arguments\" testname=\"User Defined Variables\" enabled=\"true\">\n")
          .append("          <collectionProp name=\"Arguments.arguments\">\n")
          .append("            <elementProp name=\"id\" elementType=\"Argument\">\n")
          .append("              <stringProp name=\"Argument.name\">id</stringProp>\n")
          .append("              <stringProp name=\"Argument.value\">1</stringProp>\n")
          .append("              <stringProp name=\"Argument.metadata\">=</stringProp>\n")
          .append("            </elementProp>\n")
          .append("          </collectionProp>\n")
          .append("        </Arguments>\n")
          .append("        <hashTree/>\n")
          // GET All Resources
          .append(createHttpSampler("GET All " + resourceName + "s", "/" + resourceNameLower, "GET", null))
          // GET Resource by ID
          .append(createHttpSampler("GET " + resourceName + " by ID", "/" + resourceNameLower + "/${id}", "GET", null))
          // POST Create Resource
          .append(createHttpSampler("POST Create " + resourceName, "/" + resourceNameLower, "POST", 
                 "{\"name\": \"Test " + resourceName + "\", \"email\": \"test@example.com\", \"phone\": \"123-456-7890\"}"))
          // PUT Update Resource
          .append(createHttpSampler("PUT Update " + resourceName, "/" + resourceNameLower + "/${id}", "PUT",
                 "{\"name\": \"Updated " + resourceName + "\", \"email\": \"updated@example.com\", \"phone\": \"987-654-3210\"}"))
          // DELETE Resource
          .append(createHttpSampler("DELETE " + resourceName, "/" + resourceNameLower + "/${id}", "DELETE", null))
          // View Results Tree
          .append(createResultCollector("ViewResultsFullVisualizer", "View Results Tree"))
          // Aggregate Report
          .append(createResultCollector("StatVisualizer", "Aggregate Report"))
          .append("      </hashTree>\n")
          .append("    </hashTree>\n")
          .append("  </hashTree>\n")
          .append("</jmeterTestPlan>");

        return sb.toString();
    }

    private String createHttpSampler(String testName, String path, String method, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("        <HTTPSamplerProxy guiclass=\"HttpTestSampleGui\" testclass=\"HTTPSamplerProxy\" testname=\"").append(testName).append("\" enabled=\"true\">\n")
          .append("          <stringProp name=\"HTTPSampler.path\">").append(path).append("</stringProp>\n")
          .append("          <boolProp name=\"HTTPSampler.follow_redirects\">true</boolProp>\n")
          .append("          <stringProp name=\"HTTPSampler.method\">").append(method).append("</stringProp>\n")
          .append("          <boolProp name=\"HTTPSampler.use_keepalive\">true</boolProp>\n");

        if (body != null) {
            sb.append("          <boolProp name=\"HTTPSampler.postBodyRaw\">true</boolProp>\n")
              .append("          <elementProp name=\"HTTPsampler.Arguments\" elementType=\"Arguments\">\n")
              .append("            <collectionProp name=\"Arguments.arguments\">\n")
              .append("              <elementProp name=\"\" elementType=\"HTTPArgument\">\n")
              .append("                <boolProp name=\"HTTPArgument.always_encode\">false</boolProp>\n")
              .append("                <stringProp name=\"Argument.value\">").append(body).append("</stringProp>\n")
              .append("                <stringProp name=\"Argument.metadata\">=</stringProp>\n")
              .append("              </elementProp>\n")
              .append("            </collectionProp>\n")
              .append("          </elementProp>\n");
        } else {
            sb.append("          <boolProp name=\"HTTPSampler.postBodyRaw\">false</boolProp>\n")
              .append("          <elementProp name=\"HTTPsampler.Arguments\" elementType=\"Arguments\" guiclass=\"HTTPArgumentsPanel\" testclass=\"Arguments\" testname=\"User Defined Variables\">\n")
              .append("            <collectionProp name=\"Arguments.arguments\"/>\n")
              .append("          </elementProp>\n");
        }

        sb.append("        </HTTPSamplerProxy>\n")
          .append("        <hashTree>\n")
          .append("          <HeaderManager guiclass=\"HeaderPanel\" testclass=\"HeaderManager\" testname=\"HTTP Header Manager\" enabled=\"true\">\n")
          .append("            <collectionProp name=\"HeaderManager.headers\">\n");
        
        if (body != null) {
            sb.append("              <elementProp name=\"\" elementType=\"Header\">\n")
              .append("                <stringProp name=\"Header.name\">Content-Type</stringProp>\n")
              .append("                <stringProp name=\"Header.value\">application/json</stringProp>\n")
              .append("              </elementProp>\n");
        }
        
        sb.append("              <elementProp name=\"\" elementType=\"Header\">\n")
          .append("                <stringProp name=\"Header.name\">Accept</stringProp>\n")
          .append("                <stringProp name=\"Header.value\">application/json</stringProp>\n")
          .append("              </elementProp>\n")
          .append("            </collectionProp>\n")
          .append("          </HeaderManager>\n")
          .append("          <hashTree/>\n")
          .append("        </hashTree>\n");

        return sb.toString();
    }

    private String createResultCollector(String guiclass, String testname) {
        return "        <ResultCollector guiclass=\"" + guiclass + "\" testclass=\"ResultCollector\" testname=\"" + testname + "\" enabled=\"true\">\n" +
               "          <boolProp name=\"ResultCollector.error_logging\">false</boolProp>\n" +
               "          <objProp>\n" +
               "            <name>saveConfig</name>\n" +
               "            <value class=\"SampleSaveConfiguration\">\n" +
               "              <time>true</time>\n" +
               "              <latency>true</latency>\n" +
               "              <timestamp>true</timestamp>\n" +
               "              <success>true</success>\n" +
               "              <label>true</label>\n" +
               "              <code>true</code>\n" +
               "              <message>true</message>\n" +
               "              <threadName>true</threadName>\n" +
               "              <dataType>true</dataType>\n" +
               "              <encoding>false</encoding>\n" +
               "              <assertions>true</assertions>\n" +
               "              <subresults>true</subresults>\n" +
               "              <responseData>false</responseData>\n" +
               "              <samplerData>false</samplerData>\n" +
               "              <xml>false</xml>\n" +
               "              <fieldNames>true</fieldNames>\n" +
               "              <responseHeaders>false</responseHeaders>\n" +
               "              <requestHeaders>false</requestHeaders>\n" +
               "              <responseDataOnError>false</responseDataOnError>\n" +
               "              <saveAssertionResultsFailureMessage>true</saveAssertionResultsFailureMessage>\n" +
               "              <assertionsResultsToSave>0</assertionsResultsToSave>\n" +
               "              <bytes>true</bytes>\n" +
               "              <sentBytes>true</sentBytes>\n" +
               "              <url>true</url>\n" +
               "              <threadCounts>true</threadCounts>\n" +
               "              <sampleCount>true</sampleCount>\n" +
               "              <idleTime>true</idleTime>\n" +
               "              <connectTime>true</connectTime>\n" +
               "            </value>\n" +
               "          </objProp>\n" +
               "          <stringProp name=\"filename\"></stringProp>\n" +
               "        </ResultCollector>\n" +
               "        <hashTree/>\n";
    }

    public void writeToFile(String filePath, String content) throws IOException {
        java.nio.file.Files.write(java.nio.file.Paths.get(filePath), content.getBytes());
    }
}