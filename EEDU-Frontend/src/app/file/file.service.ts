import {HttpClient, HttpEvent} from "@angular/common/http";
import {FileModel} from "./file-model";
import {Observable} from "rxjs";
import {Injectable} from '@angular/core';
import {environment} from "../../environment/environment";

interface FileResponse {
    blob: Uint8Array;
    fileName: string;
}

@Injectable({
    providedIn: 'root'
})
/**
 * Used sources:
 * https://blog.angular-university.io/angular-file-upload/
 */
export class FileService {
    URL_PREFIX: string = `${environment.backendUrl}/file`;

    public selectedFiles!: File[] | null;

    constructor(private http: HttpClient) { }

    // ------------------------------ UPLOAD -----------------------------------
    public uploadSelection(url: string, additionalData?: { [key: string]: any }): void {
        if(this.selectedFiles){
            this.uploadFiles(url, this.selectedFiles).subscribe({
                error: err => {
                    console.log(err);
                },
                complete: () => {
                    this.reset();
                }
            });
        }
    }

    public selectFiles(event: Event): File[] | null {
        const input = event.target as HTMLInputElement;

        if(input.files && input.files.length > 0) {
            this.selectedFiles = Array.from(input.files);
        }

        return null;
    }

    public uploadFiles(url: string, files: File[], additionalData?: { [key: string]: any }): Observable<HttpEvent<any>>  {
        const formData = new FormData();

        if(additionalData) {
            this.appendKeys(additionalData, formData);
        }

        files.forEach((file: File): void => formData.append('file', file));

        console.log(formData);

        return this.http.post(url, formData, {
            reportProgress: true,
            withCredentials: true,
            observe: 'events'
        });
    }

    public appendKeys(keys: { [key: string]: any }, formData: FormData)
    {
        Object.keys(keys).forEach((key: string): void => {
            formData.append(key, keys[key]);
        });
    }

    public unselectFile(index: number): void {
        this.selectedFiles?.splice(index, 1);
    }

    public reset(): void {
        this.selectedFiles = null;
    }

    // ------------------------------ DOWNLOAD -----------------------------------
    public async fetchFile(id: bigint, index?: number): Promise<FileResponse> {
        console.log("Fetching file binaries...");
        const url: string = index == null ? `${(this.URL_PREFIX)}/get/${id}` : `${(this.URL_PREFIX)}/get/${id}/${index}`
        const response: Response = await fetch(url, {
            method: 'GET',
            headers: {
                'Accept': '*/*'
            },
            credentials: 'include'
        });

        console.log(response.headers.get("Content-Type"));

        if(!response.ok) {
            console.error(`Failed to fetch resource with id ${id}`);
        }

        const arrayBuffer: ArrayBuffer = await response.arrayBuffer();

        return {
            blob: new Uint8Array(arrayBuffer),
            fileName: this.extractFileName(response.headers)
        };
    }

    public getFileInfo(id: bigint): Observable<FileModel> {
        console.log("Fetching file info...");
        const url: string = `${this.URL_PREFIX}/get/info/${id}`
        return this.http.get<FileModel>(url, {withCredentials: true});
    }

    public extractFileName(responseHeaders: Headers): string
    {
        console.log(responseHeaders);
        const disposition: string | null = responseHeaders.get('Content-Disposition');
        console.log(disposition);
        return disposition ? disposition.split('filename=')[1]?.replace(/"/g, '') || 'default-filename' : 'default-filename';
    }

    public downloadFile(id: bigint, index?: number): void {
        this.getFileInfo(id).subscribe({
            next: (fileModel: FileModel) => {
                console.log("File info received.");
                console.log(fileModel);
                this.fetchFile(id, index).then((response: any) => {
                    this.triggerDownload(response.blob, response.fileName);
                })
            }
        })
    }

    public triggerDownload(byteArray: Uint8Array, fileName: string): void
    {
        console.log("Creating blob...");
        const blob = new Blob([byteArray]);
        const url: string = URL.createObjectURL(blob);
        const anchor: HTMLAnchorElement = document.createElement('a');

        anchor.href = url;
        anchor.download = fileName;
        anchor.click();

        console.log("Anchor element executed. Revoking URL...")
        URL.revokeObjectURL(url);
    }
}
