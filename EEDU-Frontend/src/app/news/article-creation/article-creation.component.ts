import {Component, EventEmitter, inject, Injectable, Output, signal} from '@angular/core';
import {FormBuilder, FormControl, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatStep, MatStepLabel, MatStepper, MatStepperNext} from "@angular/material/stepper";
import {MatFormField} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatLabel} from "@angular/material/form-field";
import {MatButton} from "@angular/material/button";
import {MatHint} from "@angular/material/form-field";
import {FileUploadButtonComponent} from "../../file/file-upload-button/file-upload-button.component";
import {MatChipGrid, MatChipInput, MatChipInputEvent, MatChipRow} from "@angular/material/chips";
import {MatIcon} from "@angular/material/icon";
import {NgForOf} from "@angular/common";
import {HttpClient} from "@angular/common/http";
import {ArticleCreationService} from "./article-creation.service";
import {FileUploadComponent} from "../../common/file-upload/file-upload.component";
import {UserService} from "../../user/user.service";

@Injectable({
    providedIn: 'root'
})
@Component({
  selector: 'app-article-creation',
  standalone: true,
    imports: [
        MatStep,
        MatStepper,
        MatStepLabel,
        MatFormField,
        MatInput,
        FormsModule,
        MatLabel,
        MatButton,
        MatHint,
        FileUploadButtonComponent,
        MatChipRow,
        MatIcon,
        ReactiveFormsModule,
        MatChipGrid,
        MatChipInput,
        NgForOf,
        MatStepperNext,
        FileUploadComponent
    ],
  templateUrl: './article-creation.component.html',
  styleUrl: './article-creation.component.scss'
})
export class ArticleCreationComponent {

    constructor(public http: HttpClient, public articleCreationService: ArticleCreationService, public userService: UserService) {
    }

    private _formBuilder = inject(FormBuilder);

    titleFormGroup = this._formBuilder.group({
        title: ['', Validators.required],
    });
    authorFormGroup = this._formBuilder.group({
        author: ['', Validators.required],
    });
    bodyFormGroup = this._formBuilder.group({
        body: ['', Validators.required],
    });
    publishFormGroup = this._formBuilder.group({
        privilegeFormControl: new FormControl([], Validators.required),
        tagFormControl: new FormControl([], Validators.required),
    });

    get privilegeFormControl() {
        return this.publishFormGroup.get('privilegeFormControl') as FormControl;
    }
    get tagFormControl() {
        return this.publishFormGroup.get('tagFormControl') as FormControl;
    }

    imageFiles: File[] = [];

    @Output() imagesUploaded = new EventEmitter<File[]>();

    onFileSelected(event: Event): void {
        const target = event.target as HTMLInputElement;

        if (target.files) {
            const files: File[] = Array.from(target.files);
            const imageFiles: File[] = files.filter(file => file.type.startsWith('image/'));

            if (imageFiles.length > 0) {
                this.imageFiles = imageFiles;
                this.imagesUploaded.emit(this.imageFiles);
            }
        }
    }

    isLinear = false;

    readonly reactivePrivileges = signal(['ADMIN']);
    readonly reactiveTags = signal(['article']);

    trackByKeyword(index: number, keyword: string): string {
        return keyword;
    }

    removePrivilege(keyword: string) {
        if(keyword != 'ADMIN')
        {
            this.reactivePrivileges.update(keywords => keywords.filter(k => k !== keyword));
        }
    }

    addPrivilege(event: MatChipInputEvent): void {
        const value = (event.value || '').trim();
        if (value) {
            this.reactivePrivileges.update(keywords => [...keywords, value]);
        }
        event.chipInput!.clear();
    }

    removeTag(keyword: string) {
        this.reactiveTags.update(keywords => keywords.filter(k => k !== keyword));
    }

    addTag(event: MatChipInputEvent): void {
        const value = (event.value || '').trim();
        if (value) {
            this.reactiveTags.update(keywords => [...keywords, value]);
        }
        event.chipInput!.clear();
    }

    createArticle()
    {
        let title = this.titleFormGroup.get('title')?.value;
        let author = this.authorFormGroup.get('author')?.value;
        let body = this.bodyFormGroup.get('body')?.value;

        if(!title || !author || !body)
        {
            console.log("Not all required data given.");
            return;
        }

        this.articleCreationService.createArticle({
            author: author,
            title: title,
            thumbnailURL: null,
            body: body,
            editPrivileges: this.reactivePrivileges(),
            tags: this.reactiveTags()
        });
    }
}
