import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {FileModel} from "./file-model";
import {Observable} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class FileService {
    constructor(private http: HttpClient) { }

    URL_PREFIX: string = "/api/v1/file";

    public async uploadFile(url: string, files: File[]): Promise<void> {
        const formData = new FormData();
        files.forEach((file) => formData.append('file[]', file));

        const response = await fetch(url, {
            method: 'POST',
            body: formData
        });

        if(!response.ok)
        {
            throw new Error('File upload failed');
        }
    }

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
                console.log("File info recieved.")
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
