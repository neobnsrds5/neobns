package neo.spider.solution.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FwkErrorHisDto {
	
	private String errorCode;   
    private String errorSerNo;     
    private String custUserId;         
    private String errorMessage;        
    private String errorOccurDtime;     
    private String errorUrl;            
    private String errorTrace;          
    private String errorInstanceId;     

}
