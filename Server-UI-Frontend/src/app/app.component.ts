import { Component, OnInit } from '@angular/core';
import { ServerService } from './service/server.service';
import { AppState } from './interface/app-state';
import { CustomResponse } from './interface/custom-response';
import {
  BehaviorSubject,
  Observable,
  catchError,
  map,
  of,
  startWith,
} from 'rxjs';
import { DataState } from './enum/data-state.enum';
import { Status } from './enum/status.enum';
import { NgForm } from '@angular/forms';
import { Server } from './interface/server';
import { NotificationService } from './service/notification.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  servers: Server[] | undefined = [];
  filterText: string = '';
  showConfirmation: boolean = false;
  serverToDelete?: Server;
  filteredServers: Server[] | undefined = []; // This will hold the filtered results

  server: any;
  isLoadingInstall$ = new BehaviorSubject<boolean>(false);
  appState$: Observable<AppState<CustomResponse>> = of({
    dataState: DataState.LOADING_STATE,
  });

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
    data: { servers: [] },
  });
  filterStatus$ = this.filterSubject.asObservable();

  private isLoading = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoading.asObservable();

  constructor(
    private serverService: ServerService,
    private notifier: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadServers();
  }
  /*  Load Server Function */
  loadServers(): void {
    this.appState$ = this.serverService.servers$.pipe(
      map((response) => {
        this.notifier.onInfo(response.message);
        this.dataSubject.next(response);
        this.servers = response?.data.servers?.reverse();
        this.filteredServers = this.servers;
        return {
          dataState: DataState.LOADED_STATE,
          appData: {
            ...response,
            data: { servers: response.data.servers?.reverse() },
          },
        };
      }),
      startWith({ dataState: DataState.LOADING_STATE }),
      catchError((error: string) => {
        this.notifier.onError(error);
        return of({ dataState: DataState.ERROR_STATE, error: error });
      })
    );
  }
  /*  Ping Server Function */
  pingServer(ipAddr: string): void {
    this.filterSubject.next(ipAddr);
    this.appState$ = this.serverService.ping$(ipAddr).pipe(
      map((response) => {
        this.notifier.onDefault('Pinging server...');
        const index = this.dataSubject.value.data.servers!.findIndex(
          (server) => server.id === response.data.server!.id
        );
        this.dataSubject.value.data.servers![index] = response.data.server!;
        if (response.data.server!.status === Status.SERVER_UP) {
          this.notifier.onSuccess(response.message);
        } else {
          this.notifier.onError(response.message);
        }
        this.filterSubject.next('');
        this.filteredServers = this.dataSubject.value.data.servers; // Update the filteredServers
        return {
          dataState: DataState.LOADED_STATE,
          appData: this.dataSubject.value,
        };
      }),
      startWith({
        dataState: DataState.LOADED_STATE,
        appData: this.dataSubject.value,
      }),
      catchError((error: string) => {
        this.filterSubject.next('');
        this.notifier.onError(error);
        return of({ dataState: DataState.ERROR_STATE, error: error });
      })
    );
  }
  /*  Save Server Function */
  saveServer(serverForm: NgForm): void {
    this.isLoading.next(true);
    this.appState$ = this.serverService.save$(<Server>serverForm.value).pipe(
      map((response) => {
        this.dataSubject.next({
          ...response,
          data: {
            servers: [
              response.data.server!,
              ...this.dataSubject.value.data.servers!,
            ],
          },
        });
        this.notifier.onSuccess(response.message);
        this.filteredServers = this.dataSubject.value.data.servers; // Update the filteredServers
        document.getElementById('closeModal')?.click();
        serverForm.resetForm({ status: this.Status.SERVER_DOWN });
        this.isLoading.next(false);
        return {
          dataState: DataState.LOADED_STATE,
          appData: this.dataSubject.value,
        };
      }),
      startWith({
        dataState: DataState.LOADED_STATE,
        appData: this.dataSubject.value,
      }),
      catchError((error: string) => {
        this.isLoading.next(false);
        this.notifier.onError(error);
        return of({ dataState: DataState.ERROR_STATE, error: error });
      })
    );
  }

  setServerToDelete(server: Server): void {
    this.serverToDelete = server;
    this.showConfirmation = true;
    this.loadServers();
  }
  /*  Confirm Delete Function */
  confirmDelete(): void {
    if (this.serverToDelete) {
      this.deleteServer(this.serverToDelete);
      this.showConfirmation = false;
    }
  }
  /*  Cancel Delete Function */
  cancelDelete(): void {
    this.showConfirmation = false;
    this.serverToDelete = undefined;
  }
  /*  Delete Server Function */
  deleteServer(server: Server): void {
    this.appState$ = this.serverService.delete$(server.id).pipe(
      map((response) => {
        this.dataSubject.next({
          ...response,
          data: {
            servers: this.dataSubject.value.data.servers!.filter(
              (s) => s.id !== server.id
            ),
          },
        });
        this.notifier.onSuccess(response.message);
        this.filteredServers = this.dataSubject.value.data.servers; // Update the filteredServers
        return {
          dataState: DataState.LOADED_STATE,
          appData: this.dataSubject.value,
        };
      }),
      startWith({
        dataState: DataState.LOADED_STATE,
        appData: this.dataSubject.value,
      }),
      catchError((error: string) => {
        this.notifier.onError(error);
        return of({ dataState: DataState.ERROR_STATE, error: error });
      })
    );
  }
  /*  Print table & export it to xls file*/

  printReport(): void {
    this.notifier.onDefault('Printing report...');

    let tableSelect = document.getElementById('servers');
    if (!tableSelect) {
      this.notifier.onError('Table not found!');
      return;
    }

    // Clone the table to manipulate for export
    let clonedTable = tableSelect.cloneNode(true) as HTMLElement;

    // Style the cloned table for Excel export (if needed)
    let styles = `
    <style>
      table {
        width: 100%;
        border-collapse: collapse;
      }
      table, th, td {
        border: 1px solid black;
      }
      th, td {
        padding: 5px;
        text-align: left;
      }
    </style>
  `;

    let tableHTML = `
    <html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">
    <head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>Sheet1</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head>
    <body>${styles}${clonedTable.outerHTML}</body></html>`;

    let dataType = 'application/vnd.ms-excel.sheet.macroEnabled.12';
    let blob = new Blob([tableHTML], { type: dataType });
    let downloadLink = document.createElement('a');

    downloadLink.href = URL.createObjectURL(blob);
    downloadLink.download = 'server-report.xls';
    downloadLink.click();

    this.notifier.onSuccess('Report printed');
  }

  /**/
  setSelectedServer(server: any): void {
    this.server = server;
  }
  /*  Function Install Software */
  installSoftware(form: NgForm): void {
    if (form.invalid) return;

    this.isLoadingInstall$.next(true);
    const selectedSoftware = form.value.software;

    this.serverService
      .installSoftwareOnServer(this.server.id, selectedSoftware)
      .subscribe(
        () => {
          console.log(`Software installed on server: ${this.server.name}`);
          this.updateServerStatus(this.server.id);
          this.isLoadingInstall$.next(false);
          document.getElementById('closeModal')?.click();
        },
        (error) => {
          console.error(
            `Failed to install software on server: ${this.server.name}`,
            error
          );
          this.isLoadingInstall$.next(false);
        }
      );
  }
  /*  Update Server Function */
  updateServerStatus(serverId: string): void {
    console.log(`Server status updated for server ID: ${serverId}`);
  }
  /*  Search Server Function */
  searchServers(): void {
    this.filteredServers = this.servers; // If search text is empty, show all servers
    if (this.filterText.trim() === '') {
    } else {
      console.log('before filter : ' + this.filteredServers);
      // @ts-ignore
      this.filteredServers = this.servers.filter(
        (server) =>
          server.name.toLowerCase().includes(this.filterText.toLowerCase()) ||
          server.ipAddr.includes(this.filterText) ||
          server.memory.toLowerCase().includes(this.filterText.toLowerCase()) ||
          server.type.toLowerCase().includes(this.filterText.toLowerCase()) ||
          server.status.toLowerCase().includes(this.filterText.toLowerCase())
      );
      console.log(' after filter ' + this.filteredServers);
    }
    console.log(this.filterText);
  }
}
