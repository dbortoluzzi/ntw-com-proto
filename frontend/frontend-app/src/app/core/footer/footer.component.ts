import { Component, OnInit } from '@angular/core';
import {AuthenticationService} from "../../authentication.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.styl']
})
export class FooterComponent implements OnInit {

  currentYear: number;

  constructor(private authenticationService: AuthenticationService, private router: Router) { }

  ngOnInit(): void {

    this.currentYear = new Date().getFullYear();

  }

  logout(): void {
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }

  isLogged(): boolean {
    return this.authenticationService.currentUserValue != null;
  }
}
