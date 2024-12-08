import {HttpClient, HttpEvent} from "@angular/common/http";
import {FileModel} from "./file-model";
import {Observable} from "rxjs";
import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
/**
 * Used sources:
 * https://blog.angular-university.io/angular-file-upload/
 */
export class FileService {
    URL_PREFIX: string = "/api/v1/file";

    public selectedFiles!: File[] | null;

    constructor(private http: HttpClient) { }

    // TODO REMOVE!!!
    public testUpload()
    {
        this.uploadSelection("http://localhost:8080/api/v1/illness/me/uploadTest");
    }

    // ------------------------------ UPLOAD -----------------------------------
    public uploadSelection(url: string): void {
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
    public async fetchFile(id: bigint): Promise<Uint8Array> {
        console.log("Fetching file binaries...");
        const response: Response = await fetch(`${(this.URL_PREFIX)}/get/${id}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/octet-stream'
            }
        });

        if(!response.ok) {
            console.error(`Failed to fetch resource with id ${id}`);
        }

        const arrayBuffer: ArrayBuffer = await response.arrayBuffer();
        return new Uint8Array(arrayBuffer);
    }

    public getFileInfo(id: bigint): Observable<FileModel> {
        console.log("Fetching file info...");
        const url: string = `${this.URL_PREFIX}/get/info/${id}`
        return this.http.get<FileModel>(url, {withCredentials: true});
    }

    public downloadFile(id: bigint): void {
        this.getFileInfo(id).subscribe({
            next: (fileModel) => {
                console.log("File info received.")
                this.fetchFile(id).then((byteArray) => {
                    this.triggerDownload(byteArray, fileModel.fileName);
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
