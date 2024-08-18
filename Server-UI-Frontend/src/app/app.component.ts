import { Component, OnInit } from '@angular/core';
import { ServerService } from './service/server.service';
import { AppState } from './interface/app-state';
import { CustomResponse } from './interface/custom-response';
import { BehaviorSubject, Observable, catchError, map, of, startWith } from 'rxjs';
import { DataState } from './enum/data-state.enum';
import { Status } from './enum/status.enum';
import { NgForm } from '@angular/forms';
import { Server } from './interface/server';
import { NotificationService } from './service/notification.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  servers: Server[] = [];
  filterText: string = '';
  showConfirmation: boolean = false;
  serverToDelete?: Server;
  filteredServers: Server[] = [];  // This will hold the filtered results

  server: any;
  isLoadingInstall$ = new BehaviorSubject<boolean>(false);
  appState$: Observable<AppState<CustomResponse>>  = of({ dataState: DataState.LOADING_STATE });

  readonly DataState = DataState;
  readonly Status = Status;

  private filterSubject = new BehaviorSubject<string>('');
  private dataSubject = new BehaviorSubject<CustomResponse>({
    timeStamp: new Date(),
    statusCode: 0,
    status: '',
    reason: '',
    message: '',
    developerMessage: '',
    data: { servers: [] }
  });
  filterStatus$ = this.filterSubject.asObservable();

  private isLoading = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoading.asObservable();

  constructor(private serverService: ServerService, private notifier: NotificationService) {}

  ngOnInit(): void {
    this.appState$ = this.serverService.servers$
      .pipe(
        map(response => {
          this.notifier.onInfo(response.message);
          this.dataSubject.next(response);
          return {
            dataState: DataState.LOADED_STATE,
            appData: { ...response, data: { servers: response.data.servers?.reverse() } }
          };
        }),
        startWith({ dataState: DataState.LOADING_STATE }),
        catchError((error: string) => {
          this.notifier.onError(error);
          return of({ dataState: DataState.ERROR_STATE, error: error });
        })
      );
  }

  pingServer(ipAddr: string): void {
    this.filterSubject.next(ipAddr);
    this.appState$ = this.serverService.ping$(ipAddr)
      .pipe(
        map(response => {
          this.notifier.onDefault("Pinging server...");
          const index = this.dataSubject.value.data.servers!.findIndex(server => server.id === response.data.server!.id);
          this.dataSubject.value.data.servers![index] = response.data.server!;
          if (response.data.server!.status === Status.SERVER_UP) {
            this.notifier.onSuccess(response.message);
          } else {
            this.notifier.onError(response.message);
          }
          this.filterSubject.next('');
          return { dataState: DataState.LOADED_STATE, appData: this.dataSubject.value };
        }),
        startWith({ dataState: DataState.LOADED_STATE, appData: this.dataSubject.value }),
        catchError((error: string) => {
          this.filterSubject.next('');
          this.notifier.onError(error);
          return of({ dataState: DataState.ERROR_STATE, error: error });
        })
      );
  }

  saveServer(serverForm: NgForm): void {
    this.isLoading.next(true);
    this.appState$ = this.serverService.save$(<Server>serverForm.value)
      .pipe(
        map(response => {
          this.dataSubject.next({
            ...response,
            data: { servers: [response.data.server!, ...this.dataSubject.value.data.servers!] }
          });
          this.notifier.onSuccess(response.message);
          document.getElementById('closeModal')?.click();
          serverForm.resetForm({ status: this.Status.SERVER_DOWN });
          this.isLoading.next(false);
          return { dataState: DataState.LOADED_STATE, appData: this.dataSubject.value };
        }),
        startWith({ dataState: DataState.LOADED_STATE, appData: this.dataSubject.value }),
        catchError((error: string) => {
          this.isLoading.next(false);
          this.notifier.onError(error);
          return of({ dataState: DataState.ERROR_STATE, error: error });
        })
      );
  }

  filterServers(status: Status): void {
    this.appState$ = this.serverService.filter$(status, this.dataSubject.value)
      .pipe(
        map(response => {
          this.notifier.onDefault(response.message);
          return { dataState: DataState.LOADED_STATE, appData: response };
        }),
        startWith({ dataState: DataState.LOADED_STATE, appData: this.dataSubject.value }),
        catchError((error: string) => {
          this.notifier.onError(error);
          return of({ dataState: DataState.ERROR_STATE, error: error });
        })
      );
  }

  setServerToDelete(server: Server): void {
    this.serverToDelete = server;
    this.showConfirmation = true;
  }

  confirmDelete(): void {
    if (this.serverToDelete) {
      this.deleteServer(this.serverToDelete);
      this.showConfirmation = false;
    }
  }

  cancelDelete(): void {
    this.showConfirmation = false;
    this.serverToDelete = undefined;
  }

  deleteServer(server: Server): void {
    this.appState$ = this.serverService.delete$(server.id)
      .pipe(
        map(response => {
          this.dataSubject.next({
            ...response,
            data: { servers: this.dataSubject.value.data.servers!.filter(s => s.id !== server.id) }
          });
          this.notifier.onSuccess(response.message);
          return { dataState: DataState.LOADED_STATE, appData: this.dataSubject.value };
        }),
        startWith({ dataState: DataState.LOADED_STATE, appData: this.dataSubject.value }),
        catchError((error: string) => {
          this.notifier.onError(error);
          return of({ dataState: DataState.ERROR_STATE, error: error });
        })
      );
  }

  printReport(): void {
    this.notifier.onDefault('Printing report...');
    let dataType = 'application/vnd.ms-excel.sheet.macroEnabled.12';
    let tableSelect = document.getElementById('servers');
    let tableHTML = tableSelect!.outerHTML.replace(/ /g, '%20');
    let downloadLink = document.createElement('a');
    document.body.appendChild(downloadLink);
    downloadLink.href = 'data:' + dataType + ', ' + tableHTML;
    downloadLink.download = 'server-report.xls';
    downloadLink.click();
    document.body.removeChild(downloadLink);
    this.notifier.onSuccess('Report printed');
  }

  setSelectedServer(server: any): void {
    this.server = server;
  }

  installSoftware(form: NgForm): void {
    if (form.invalid) return;

    this.isLoadingInstall$.next(true);
    const selectedSoftware = form.value.software;

    this.serverService.installSoftwareOnServer(this.server.id, selectedSoftware).subscribe(
      () => {
        console.log(`Software installed on server: ${this.server.name}`);
        this.updateServerStatus(this.server.id);
        this.isLoadingInstall$.next(false);
        document.getElementById('closeModal')?.click();
      },
      (error) => {
        console.error(`Failed to install software on server: ${this.server.name}`, error);
        this.isLoadingInstall$.next(false);
      }
    );
  }

  updateServerStatus(serverId: string): void {
    console.log(`Server status updated for server ID: ${serverId}`);
  }
  /*  Serach keyword code */
  searchServers(): void {
    if (this.filterText.trim() === '') {
      this.filteredServers = this.servers;  // If search text is empty, show all servers
    } else {
      this.filteredServers = this.servers.filter(server =>
        server.name.toLowerCase().includes(this.filterText.toLowerCase()) ||
        server.ipAddr.includes(this.filterText) ||
        server.type.toLowerCase().includes(this.filterText.toLowerCase()) ||
        server.status.toLowerCase().includes(this.filterText.toLowerCase())
      );
      console.log(this.filteredServers);
    }
    console.log(this.filterText);
  }
}
