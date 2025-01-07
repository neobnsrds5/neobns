import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { SystemHealth } from '../interface/system-health';
import { SystemCpu } from '../interface/system-cpu';

@Injectable({
  providedIn: 'root'
})

export class DashboardService {

  private SERVER_URL: any = environment.serverUrl;

  constructor(private http: HttpClient) { }

  public getHttpTraces(): Observable<any>{
    
    return this.http.get<any>(`${this.SERVER_URL}/httpexchanges`);

  }

  public getSystemHealth(): Observable<SystemHealth>{
    
    return this.http.get<SystemHealth>(`${this.SERVER_URL}/health`);

  }

  public getSystemCpu(): Observable<SystemCpu>{
    
    return this.http.get<SystemCpu>(`${this.SERVER_URL}/metrics/system.cpu.count`);

  }

  
  public getProcessUptime(): Observable<any>{
    
    return this.http.get<any>(`${this.SERVER_URL}/metrics/process.uptime`);

  }
}
