import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable, throwError} from 'rxjs';
import {catchError, retry} from 'rxjs/operators';
import {environment} from './../environments/environment';
import {Atm} from "./model/atm";
import {AuthenticationService} from "./authentication.service";

@Injectable({
  providedIn: 'root'
})
export class AtmService {
  constructor(private http: HttpClient, private authenticationServices: AuthenticationService) {
  }

  search(query: string, page: number, size: number) {
    let url = environment.api.atm.search
      .replace(":query", query)
      .replace(":page", page.toString())
      .replace(":size", size.toString());

    return this.http.get<any>(url);
  }
}
