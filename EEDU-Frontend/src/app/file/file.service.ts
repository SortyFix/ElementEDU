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

    public async uploadFile(url: string, files: File[]) {
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

    public async fetchFile(id: bigint) {
        const response: Response = await fetch(`${(this.URL_PREFIX)}/get/${id}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/octet-stream'
            }
        });

        if(!response.ok) {
            console.error(`Failed to fetch resource with id ${id}`);
        }

        const arrayBuffer = await response.arrayBuffer();
        return new Uint8Array(arrayBuffer);
    }

    public getFileInfo(id: bigint): Observable<FileModel> {
        const url: string = `${this.URL_PREFIX}/get/info/${id}`
        return this.http.get<FileModel>(url, {withCredentials: true});
    }

    public downloadFile(id: bigint) {
        this.getFileInfo(id).subscribe({
            next: (fileModel) => {
                this.fetchFile(id).then((byteArray) => {
                    this.triggerDownload(byteArray, fileModel.fileName);
                })
            }
        })
    }

    public triggerDownload(byteArray: Uint8Array, fileName: string)
    {
        const blob = new Blob([byteArray]);
        const url: string = URL.createObjectURL(blob);

        const anchor: HTMLAnchorElement = document.createElement('a');

        anchor.href = url;
        anchor.download = fileName;
        anchor.click();

        URL.revokeObjectURL(url);
    }
}
