import {ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import {AtmService} from "../../atm.service";
import {fromEvent, Subject} from "rxjs/index";
import {debounceTime, distinctUntilChanged, tap} from "rxjs/internal/operators";

@Component({
  selector: 'app-atm-table',
  templateUrl: './atm-table.component.html',
  styleUrls: ['./atm-table.component.styl']
})
export class AtmTableComponent implements OnInit {
  displayedColumns = ['type', "address", "lat", "lng"];
  dataSource = new MatTableDataSource([]);
  totalCount: number = 0;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort, {}) sort: MatSort;
  @ViewChild('atmSearchInput') input: ElementRef;
  public loading$ = new Subject<boolean>();
  filterValue: string;

  constructor(private atmService: AtmService, private cdr: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.dataSource.paginator = this.paginator;

  }

  // TODO:
  onNavigate(id) {
    console.log(`Atm id${id}`)
  }

  ngAfterViewInit() {
    fromEvent(this.input.nativeElement,'keyup')
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap((text) => {
          this.onSearchEvent();
        })
      )
      .subscribe();
  }

  onSearchEvent(e?): void {
    if (this.filterValue !== undefined && this.filterValue!== "") {
      this.loading$.next(true);
      console.log("filter", this.filterValue);
      let pageIndex = this.paginator.pageIndex;
      let pageSize = this.paginator.pageSize;
      if (e != undefined) {
        pageIndex = e.pageIndex;
        pageSize = e.pageSize;
      }
      this.atmService
        .search(this.filterValue, pageIndex, pageSize)
        .subscribe(response => {
          this.dataSource.data = response['atms'];
          this.totalCount = response['count'];
          this.loading$.next(false);
        });
    } else {
      this.dataSource.data = [];
      this.totalCount = 0;
      this.paginator.pageIndex = 0;
      this.paginator.pageSize = 5;
    }
  }

}
