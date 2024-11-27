import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { SystemHealth } from './interface/system-health';
import { SystemCpu } from './interface/system-cpu';
import { DashboardService } from './service/dashboard.service';
import { HttpErrorResponse } from '@angular/common/http';
import { time } from 'console';

// console.log 전부 제거할 것
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  public traceList: any[] = [];
  public selectedTrace: any;
  public systemHealth: SystemHealth;
  public systemCpu: SystemCpu;
  public processUpTime: string;
  public http200Traces: any[] = [];
  public http400Traces: any[] = [];
  public http404Traces: any[] = [];
  public http500Traces: any[] = [];
  public httpDefaultTraces: any[] = [];
  public timestamp: number;
  formattedFree: string;

  constructor(private dashboardService: DashboardService) { }

  ngOnInit(): void {
    this.getTraces();
    this.getCpuUsage();
    this.getSystemHealth();
    // this.getProcessUptime(true);
  }


  private getTraces(): void {
    this.dashboardService.getHttpTraces().subscribe(
      (response: any) => {
        this.processTraces(response.exchanges);
      },

      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  private getCpuUsage(): void {
    this.dashboardService.getSystemCpu().subscribe(
      (response: SystemCpu) => {
        this.systemCpu = response;
      },

      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  private getSystemHealth(): void {
    this.dashboardService.getSystemHealth().subscribe(
      (response: SystemHealth) => {
        console.log(response);
        this.systemHealth = response;
        this.formattedFree = this.formatBytes(this.systemHealth?.components?.diskSpace?.details?.free);
      },

      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  public onRefreshData() : void{
    this.getTraces();
    this.getCpuUsage();
    this.getSystemHealth();
  }

  private getProcessUptime(isUpdateTime: boolean): void {
    this.dashboardService.getProcessUptime().subscribe(
      (response: any) => {
        this.timestamp = Math.round(response.measurements[0].value);
        this.processUpTime = this.formatUptime(this.timestamp);
        // if(isUpdateTime){
        //   this.updateTime();
        // }
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }
  
  formatUptime(timestamp: number): string {
    const hours = Math.floor(timestamp / 60 / 60);
    const minutes = Math.floor(timestamp / 60) - (hours * 60);
    const seconds = timestamp % 60;
    return hours.toString().padStart(2, '0') + 'h' + minutes.toString().padStart(2, '0') + 'm' + seconds.toString().padStart(2, '0') + 's';
  }

  private formatBytes(bytes: any): string {

    if (bytes == 0) {
      return '0 Bytes';
    }
    const k = 1024;
    const dm = 2 < 0 ? 0 : 2;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
  }

  processTraces(exchanges: any): void {
    this.traceList = exchanges;
    this.traceList.forEach(exchange => {
      switch (exchange.response.status) {
        case 200:
          this.http200Traces.push(exchange);
          break;

        case 400:
          this.http400Traces.push(exchange);
          break;

        case 404:
          this.http404Traces.push(exchange);
          break;

        case 500:
          this.http500Traces.push(exchange);
          break;

        default:
          this.httpDefaultTraces.push(exchange);
          break;

      }
    });
  }

  formatTimeTaken(timeTaken: string | undefined): string {
    if (timeTaken == null) {
      return "0ms"
    }

    let time: number = parseFloat(timeTaken.replace("PT", "").replace("S", ""));
    let miilisecond: number = time * 1000;

    return `${miilisecond}ms`;

  }

  public onSelectTrace(trace: any): void {
    this.selectedTrace = trace;
    document.getElementById('trace-modal')?.click();

  }

  private updateTime(): void {
    setInterval(() =>{
      this.processUpTime= this.formatUptime(this.timestamp + 1);
      this.timestamp++;
    }, 1000);
  }

}


